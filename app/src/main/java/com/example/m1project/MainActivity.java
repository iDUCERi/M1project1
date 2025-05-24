package com.example.m1project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager; // Make sure this is imported

// 1. Implement the LoginDialogListener interface <<< MODIFIED
public class MainActivity extends AppCompatActivity implements LoginDialogFragment.LoginDialogListener {

    EditText newEditTextText;
    Button bt,logBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Make sure this matches your main layout XML file name
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

    // 2. Update goToLogIn to show the dialog <<< MODIFIED
    public void goToLogIn(View view) { // Ensure the parameter is 'View view' if using android:onClick
        FragmentManager fm = getSupportFragmentManager();
        LoginDialogFragment loginDialogFragment = LoginDialogFragment.newInstance();
        // The second argument is a tag, which is optional but can be useful for finding the dialog later
        loginDialogFragment.show(fm, "fragment_login_dialog");
    }

    // 3. Implement the method from LoginDialogListener <<< ADDED
    @Override
    public void onLoginSuccess(String email) {
        // This method will be called when login is successful in the dialog
        Toast.makeText(this, "Login successful for: " + email + " (from Activity)", Toast.LENGTH_LONG).show();
        // TODO: Add your logic here, e.g., navigate to another activity, update UI
        // Example:
        // Intent intent = new Intent(this, UserProfileActivity.class);
        // intent.putExtra("USER_EMAIL", email);
        // startActivity(intent);
        // finish(); // Optional: if you want to close MainActivity after login
    }
}