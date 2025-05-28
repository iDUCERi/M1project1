package com.example.m1project;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.m1project.databinding.ActivityMainPageBinding;

public class MainPage extends AppCompatActivity {

    ActivityMainPageBinding binding;
    private String currentUserEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("USER_EMAIL")) {
            currentUserEmail = intent.getStringExtra("USER_EMAIL");
            Log.d("MainPage", "Received email from Intent: " + currentUserEmail);
        } else {
            Log.w("MainPage", "No email passed via Intent. Status fragment might rely on SharedPreferences or show 'not available'.");

        }

        if (savedInstanceState == null) {

            replaceFragment(new Order_Fragment());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.orders_nav) {
                replaceFragment(new Order_Fragment());
                return true;
            } else if (itemId == R.id.status_nav) {

                replaceFragment(Status_Fragment.newInstance(currentUserEmail, null));
                return true;
            } else if (itemId == R.id.profile_nav) {
                replaceFragment(Profile_Fragment.newInstance());
                return true;
            }
            return false;
        });


        if (savedInstanceState == null && binding.bottomNavigationView.getSelectedItemId() == R.id.status_nav) {
            replaceFragment(Status_Fragment.newInstance(currentUserEmail, null));
        } else if (savedInstanceState == null && binding.bottomNavigationView.getSelectedItemId() == 0 && R.id.orders_nav == binding.bottomNavigationView.getMenu().getItem(0).getItemId()){

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