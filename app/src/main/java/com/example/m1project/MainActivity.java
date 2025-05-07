package com.example.m1project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.checkerframework.common.subtyping.qual.Bottom;

public class MainActivity extends AppCompatActivity {

    EditText newEditTextText;
    Button bt,logBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        newEditTextText=findViewById(R.id.newEditTextText);
        bt=findViewById(R.id.GetLocation);
        logBtn=findViewById(R.id.LogInButton);
    }

    public void GoSignPage(View view) {
        Intent signPage= new Intent(this, MainSignPage.class);
        startActivity(signPage);
    }

    public void why(View view) {
        String fullAdress=newEditTextText.getText().toString();


        Intent signPage= new Intent(this, DelieryAdressActivity.class);
        signPage.putExtra("adress",fullAdress);
        startActivity(signPage);

    }

    public void goToLogIn(View view) {
        CustomAlertDialog customAlertDialog = new CustomAlertDialog(this);
        customAlertDialog.showDialog("dQw4w9WgXcQ");
    }
}