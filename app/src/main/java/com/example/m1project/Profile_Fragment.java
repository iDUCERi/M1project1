package com.example.m1project;

import android.content.Context; // Import Context
import android.content.Intent;
import android.content.SharedPreferences; // Import SharedPreferences
import android.os.Bundle;
import android.util.Log; // For logging
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Profile_Fragment extends Fragment {

    private Button logoutButton;

    // Use the SharedPreferences keys defined in MainActivity
    // It's often better to define these in a separate Constants class
    // if used by multiple classes, but for now, we'll reference them conceptually.
    private static final String PREFS_NAME = "MyPrefsFile"; // Should match MainActivity
    private static final String PREF_REMEMBER_ME = "rememberMe"; // Should match MainActivity
    private static final String PREF_USER_EMAIL = "userEmail"; // Should match MainActivity

    public Profile_Fragment() {
        // Required empty public constructor
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
            // It's good to log if the button isn't found, helps in debugging.
            Log.e("Profile_Fragment", "Button with ID 'button3' not found in fragment_profile_.xml");
        }
    }

    private void performLogout() {
        if (getActivity() == null) {
            // Fragment is not attached to an activity, cannot proceed.
            Log.e("Profile_Fragment", "Fragment not attached to an activity. Cannot perform logout.");
            return;
        }

        // 1. Disconnect from account: Clear "Remember Me" preferences
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Clear the specific preferences used for "Remember Me"
        editor.remove(PREF_REMEMBER_ME);
        editor.remove(PREF_USER_EMAIL);
        // If you had a general "isLoggedIn" flag, you'd clear it too:
        // editor.putBoolean("isLoggedIn", false);

        editor.apply(); // Apply changes

        Log.d("Profile_Fragment", "Remember Me preferences cleared.");

        // 2. Navigate to MainActivity (Login Screen)
        Intent intent = new Intent(getActivity(), MainActivity.class);
        // These flags are crucial:
        // FLAG_ACTIVITY_NEW_TASK: Starts MainActivity in a new task if needed.
        // FLAG_ACTIVITY_CLEAR_TASK: Clears the existing task before MainActivity is started.
        // This makes MainActivity the root of the task stack.
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // 3. Finish the current activity (MainPage)
        // This is important to prevent the user from navigating back to MainPage
        // using the system back button after logging out.
        getActivity().finish();
        Log.d("Profile_Fragment", "Navigated to MainActivity and finished current activity (MainPage).");
    }
}