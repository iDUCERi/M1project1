package com.example.m1project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log; // Import Log
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;


public class MainSignPage extends AppCompatActivity {

    EditText pName, pEmail, pPhone, pPassword, pCity, pPasswordAgain;
    Button b;
    Spinner spinnerTransportType;
    inputValidation iv;
    FirebaseFirestore db;

    private static final String TAG = "MainSignPage";

    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_sign_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        pName = findViewById(R.id.etName);
        pEmail = findViewById(R.id.etEmail);
        pPhone = findViewById(R.id.etPhone);
        pPassword = findViewById(R.id.etPasswords);
        pPasswordAgain = findViewById(R.id.validPassword);
        pCity = findViewById(R.id.etCity);
        b = findViewById(R.id.btnSign);
        spinnerTransportType = findViewById(R.id.spinnerTransportType);

        iv = new inputValidation(pName, pEmail, pPhone, pPassword, pPasswordAgain, pCity);
        db = FirebaseFirestore.getInstance();

        String[] transportTypesArray = getResources().getStringArray(R.array.transport_types);

        final ArrayAdapter<String> transportAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, transportTypesArray) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.parseColor("#F2F2F2"));
                }
                return view;
            }
        };

        transportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransportType.setAdapter(transportAdapter);

        spinnerTransportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (TextView) parent.getChildAt(0);
                if (selectedTextView != null) {
                    if (position == 0) {
                        selectedTextView.setTextColor(Color.GRAY);
                    } else {
                        selectedTextView.setTextColor(Color.parseColor("#F2F2F2"));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private boolean isTransportTypeSelected() {
        if (spinnerTransportType.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Please select a transport type", Toast.LENGTH_SHORT).show();
            TextView errorText = (TextView)spinnerTransportType.getSelectedView();
            if (errorText != null) {
                errorText.setError("");
                errorText.requestFocus();
            }
            return false;
        }
        return true;
    }


    public void SaveToFireBase(View view) {
        final String email = pEmail.getText().toString().trim();
        final String name = pName.getText().toString().trim();
        final String phone = pPhone.getText().toString().trim();
        final String password = pPassword.getText().toString().trim();
        final String city = pCity.getText().toString().trim(); // This is the user input for city
        final String passwordAgain = pPasswordAgain.getText().toString().trim();

        if (!isTransportTypeSelected()) {
            return;
        }

        final String transportType = spinnerTransportType.getSelectedItem().toString();
        final Location currentLocation = null;
        final boolean isAvailable = false;


        if (!iv.checkInput(this) || !iv.checkSecondPassword(this) || !iv.isValidEmail(pEmail) || !iv.isValidPhone(pPhone) || !iv.isValidPassword(pPassword)) {
            return;
        }




        db.collection(FIreBaseHelper.User_collection) // Query the single, comprehensive user collection
                .whereEqualTo(FIreBaseHelper.UserPhone_key, phone)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            pPhone.setError("This phone number is already registered.");
                        } else {
                            proceedWithCityValidationAndSave(name, email, phone, password, passwordAgain, city, transportType, currentLocation, isAvailable);
                        }
                    } else {
                        Log.w(TAG, "Error checking phone in users.", task.getException());
                        Toast.makeText(MainSignPage.this, "Error checking phone availability.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void proceedWithCityValidationAndSave(String name, String email, String phone, String password, String passwordAgain, String cityInput, String transportType, Location currentLocation, boolean isAvailable) {
        iv.isValidCity(new inputValidation.CityValidationListener() {
            @Override
            public void onCityValidationResult(boolean isCityValid, String validatedCityName, String message) {
                if (isCityValid) {
                    FIreBaseHelper.isAccountExist(email, MainSignPage.this, new FIreBaseHelper.AccountExistListener() {
                        @Override
                        public void onAccountExistResult(boolean exists) {
                            if (exists) {
                                pEmail.setError("Account with this email already exists.");
                                Toast.makeText(MainSignPage.this, "Account with this email already exists.", Toast.LENGTH_LONG).show();
                            } else {
                                Delivery newDeliver = new Delivery(name, email, phone, password, passwordAgain, validatedCityName, transportType, currentLocation, isAvailable);
                                FIreBaseHelper.headToFirebase(newDeliver, MainSignPage.this);
                                Intent intent = new Intent(MainSignPage.this, MainPage.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    pCity.setError(message);
                }
            }
        });
    }
}