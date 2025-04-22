package com.example.m1project;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

public class inputValidation {
    private EditText name;
    private EditText email;
    private EditText phone;
    private EditText password;
    private EditText passwordAgain;
    private EditText city;

    public inputValidation(EditText name, EditText email, EditText phone,EditText password,EditText passwordAgain,EditText city)
    {
        this.name=name;
        this.email=email;
        this.phone=phone;
        this.password=password;
        this.passwordAgain=passwordAgain;
        this.city=city;
    }

    public boolean checkInput(Activity activity)
    {
        if (TextUtils.isEmpty(name.getText().toString()) ||
                TextUtils.isEmpty(email.getText().toString()) ||
                TextUtils.isEmpty(password.getText().toString()) ||
                TextUtils.isEmpty(phone.getText().toString()) ||
                TextUtils.isEmpty(passwordAgain.getText().toString()) ||
                TextUtils.isEmpty(city.getText().toString()))
        {
            Toast.makeText(activity, "not all fields are filled", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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

    boolean isValidEmail(EditText email)
    {
        if(!TextUtils.isEmpty(email.getText().toString())&& Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
            return true;
        else{
            email.setError("not valid email");
        }
        return false;
    }

    boolean isValidPhone(EditText phone)
    {
        if(!TextUtils.isEmpty(phone.getText().toString())&& Patterns.PHONE.matcher(phone.getText().toString()).matches())
            return true;
        else{
            phone.setError("not valid email");
        }
        return false;
    }



}
