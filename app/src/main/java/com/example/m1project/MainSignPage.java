package com.example.m1project;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;
import java.util.Map;

public class MainSignPage extends AppCompatActivity {

    TextView pName,pEmail,pPhone,pPassword,pCity;

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

        pName=findViewById(R.id.etName);
        pEmail=findViewById(R.id.etEmail);
        pPhone=findViewById(R.id.etPhone);
        pPassword=findViewById(R.id.etPasswords);
        pCity=findViewById(R.id.etCity);



    }

    public void SaveToFireBase(View view){
        User newUser= new User(pName.getText().toString(),pEmail.getText().toString(),pPhone.getText().toString(),pPassword.getText().toString(),pCity.getText().toString());
        FIreBaseHelper.headToFirebase(newUser,this);
        //Map<String, Object> user = new HashMap<>();
        //user.put("first", "Ada");
        //user.put("last", "Lovelace");
        //user.put("born", 1815);
    }
}