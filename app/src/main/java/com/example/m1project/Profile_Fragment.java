package com.example.m1project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Profile_Fragment extends Fragment {

    private Button logoutButton;


    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_REMEMBER_ME = "rememberMe";
    private static final String PREF_USER_EMAIL = "userEmail";

    public Profile_Fragment() {

    }

    public static Profile_Fragment newInstance() {
        Profile_Fragment fragment = new Profile_Fragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logoutButton = view.findViewById(R.id.button3);

        if (logoutButton != null) {
            logoutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    performLogout();
                }
            });
        } else {

            Log.e("Profile_Fragment", "Button with ID 'button3' not found in fragment_profile_.xml");
        }
    }

    private void performLogout() {
        if (getActivity() == null) {

            Log.e("Profile_Fragment", "Fragment not attached to an activity. Cannot perform logout.");
            return;
        }


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();


        editor.remove(PREF_REMEMBER_ME);
        editor.remove(PREF_USER_EMAIL);


        editor.apply();

        Log.d("Profile_Fragment", "Remember Me preferences cleared.");


        Intent intent = new Intent(getActivity(), MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);


        getActivity().finish();
        Log.d("Profile_Fragment", "Navigated to MainActivity and finished current activity (MainPage).");
    }
}