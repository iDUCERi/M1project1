package com.example.m1project;

import android.annotation.SuppressLint;
import android.graphics.Color; // Import Color
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup; // Import ViewGroup
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull; // Import NonNull
import androidx.annotation.Nullable; // Import Nullable
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

        // Get the transport types from strings.xml
        String[] transportTypesArray = getResources().getStringArray(R.array.transport_types);

        // Custom ArrayAdapter to handle the appearance of the prompt
        final ArrayAdapter<String> transportAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, transportTypesArray) {
            @Override
            public boolean isEnabled(int position) {
                // Disable the first item (prompt)
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textView = (TextView) view;
                if (position == 0) {
                    // Set the color of the prompt item in the dropdown
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.parseColor("#F2F2F2")); // Your normal text color
                }
                return view;
            }
        };

        transportAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTransportType.setAdapter(transportAdapter);

        spinnerTransportType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                TextView selectedTextView = (TextView) parent.getChildAt(0); // Get the selected item view
                if (selectedTextView != null) {
                    if (position == 0) {
                        // If the prompt is selected (e.g., user re-selects it), make it gray
                        selectedTextView.setTextColor(Color.GRAY);
                    } else {
                        // For actual selections, use your desired text color
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
            // Display an error message. A Toast is good.
            // You can also try setting an error on a TextView associated with the Spinner
            // if you have one, or directly on the spinner (though its appearance varies).
            Toast.makeText(this, "Please select a transport type", Toast.LENGTH_SHORT).show();

            // Optionally, try to visually indicate error on the spinner itself
            TextView errorText = (TextView)spinnerTransportType.getSelectedView();
            if (errorText != null) { // Check if a view is selected
                errorText.setError(""); // Setting error to empty string can sometimes trigger the error icon
                // Or set a specific error message if it displays well
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
            // If any of the above checks fail, return immediately
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