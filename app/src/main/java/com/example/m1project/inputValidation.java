package com.example.m1project;

import android.app.Activity;
import android.widget.EditText;
import android.widget.Toast;

public class inputValidation {
    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText passwordAgain;
    private EditText city;

    public inputValidation(EditText name, EditText email, EditText phone,EditText password,EditText city, EditText passwordAgain)
    {
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.password=password;
        this.passwordAgain=passwordAgain;
        this.city=city;
    }

    boolean checkInput(Activity activity)
    {
        if (name.getText().toString().isEmpty()||email.getText().toString().isEmpty()||phone.getText().toString().isEmpty()||password.getText().toString().isEmpty()||city.getText().toString().isEmpty())
            Toast.makeText(activity, "not all fields are filled",Toast.LENGTH_LONG).show();
        return false;
    }

    boolean checkSecondPassword(Activity activity)
    {
        if (password.getText().toString().equals(passwordAgain.getText().toString()))
            return true;
        else {
            Toast.makeText(activity, "the passwrod are not same", Toast.LENGTH_LONG).show();
        }
        return false;
    }

}
