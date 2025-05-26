package com.example.m1project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // <<< ADDED Import for ImageView
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;

public class MainActivity extends AppCompatActivity implements LoginDialogFragment.LoginDialogListener {

    EditText newEditTextText;
    Button bt, logBtn;
    ImageView imageView4; // <<< ADDED ImageView reference

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

        newEditTextText = findViewById(R.id.newEditTextText);
        bt = findViewById(R.id.GetLocation);
        logBtn = findViewById(R.id.LogInButton);
        imageView4 = findViewById(R.id.imageView4); // <<< ADDED Initialize ImageView

        //Animation
        // Set initial state (very small)
        imageView4.setScaleX(0.01f); // Start at 1% of its width
        imageView4.setScaleY(0.01f); // Start at 1% of its height
        imageView4.setAlpha(0f);     // Start fully transparent (optional, but good for smooth appearance)


        // Animate to final state after the layout is established
        imageView4.post(() -> { // Use post to ensure the view has been measured
            imageView4.animate()
                    .scaleX(1f) // Animate to 100% of its original width
                    .scaleY(1f) // Animate to 100% of its original height
                    .alpha(1f)  // Animate to fully opaque (optional)
                    .setDuration(1000) // Duration of the animation in milliseconds (e.g., 1 second)
                    .setStartDelay(300) // Optional: delay before animation starts
                    .start();
        });
        // --- End of Animation Logic ---
    }

    public void GoSignPage(View view) {
        Intent signPage = new Intent(this, MainSignPage.class);
        startActivity(signPage);
    }

    public void why(View view) {
        String fullAdress = newEditTextText.getText().toString();
        Intent signPage = new Intent(this, DelieryAdressActivity.class);
        signPage.putExtra("adress", fullAdress);
        startActivity(signPage);
    }

    public void goToLogIn(View view) {
        FragmentManager fm = getSupportFragmentManager();
        LoginDialogFragment loginDialogFragment = LoginDialogFragment.newInstance();
        loginDialogFragment.show(fm, "fragment_login_dialog");
    }

    @Override
    public void onLoginSuccess(String email) {
        Toast.makeText(this, "Login successful for: " + email + " (from Activity)", Toast.LENGTH_LONG).show();
        // Intent test = new Intent(this, MainLogIn.class);
        // startActivity(test);
    }
}