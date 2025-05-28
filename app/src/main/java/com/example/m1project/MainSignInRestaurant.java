package com.example.m1project;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.firestore.FirebaseFirestore;

public class MainSignInRestaurant extends AppCompatActivity {

    private EditText etName, etEmail, etPhone, etPasswords, validPassword;
    private EditText etFullAddress;
    private EditText etCity;
    private CheckBox cbRememberMe;
    private Button btnSign;

    private inputValidation validator;
    private FirebaseFirestore db;
    private static final String TAG = "MainSignInRestaurant";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_sign_in_restaurant);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        etPasswords = findViewById(R.id.etPasswords);
        validPassword = findViewById(R.id.validPassword);
        etFullAddress = findViewById(R.id.etFullAddress);
        etCity = findViewById(R.id.etCity);
        btnSign = findViewById(R.id.btnSign);

        validator = new inputValidation(etName, etEmail, etPhone, etPasswords, validPassword, etCity);
        db = FirebaseFirestore.getInstance();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_sign_page_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }


    public void SaveToFireBase(View view) {
        if (!validator.checkInput(this) || !validator.checkSecondPassword(this) ||
                !validator.isValidEmail(etEmail) || !validator.isValidPhone(etPhone) ||
                !validator.isValidPassword(etPasswords)) {
            return;
        }

        final String fullAddressStr = etFullAddress.getText().toString().trim();
        if (TextUtils.isEmpty(fullAddressStr)) {
            etFullAddress.setError("Full address is required");
            etFullAddress.requestFocus();
            return;
        }

        final String phoneToValidate = etPhone.getText().toString().trim();
        final String emailToValidate = etEmail.getText().toString().trim();

        db.collection(FIreBaseHelper.User_collection)
                .whereEqualTo(FIreBaseHelper.UserPhone_key, phoneToValidate)
                .get()
                .addOnCompleteListener(taskUserPhone -> {
                    if (taskUserPhone.isSuccessful()) {
                        if (!taskUserPhone.getResult().isEmpty()) {
                            etPhone.setError("This phone number is already registered.");
                        } else {
                            checkAddressAndContinue(fullAddressStr, emailToValidate);
                        }
                    } else {
                        Log.w(TAG, "Error checking phone in users.", taskUserPhone.getException());
                        Toast.makeText(MainSignInRestaurant.this, "Error checking phone availability.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void checkAddressAndContinue(String fullAddressStr, String emailToValidate) {
        db.collection(FIreBaseHelper.Restaurant_collection)
                .whereEqualTo(FIreBaseHelper.RestaurantAdress_key, fullAddressStr)
                .get()
                .addOnCompleteListener(taskAddress -> {
                    if (taskAddress.isSuccessful()) {
                        if (!taskAddress.getResult().isEmpty()) {
                            etFullAddress.setError("This restaurant address is already registered.");
                        } else {
                            validateCityAndSave(emailToValidate, fullAddressStr);
                        }
                    } else {
                        Log.w(TAG, "Error checking address existence.", taskAddress.getException());
                        Toast.makeText(MainSignInRestaurant.this, "Error checking address availability.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void validateCityAndSave(String emailToValidate, String fullAddressStr) {
        validator.isValidCity(new inputValidation.CityValidationListener() {
            @Override
            public void onCityValidationResult(boolean isCityValid, String validatedCityName, String message) {
                if (isCityValid) {
                    checkAccountAndPerformSave(emailToValidate, validatedCityName, fullAddressStr);
                } else {
                    etCity.setError(message);
                    Toast.makeText(MainSignInRestaurant.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void checkAccountAndPerformSave(String email, String validatedCityName, String fullAddress) {
        FIreBaseHelper.isAccountExist(email, this, new FIreBaseHelper.AccountExistListener() {
            @Override
            public void onAccountExistResult(boolean exists) {
                if (exists) {
                    etEmail.setError("An account with this email already exists.");
                    Toast.makeText(MainSignInRestaurant.this, "Account with this email already exists.", Toast.LENGTH_LONG).show();
                } else {
                    performSaveData(validatedCityName, fullAddress, email);
                }
            }
        });
    }


    private void performSaveData(String validatedCity, String fullAddress, String emailForPrefs) {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPasswords.getText().toString().trim();
        String passwordAgain = validPassword.getText().toString().trim();

        Restaurant restaurant = new Restaurant(name, email, phone, password, passwordAgain, validatedCity, fullAddress);


        FIreBaseHelper.headToFirebase(restaurant, MainSignInRestaurant.this);
        


        Toast.makeText(this, "Sign-up successful for: " + name + "! Taking you to the main page.", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(MainSignInRestaurant.this, MainPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}