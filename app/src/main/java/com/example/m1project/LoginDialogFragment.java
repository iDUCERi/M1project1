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
import android.widget.CheckBox; // <<< Import CheckBox
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
    private CheckBox rememberMeCheckbox; // <<< Add CheckBox variable
    private LoginDialogListener listener;

    public interface LoginDialogListener {
        void onLoginSuccess(String email, boolean rememberMe); // <<< Modified interface
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
        View dialogView = inflater.inflate(R.layout.dialog_login, null);

        emailInput = dialogView.findViewById(R.id.etDialogEmail);
        passwordInput = dialogView.findViewById(R.id.etDialogPassword);
        rememberMeCheckbox = dialogView.findViewById(R.id.cbDialogRememberMe); // <<< Initialize CheckBox
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

            emailInput.setError(null);
            passwordInput.setError(null);

            if (TextUtils.isEmpty(email)) {
                emailInput.setError("Email cannot be empty");
                return;
            }
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.setError("Invalid email");
                return;
            }
            if (TextUtils.isEmpty(password)) {
                passwordInput.setError("Password cannot be empty");
                return;
            }

            validateCredentialsWithFirebase(email, password, alertDialog);
        });

        return alertDialog;
    }

    private void validateCredentialsWithFirebase(String email, String password, Dialog dialog) {
        FirebaseFirestore db = FIreBaseHelper.db;

        db.collection(FIreBaseHelper.User_collection)
                .whereEqualTo(FIreBaseHelper.UserEmail_key, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            emailInput.setError("Incorrect password or email");
                            passwordInput.setError("Incorrect password or email");
                        } else {
                            boolean passwordMatch = false;
                            String storedPassword = "";
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                storedPassword = document.getString(FIreBaseHelper.UserPass_key);
                                if (storedPassword != null && storedPassword.equals(password)) {
                                    passwordMatch = true;
                                    break;
                                }
                            }

                            if (passwordMatch) {
                                Toast.makeText(getContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                if (listener != null) {
                                    boolean remember = rememberMeCheckbox.isChecked(); // <<< Get CheckBox state
                                    listener.onLoginSuccess(email, remember); // <<< Pass to listener
                                }
                                dialog.dismiss();
                            } else {
                                emailInput.setError("Invalid email or password");
                                passwordInput.setError("Invalid email or password");
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        emailInput.setError("Login failed. Try again.");
                    }
                });
    }
}