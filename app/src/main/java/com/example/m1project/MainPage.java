package com.example.m1project;

import android.os.Bundle; // Removed unused Intent and SharedPreferences imports for this specific file
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.m1project.databinding.ActivityMainPageBinding;

public class MainPage extends AppCompatActivity {

    ActivityMainPageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainPageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            replaceFragment(new Order_Fragment());
        }

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.orders_nav) {
                replaceFragment(new Order_Fragment());
                return true;
            } else if (itemId == R.id.status_nav) {
                replaceFragment(new Status_Fragment());
                return true;
            } else if (itemId == R.id.profile_nav) {
                replaceFragment(new Profile_Fragment());
                return true;
            }
            return false;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}