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

    EditText pName,pEmail,pPhone,pPassword,pCity;
    Button b;

    Spinner userType;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;

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

        pName=findViewById(R.id.etName);
        pEmail=findViewById(R.id.etEmail);
        pPhone=findViewById(R.id.etPhone);
        pPassword=findViewById(R.id.etPasswords);
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

    public void SaveToFireBase(View view){
        User newUser= new User(pName.getText().toString(),pEmail.getText().toString(),pPhone.getText().toString(),pPassword.getText().toString(),pCity.getText().toString());
        FIreBaseHelper.headToFirebase(newUser,this);
        //Map<String, Object> user = new HashMap<>();
        //user.put("first", "Ada");
        //user.put("last", "Lovelace");
        //user.put("born", 1815);

        //db.collection("users")
                //.add(user)
                //.addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    //@SuppressLint("RestrictedApi")
                    //@Override
                    //public void onSuccess(DocumentReference documentReference) {
                        //Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        //Toast.makeText(view.getContext(),"banana",Toast.LENGTH_LONG).show();
                    //}
                //})
                //.addOnFailureListener(new OnFailureListener() {
                   // @SuppressLint("RestrictedApi")
                    //@Override
                    //public void onFailure(@NonNull Exception e) {
                        //Log.w(TAG, "Error adding document", e);
                    //}
                //});
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position==1&& data.get(position).equals("Deliver"))
        {
            Toast.makeText(this,"deliver",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (position==1&& data.get(position).equals("Deliver"))
        {
            Toast.makeText(this,"deliver",Toast.LENGTH_LONG).show();
            // יצירת פקד EditText בזמן ריצה
            EditText transportType = new EditText(this);
            transportType.setHint("null");
            transportType.setTextColor(Color.BLACK);
            transportType.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));


            // אם אתה רוצה להוסיף את ה-EditText הזה למסך
            LinearLayout layout = findViewById(R.id.main);  // ייתכן ותצטרך להוסיף LinearLayout ב-XML שלך
            layout.addView(transportType);

            //transportType.setPadding();
            //transportType.isEnabled();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}