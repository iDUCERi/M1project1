package com.example.m1project;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;

public class Status_Fragment extends Fragment {

    private static final String ARG_EMAIL = "user_email_arg";
    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String PREF_USER_EMAIL = "userEmail";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001; // For permission request

    TextView textView2;
    Button btToggleAvailability;

    private String displayedEmail;
    private Boolean isCurrentlyAvailable;

    private FusedLocationProviderClient fusedLocationClient;

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

        if (getContext() != null) {
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
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
            fetchCurrentAvailability();
        } else {
            textView2.setText("Email not available.");
            btToggleAvailability.setEnabled(false);
            btToggleAvailability.setText("Status Unavailable");
            Log.w("Status_Fragment", "Email could not be determined.");
        }

        btToggleAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check for location permissions before proceeding
                if (getContext() != null && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                        ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    // Request permissions
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                            LOCATION_PERMISSION_REQUEST_CODE);
                } else {
                    // Permissions are already granted, proceed to toggle availability
                    toggleAvailabilityAndLocation();
                }
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

    // Renamed original toggleAvailability and added location fetching
    private void toggleAvailabilityAndLocation() {
        if (displayedEmail == null || displayedEmail.isEmpty()) {
            Toast.makeText(getContext(), "Email not available.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isCurrentlyAvailable == null) {
            Toast.makeText(getContext(), "Current status unknown. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getContext() == null) {
            Toast.makeText(getContext(), "Context not available. Cannot fetch location.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check for permissions again just in case, though the click listener should handle it
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getContext(), "Location permission not granted.", Toast.LENGTH_SHORT).show();
            // Optionally, call updateFirebaseAvailability with null location if you still want to update status
            // updateFirebaseAvailability(!isCurrentlyAvailable, null);
            return;
        }

        btToggleAvailability.setEnabled(false);
        btToggleAvailability.setText("Updating...");

        // Get current location
        // Using getCurrentLocation for a one-time location update.
        // For frequent updates, you'd use requestLocationUpdates.
        CancellationTokenSource cancellationTokenSource = new CancellationTokenSource();
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancellationTokenSource.getToken())
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (getActivity() == null) return; // Fragment not attached

                        if (location != null) {
                            Log.d("Status_Fragment", "Location fetched: Lat: " + location.getLatitude() + ", Lon: " + location.getLongitude());
                            updateFirebaseAvailability(!isCurrentlyAvailable, location);
                        } else {
                            Log.w("Status_Fragment", "Failed to get location, but proceeding to update availability without it.");
                            Toast.makeText(getContext(), "Could not get current location. Updating status only.", Toast.LENGTH_LONG).show();
                            updateFirebaseAvailability(!isCurrentlyAvailable, null); // Update status even if location is null
                        }
                    }
                })
                .addOnFailureListener(getActivity(), e -> {
                    if (getActivity() == null) return; // Fragment not attached
                    Log.e("Status_Fragment", "Error getting location", e);
                    Toast.makeText(getContext(), "Error getting location: " + e.getMessage() + ". Updating status only.", Toast.LENGTH_LONG).show();
                    updateFirebaseAvailability(!isCurrentlyAvailable, null); // Update status even on location failure
                });
    }


    private void updateFirebaseAvailability(boolean newAvailabilityState, @Nullable Location location) {
        // Ensure displayedEmail is not null here again, though prior checks should cover it.
        if (displayedEmail == null || displayedEmail.isEmpty() || getContext() == null) {
            Toast.makeText(getContext(), "Cannot update: critical data missing.", Toast.LENGTH_SHORT).show();
            btToggleAvailability.setEnabled(true); // Re-enable button
            updateButtonState(); // Reset button text
            return;
        }

        // Assuming FIreBaseHelper.setDeliveryAvailability is modified to accept Location
        FIreBaseHelper.setDeliveryAvailability(displayedEmail, newAvailabilityState, location, getContext(), new FIreBaseHelper.UpdateListener() {
            @Override
            public void onSuccess() {
                if (getActivity() == null) return;
                isCurrentlyAvailable = newAvailabilityState;
                updateButtonState();
                btToggleAvailability.setEnabled(true);
                String locationMessage = (location != null) ? " and location" : "";
                Toast.makeText(getContext(), "Availability" + locationMessage + " updated to " + (newAvailabilityState ? "AVAILABLE" : "UNAVAILABLE"), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Exception e) {
                if (getActivity() == null) return;
                Toast.makeText(getContext(), "Failed to update availability: " + e.getMessage(), Toast.LENGTH_LONG).show();
                // Don't change isCurrentlyAvailable here, as the update failed.
                updateButtonState(); // Revert button text to reflect actual current state
                btToggleAvailability.setEnabled(true);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, proceed with the action
                Toast.makeText(getContext(), "Location permission granted.", Toast.LENGTH_SHORT).show();
                toggleAvailabilityAndLocation(); // Retry the operation
            } else {
                // Permission denied.
                Toast.makeText(getContext(), "Location permission denied. Cannot update location.", Toast.LENGTH_LONG).show();
                // Optionally, you could still update the availability status without location:
                // if (isCurrentlyAvailable != null) {
                //     updateFirebaseAvailability(!isCurrentlyAvailable, null);
                // }
            }
        }
    }
}