package com.example.m1project;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class Status_Fragment extends Fragment {

    private static final String ARG_EMAIL = "user_email_arg";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USER_EMAIL = "userEmail";

    TextView textView2;
    Button btToggleAvailability;

    private String displayedEmail;
    private Boolean isCurrentlyAvailable;

    public Status_Fragment() {
        // Required empty public constructor
    }

    public static Status_Fragment newInstance(String email, @Nullable String param2) {
        Status_Fragment fragment = new Status_Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_EMAIL, email);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            displayedEmail = getArguments().getString(ARG_EMAIL);
            Log.d("Status_Fragment", "Email from arguments: " + displayedEmail);
        }

        if (displayedEmail == null && getContext() != null) {
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            displayedEmail = sharedPreferences.getString(PREF_USER_EMAIL, null);
            Log.d("Status_Fragment", "Email from SharedPreferences: " + displayedEmail);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_status_, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textView2 = view.findViewById(R.id.textView2);
        btToggleAvailability = view.findViewById(R.id.button4);

        if (displayedEmail != null) {
            textView2.setText("Email: " + displayedEmail);
            fetchCurrentAvailability(); //
        } else {
            textView2.setText("Email not available.");
            btToggleAvailability.setEnabled(false);
            btToggleAvailability.setText("Status Unavailable");
            Log.w("Status_Fragment", "Email could not be determined.");
        }

        btToggleAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAvailability();
            }
        });
    }

    private void fetchCurrentAvailability() {
        if (displayedEmail == null || displayedEmail.isEmpty() || getContext() == null) {
            btToggleAvailability.setEnabled(false);
            btToggleAvailability.setText("Status N/A");
            return;
        }

        btToggleAvailability.setEnabled(false);
        btToggleAvailability.setText("Loading Status...");

        FIreBaseHelper.getDeliveryAvailability(displayedEmail, getContext(), new FIreBaseHelper.AvailabilityListener() {
            @Override
            public void onAvailabilityFetched(Boolean isAvailable) {
                if (getActivity() == null) return;
                isCurrentlyAvailable = isAvailable;
                updateButtonState();
                btToggleAvailability.setEnabled(true);
            }

            @Override
            public void onNotFound() {
                if (getActivity() == null) return;
                Toast.makeText(getContext(), "Delivery profile not found for this email.", Toast.LENGTH_LONG).show();
                isCurrentlyAvailable = null;
                btToggleAvailability.setText("Profile Not Found");
                btToggleAvailability.setEnabled(false);
            }

            @Override
            public void onError(Exception e) {
                if (getActivity() == null) return;
                Toast.makeText(getContext(), "Error fetching status: " + e.getMessage(), Toast.LENGTH_LONG).show();
                isCurrentlyAvailable = null;
                btToggleAvailability.setText("Error Fetching Status");
                btToggleAvailability.setEnabled(false);
            }
        });
    }

    private void updateButtonState() {
        if (isCurrentlyAvailable == null) {
            btToggleAvailability.setText("Status Unknown");

        } else if (isCurrentlyAvailable) {
            btToggleAvailability.setText("Set Unavailable");
        } else {
            btToggleAvailability.setText("Set Available");
        }
    }

    private void toggleAvailability() {
        if (displayedEmail == null || displayedEmail.isEmpty()) {
            Toast.makeText(getContext(), "Email not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCurrentlyAvailable == null) {
            Toast.makeText(getContext(), "Current status unknown. Please try again.", Toast.LENGTH_SHORT).show();

            return;
        }

        boolean newAvailabilityState = !isCurrentlyAvailable;

        btToggleAvailability.setEnabled(false);
        btToggleAvailability.setText("Updating...");

        FIreBaseHelper.setDeliveryAvailability(displayedEmail, newAvailabilityState, getContext(), new FIreBaseHelper.UpdateListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) return;
                isCurrentlyAvailable = newAvailabilityState;
                updateButtonState();
                btToggleAvailability.setEnabled(true);
                Toast.makeText(getContext(), "Availability updated to " + (newAvailabilityState ? "AVAILABLE" : "UNAVAILABLE"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() == null) return;
                Toast.makeText(getContext(), "Failed to update availability: " + e.getMessage(), Toast.LENGTH_LONG).show();
                updateButtonState();
                btToggleAvailability.setEnabled(true);
            }
        });
    }
}