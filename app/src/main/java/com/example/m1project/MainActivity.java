package com.example.m1project;

import android.content.Intent;
import android.content.SharedPreferences; // <<< Import SharedPreferences
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
    ImageView imageView4;

    // SharedPreferences keys
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_REMEMBER_ME = "rememberMe";
    private static final String PREF_USER_EMAIL = "userEmail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean shouldRemember = prefs.getBoolean(PREF_REMEMBER_ME, false);
        String rememberedEmail = prefs.getString(PREF_USER_EMAIL, null);

        if (shouldRemember && rememberedEmail != null) {
            Intent intent = new Intent(MainActivity.this, MainPage.class);
            startActivity(intent);
            finish();
            return;
        }

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
        imageView4 = findViewById(R.id.imageView4);


        imageView4.setScaleX(0.01f);
        imageView4.setScaleY(0.01f);
        imageView4.setAlpha(0f);

        imageView4.post(() -> {
            imageView4.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(1000)
                    .setStartDelay(300)
                    .start();
        });
    }

    public void GoSignPage(View view) {
        Intent signPage = new Intent(this, MainChooseType.class);
        startActivity(signPage);
        finish();
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
    public void onLoginSuccess(String email, boolean rememberMe) {
        Toast.makeText(this, "Login successful for: " + email, Toast.LENGTH_LONG).show();

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        if (rememberMe) {
            editor.putBoolean(PREF_REMEMBER_ME, true);
            editor.putString(PREF_USER_EMAIL, email);
        } else {
            // If not remember me, clear the preferences
            editor.remove(PREF_REMEMBER_ME);
            editor.remove(PREF_USER_EMAIL);
        }
        editor.apply();

        Intent intent = new Intent(this, MainPage.class);
        startActivity(intent);
        finish();
    }
}