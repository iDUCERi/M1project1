package com.example.m1project;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginDialogFragment extends DialogFragment {

    private EditText emailInput;
    private EditText passwordInput;
    private LoginDialogListener listener;

    // Interface for communication back to the Activity
    public interface LoginDialogListener {
        void onLoginSuccess(String email); // Or pass user object if needed
        // void onLoginFailed(); // Optional: if activity needs to know about general failure
    }

    public static LoginDialogFragment newInstance() {
        return new LoginDialogFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            listener = (LoginDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement LoginDialogListener");
        }
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_login, null); // We'll create this layout file

        emailInput = dialogView.findViewById(R.id.etDialogEmail);
        passwordInput = dialogView.findViewById(R.id.etDialogPassword);
        Button loginButton = dialogView.findViewById(R.id.btnDialogLogin);

        builder.setView(dialogView)
                .setTitle("Login");
       
        AlertDialog alertDialog = builder.create();

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
            alertDialog.getWindow().setDimAmount(0.6f); 
        }

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            // Reset previous errors
            emailInput.setError(null);
            passwordInput.setError(null);

            //Validate email format
            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email cannot be empty");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Email is not logical");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password cannot be empty");
                return;
            }

            // Proceed to Firebase validation
            validateCredentialsWithFirebase(email, password, alertDialog);
        });

        return alertDialog;
    }

    private void validateCredentialsWithFirebase(String email, String password, Dialog dialog) {
        FirebaseFirestore db = FIreBaseHelper.db; // Use existing instance

        db.collection(FIreBaseHelper.User_collection)
                .whereEqualTo(FIreBaseHelper.UserEmail_key, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // Email does not exist
                            emailInput.setError("Invalid email or password");
                            passwordInput.setError("Invalid email or password");
                        } else {
                            // Email exists, check password
                            boolean passwordMatch = false;
                            String storedPassword = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Assuming only one user per email
                                storedPassword = document.getString(FIreBaseHelper.UserPass_key);
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    passwordMatch = true;
                                    break;
                                }
                            }

                            if (passwordMatch) {
                                // Login successful
                                Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                if (listener != null) {
                                    listener.onLoginSuccess(email);
                                }
                                dialog.dismiss();
                            } else {
                                // Password incorrect
                                emailInput.setError("Invalid email or password");
                                passwordInput.setError("Invalid email or password");
                            }
                        }
                    } else {
                        // Error fetching data
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        emailInput.setError("Login failed. Try again."); // Generic error
                    }
                });
    }
}
