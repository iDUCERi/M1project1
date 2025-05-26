package com.example.m1project;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class inputValidation {
    private final EditText name;
    private final EditText email;
    private final EditText phone;
    private final EditText password;
    private final EditText passwordAgain;
    private final EditText city;

    private static final String TAG = "CityValidation";
    private static final String API_URL_BASE = "https://data.gov.il/api/action/datastore_search";
    private static final String RESOURCE_ID = "b7cf8f14-64a2-4b33-8d4b-edb286fdbd37";

    // Constructor
    public inputValidation(EditText name, EditText email, EditText phone, EditText password, EditText passwordAgain, EditText city) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordAgain = passwordAgain;
        this.city = city;
    }


    public interface CityValidationListener {
        void onCityValidationResult(boolean isValid, String cityInput, String message);
    }


    public boolean checkInput(Activity activity) {
        if (TextUtils.isEmpty(name.getText().toString()) ||
                TextUtils.isEmpty(email.getText().toString()) ||
                TextUtils.isEmpty(password.getText().toString()) ||
                TextUtils.isEmpty(phone.getText().toString()) ||
                TextUtils.isEmpty(passwordAgain.getText().toString()) ||
                TextUtils.isEmpty(this.city.getText().toString())) { // Use this.city
            Toast.makeText(activity, "Not all fields are filled", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    boolean checkSecondPassword(Activity activity) {
        if (password.getText().toString().equals(passwordAgain.getText().toString())) {
            return true;
        } else {
            passwordAgain.setError("Passwords do not match");
            Toast.makeText(activity, "The passwords are not the same", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    boolean isValidEmail(EditText emailInput) {
        String emailStr = emailInput.getText().toString().trim();
        String errorMessage = "Not a valid email. Must end with @gmail.com, @yahoo.com, or @hotmail.com.";

        if (TextUtils.isEmpty(emailStr)) {
            emailInput.setError("Email cannot be empty.");
            return false;
        }

        String emailRegex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@(?:gmail\\.com|yahoo\\.com|hotmail\\.com)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(emailStr);

        if (matcher.matches()) {
            emailInput.setError(null);
            return true;
        } else {
            emailInput.setError(errorMessage);
            return false;
        }
    }

    boolean isValidPhone(EditText phoneInput) {
        String phoneStr = phoneInput.getText().toString().trim();
        String israelPhoneRegex = "^0(5[0-9]{1}|[23489]{1})[ -]?[0-9]{7}$";


        if (TextUtils.isEmpty(phoneStr)) {
            // Caught by checkInput
            phoneInput.setError("Phone number cannot be empty.");
            return false;
        }

        if (phoneStr.matches(israelPhoneRegex)) {
            phoneInput.setError(null);
            return true;
        } else {
            phoneInput.setError("Invalid Israeli phone number format (e.g., 05X-XXXXXXX or 0X-XXXXXXX).");
            return false;
        }
    }

    public boolean isValidPassword(EditText passwordInput) {
        String passStr = passwordInput.getText().toString();
        String errorMessage = "Password must be at least 10 characters, include at least one uppercase letter, one symbol (e.g., @, #, $), and no spaces.";

        if (passStr.contains(" ")) {
            passwordInput.setError(errorMessage);
            return false;
        }
        if (passStr.length() < 10) {
            passwordInput.setError(errorMessage);
            return false;
        }
        Pattern uppercasePattern = Pattern.compile("[A-Z]");
        Matcher uppercaseMatcher = uppercasePattern.matcher(passStr);
        if (!uppercaseMatcher.find()) {
            passwordInput.setError(errorMessage);
            return false;
        }
        Pattern symbolPattern = Pattern.compile("[^a-zA-Z0-9\\s]");
        Matcher symbolMatcher = symbolPattern.matcher(passStr);
        if (!symbolMatcher.find()) {
            passwordInput.setError(errorMessage);
            return false;
        }
        passwordInput.setError(null);
        return true;
    }


    public void isValidCity(final CityValidationListener listener) {
        final String cityToValidate = city.getText().toString().trim();

        if (TextUtils.isEmpty(cityToValidate)) {
            city.setError("City cannot be empty.");
            if (listener != null) {

                new Handler(Looper.getMainLooper()).post(() ->
                        listener.onCityValidationResult(false, cityToValidate, "City cannot be empty.")
                );
            }
            return;
        }


        new Thread(() -> { //
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            boolean cityFound = false;
            String feedbackMessage = "";

            try {

                String encodedCityName = URLEncoder.encode(cityToValidate, "UTF-8");
                String queryParams = "resource_id=" + RESOURCE_ID + "&q=" + encodedCityName + "&limit=50";

                URL url = new URL(API_URL_BASE + "?" + queryParams);
                Log.d(TAG, "Requesting URL: " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                int responseCode = urlConnection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        result.append(line).append("\n");
                    }
                    bufferedReader.close();

                    Log.d(TAG, "API Response: " + result.toString());


                    JSONObject jsonResponse = new JSONObject(result.toString());
                    JSONObject resultObject = jsonResponse.getJSONObject("result");
                    JSONArray records = resultObject.getJSONArray("records");


                    String cityFieldName = "שם_ישוב";

                    for (int i = 0; i < records.length(); i++) {
                        JSONObject record = records.getJSONObject(i);
                        if (record.has(cityFieldName)) {
                            String apiCityName = record.getString(cityFieldName).trim();
                            if (apiCityName.equalsIgnoreCase(cityToValidate)) {
                                cityFound = true;
                                break;
                            }
                        }
                    }

                    if (cityFound) {
                        feedbackMessage = "City '" + cityToValidate + "' is valid.";
                    } else {
                        feedbackMessage = "City '" + cityToValidate + "' not found in the list.";

                    }
                } else {
                    feedbackMessage = "Error fetching city data: " + responseCode;
                    Log.e(TAG, "API Error Response: " + responseCode + " " + urlConnection.getResponseMessage());
                    // Read error stream if available
                    BufferedReader errorReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                    String errorLine;
                    StringBuilder errorResult = new StringBuilder();
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResult.append(errorLine);
                    }
                    errorReader.close();
                    Log.e(TAG, "Error details: " + errorResult.toString());
                }

            } catch (Exception e) {
                Log.e(TAG, "Error during city validation", e);
                feedbackMessage = "Exception during city validation: " + e.getMessage();
                cityFound = false;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }

                final boolean finalCityFound = cityFound;
                final String finalFeedbackMessage = feedbackMessage;
                if (listener != null) {
                    new Handler(Looper.getMainLooper()).post(() ->
                            listener.onCityValidationResult(finalCityFound, cityToValidate, finalFeedbackMessage)
                    );
                }
            }
        }).start();
    }

}