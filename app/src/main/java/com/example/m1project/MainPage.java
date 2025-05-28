package com.example.m1project;

import android.content.Intent; // <<< Import Intent
import android.os.Bundle;
import android.util.Log; // For logging

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.m1project.databinding.ActivityMainPageBinding;

public class MainPage extends AppCompatActivity {

    ActivityMainPageBinding binding;
    private String currentUserEmail; // To store the email for the current session

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get email passed from the previous Activity (e.g., MainActivity after login)
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_EMAIL")) {
            currentUserEmail = intent.getStringExtra("USER_EMAIL");
            Log.d("MainPage", "Received email from Intent: " + currentUserEmail);
        } else {
            Log.w("MainPage", "No email passed via Intent. Status fragment might rely on SharedPreferences or show 'not available'.");
            // If email isn't passed via intent, Status_Fragment will try SharedPreferences
            // Or, if you use Firebase Auth, you could try getting it here:
            // FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // if (user != null) { currentUserEmail = user.getEmail(); }
        }

        if (savedInstanceState == null) {
            // Pass email if available, otherwise pass null
            // Assuming Order_Fragment also has a newInstance that can handle null or no params
            replaceFragment(new Order_Fragment()); // Modify if Order_Fragment needs email
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.orders_nav) {
                replaceFragment(new Order_Fragment()); // Modify if Order_Fragment needs email
                return true;
            } else if (itemId == R.id.status_nav) {
                // Pass the currentUserEmail to Status_Fragment
                replaceFragment(Status_Fragment.newInstance(currentUserEmail, null));
                return true;
            } else if (itemId == R.id.profile_nav) {
                replaceFragment(Profile_Fragment.newInstance());
                return true;
            }
            return false;
        });

        // If app starts and default tab is Status, ensure email is passed
        // This handles initial load if status_nav is the default selected item.
        if (savedInstanceState == null && binding.bottomNavigationView.getSelectedItemId() == R.id.status_nav) {
            replaceFragment(Status_Fragment.newInstance(currentUserEmail, null));
        } else if (savedInstanceState == null && binding.bottomNavigationView.getSelectedItemId() == 0 && R.id.orders_nav == binding.bottomNavigationView.getMenu().getItem(0).getItemId()){
            // If no item is selected by default, and orders_nav is the first item, load it.
            // This might be the case if you don't set app:startDestination in your nav graph or a default selected item.
            replaceFragment(Order_Fragment.newInstance(currentUserEmail, null));
        }

    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}