package com.example.m1project;

import static androidx.fragment.app.FragmentManager.TAG;
import static com.example.m1project.FIreBaseHelper.db;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainSignPage extends AppCompatActivity {

    EditText pName,pEmail,pPhone,pPassword,pCity,pPasswordAgain;
    Button b;

    Spinner userType;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;
   static boolean isDeliver;

    //LinearLayout msp;

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

        isDeliver=false;

        pName=findViewById(R.id.etName);
        pEmail=findViewById(R.id.etEmail);
        pPhone=findViewById(R.id.etPhone);
        pPassword=findViewById(R.id.etPasswords);
        pPasswordAgain=findViewById(R.id.validPassword);
        pCity=findViewById(R.id.etCity);
        //msp= findViewById(R.id.main);

        b = findViewById(R.id.btnSign);

    }

//    public void SaveToFireBase(View view) {
//        Log.d("pass", "pass");
//        String email = pEmail.getText().toString();
//        User newUser = new User(pName.getText().toString(), email, pPhone.getText().toString(), pPassword.getText().toString(), pCity.getText().toString(), pPasswordAgain.getText().toString());
//        inputValidation iv = new inputValidation(pName, pEmail, pPhone, pPassword, pPasswordAgain, pCity);
//        if (iv.checkInput(this) && iv.checkSecondPassword(this) && iv.isValidEmail(pEmail) && iv.isValidPhone(pPhone) && iv.isValidPassword(pPassword)) {
//            // Check if the account exists before proceeding
//            FIreBaseHelper.isAccountExist(email, this, new FIreBaseHelper.AccountExistListener() {
//                @Override
//                public void onAccountExistResult(boolean exists) {
//                    if (exists) {
//                        // Account already exists
//                        Toast.makeText(MainSignPage.this, "Account with this email already exists.", Toast.LENGTH_LONG).show();
//                    } else {
//                        // Account doesn't exist, proceed to save
//                        FIreBaseHelper.headToFirebase(newUser, MainSignPage.this);
//                    }
//                }
//            });
//        }
//    }

    public void SaveToFireBase(View view) {
        String email = pEmail.getText().toString().trim();
        String name = pName.getText().toString().trim();
        String phone = pPhone.getText().toString().trim();
        String password = pPassword.getText().toString().trim();
        String city = pCity.getText().toString().trim(); // Get city text
        String passwordAgain = pPasswordAgain.getText().toString().trim();

        User newUser = new User(name, email, phone, password, city, passwordAgain);

        inputValidation iv = new inputValidation(pName, pEmail, pPhone, pPassword, pPasswordAgain, pCity);


        if (!iv.checkInput(this)) {
            return;
        }
        if (!iv.checkSecondPassword(this)) {
            return;
        }
        if (!iv.isValidEmail(pEmail)) {
            return;
        }
        if (!iv.isValidPhone(pPhone)) {
            return;
        }
        if (!iv.isValidPassword(pPassword)) {
            return;
        }

        iv.isValidCity(new inputValidation.CityValidationListener() {
            @Override
            public void onCityValidationResult(boolean isCityValid, String validatedCity, String message) {
                if (isCityValid) {
                    FIreBaseHelper.isAccountExist(email, MainSignPage.this, new FIreBaseHelper.AccountExistListener() {
                        @Override
                        public void onAccountExistResult(boolean exists) {
                            if (exists) {

                                Toast.makeText(MainSignPage.this, "Account with this email already exists.", Toast.LENGTH_LONG).show();
                            } else {

                                FIreBaseHelper.headToFirebase(newUser, MainSignPage.this);
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

    //public void SaveToFireBase(View view){
        //Log.d("pass","pass");
        //User newUser= new User(pName.getText().toString(),pEmail.getText().toString(),pPhone.getText().toString(),pPassword.getText().toString(),pPasswordAgain.getText().toString(),pCity.getText().toString());
        //inputValidation iv = new inputValidation(pName,pEmail,pPhone,pPassword,pPasswordAgain,pCity);
       //if(iv.checkInput(this)&&iv.checkSecondPassword(this)&&iv.isValidEmail(pEmail)&&iv.isValidPhone(pPhone)) {
       //    FIreBaseHelper.headToFirebase(newUser, this);
     //   }
   // }

}