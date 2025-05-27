package com.example.m1project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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

public class MainSignPage extends AppCompatActivity {

    EditText pName, pEmail, pPhone, pPassword, pCity, pPasswordAgain;
    Button b;
    Spinner spinnerTransportType;

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
        String email = pEmail.getText().toString().trim();
        String name = pName.getText().toString().trim();
        String phone = pPhone.getText().toString().trim();
        String password = pPassword.getText().toString().trim();
        String city = pCity.getText().toString().trim();
        String passwordAgain = pPasswordAgain.getText().toString().trim();

        if (!isTransportTypeSelected()) {
            return;
        }

        String transportType = spinnerTransportType.getSelectedItem().toString();
        Location currentLocation = null;
        boolean isAvailable = false;

        Delivery newDeliver = new Delivery(name, email, phone, password, passwordAgain, city, transportType, currentLocation, isAvailable);
        inputValidation iv = new inputValidation(pName, pEmail, pPhone, pPassword, pPasswordAgain, pCity);

        if (!iv.checkInput(this) || !iv.checkSecondPassword(this) || !iv.isValidEmail(pEmail) || !iv.isValidPhone(pPhone) || !iv.isValidPassword(pPassword)) {
            return;
        }

        iv.isValidCity(new inputValidation.CityValidationListener() {
            @Override
            public void onCityValidationResult(boolean isCityValid, String validatedCity, String message) {
                if (isCityValid) {
                    newDeliver.setCity(validatedCity);

                    FIreBaseHelper.isAccountExist(email, MainSignPage.this, new FIreBaseHelper.AccountExistListener() {
                        @Override
                        public void onAccountExistResult(boolean exists) {
                            if (exists) {
                                Toast.makeText(MainSignPage.this, "Account with this email already exists.", Toast.LENGTH_LONG).show();
                            } else {
                                FIreBaseHelper.headToFirebase(newDeliver, MainSignPage.this);
                                Intent intent = new Intent(MainSignPage.this, MainPage.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                } else {
                    pCity.setError(message);
                    Toast.makeText(MainSignPage.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}