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

public class MainSignPage extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemSelectedListener {

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

        userType= findViewById(R.id.userType);
        data= new ArrayList<String>();
            data.add("Choose account type");
            data.add("Deliver");
            data.add("Restaurant");

        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, data);
        userType.setAdapter(adapter);
        userType.setOnItemSelectedListener(this);

    }

    public void SaveToFireBase(View view) {
        Log.d("pass", "pass");
        String email = pEmail.getText().toString();
        User newUser = new User(pName.getText().toString(), email, pPhone.getText().toString(), pPassword.getText().toString(), pPasswordAgain.getText().toString(), pCity.getText().toString());
        inputValidation iv = new inputValidation(pName, pEmail, pPhone, pPassword, pPasswordAgain, pCity);
        if (iv.checkInput(this) && iv.checkSecondPassword(this) && iv.isValidEmail(pEmail) && iv.isValidPhone(pPhone)) {
            // Check if the account exists before proceeding
            FIreBaseHelper.isAccountExist(email, this, new FIreBaseHelper.AccountExistListener() {
                @Override
                public void onAccountExistResult(boolean exists) {
                    if (exists) {
                        // Account already exists
                        Toast.makeText(MainSignPage.this, "Account with this email already exists.", Toast.LENGTH_LONG).show();
                    } else {
                        // Account doesn't exist, proceed to save
                        FIreBaseHelper.headToFirebase(newUser, MainSignPage.this);
                    }
                }
            });
        }
    }

    //public void SaveToFireBase(View view){
        //Log.d("pass","pass");
        //User newUser= new User(pName.getText().toString(),pEmail.getText().toString(),pPhone.getText().toString(),pPassword.getText().toString(),pPasswordAgain.getText().toString(),pCity.getText().toString());
        //inputValidation iv = new inputValidation(pName,pEmail,pPhone,pPassword,pPasswordAgain,pCity);
       //if(iv.checkInput(this)&&iv.checkSecondPassword(this)&&iv.isValidEmail(pEmail)&&iv.isValidPhone(pPhone)) {
       //    FIreBaseHelper.headToFirebase(newUser, this);
     //   }
   // }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position==1&& data.get(position).equals("Deliver"))
        {
            Toast.makeText(this,"deliver",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // אם אתה רוצה להוסיף את ה-EditText הזה למסך
        LinearLayout layout = findViewById(R.id.main);  // ייתכן ותצטרך להוסיף LinearLayout ב-XML שלך
        EditText transportType = new EditText(this);

        if (position==1&& data.get(position).equals("Deliver") && isDeliver==false)
        {
            isDeliver=true;
            Toast.makeText(this,"deliver",Toast.LENGTH_LONG).show();
            // יצירת פקד EditText בזמן ריצה
            //EditText transportType = new EditText(this);
            transportType.setHint("irelevent for now");
            transportType.setTextColor(Color.BLACK);
            transportType.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            layout.addView(transportType);

            if (position==2&& data.get(position).equals("Restaurant"))
            {
                isDeliver=false;
                if (transportType != null) {
                    layout.removeView(transportType);
                    transportType = null; // Reset the reference
                }
            }

            // אם אתה רוצה להוסיף את ה-EditText הזה למסך
            //LinearLayout layout = findViewById(R.id.main);  // ייתכן ותצטרך להוסיף LinearLayout ב-XML שלך
            //layout.addView(transportType);

            //transportType.setPadding();
            //transportType.isEnabled();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}