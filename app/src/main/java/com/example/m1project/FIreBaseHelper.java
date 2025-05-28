package com.example.m1project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.location.Location; // <-- Import Location
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable; // <-- Import Nullable for the location parameter

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint; // <-- Import GeoPoint if you prefer to store location that way
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FIreBaseHelper {
    public static final String User_collection = "Users";
    public static final String Delivery_collection = "DeliveryC";
    public static final String Restaurant_collection = "RestaurantC";

    public static final String UserName_key = "User_name";
    public static final String UserEmail_key = "User_email";
    public static final String UserCity_key = "User_city";
    public static final String UserPass_key = "User_pass";
    public static final String UserPhone_key = "User_phone";

    public static final String RestaurantAdress_key = "Restaurant_adress";

    public static final String TransportType_key = "Transport_type";

    // Define keys for location data if they are different from what your Delivery class uses
    // For this example, let's assume your Delivery documents will store 'latitude' and 'longitude'
    // or a single 'locationGeoPoint' field of type GeoPoint.
    public static final String DeliverLatitude_key = "latitude"; // Example key
    public static final String DeliverLongitude_key = "longitude"; // Example key
    public static final String DeliverLocationGeoPoint_key = "locationGeoPoint"; // Example key for GeoPoint

    // Keep your original DeliverLocation_key if it serves a different purpose or if your Delivery class
    // already handles a complex Location object directly (which is less common for Firestore).
    // For simplicity with primitive types or GeoPoint in Firestore, the keys above are often used.
    public static final String DeliverLocation_key = "Deliver_Location"; // Original, might be a custom object string
    public static final String IsDeliverAvailable_key = "Is_Deliver_Available"; // Corrected variable name to match usage

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();

    private static Map<String, Object> prepareData2Save(User user) {
        Map<String, Object> userHashMaps = new HashMap<>();
        userHashMaps.put(FIreBaseHelper.UserCity_key, user.getCity());
        userHashMaps.put(FIreBaseHelper.UserEmail_key, user.getEmail());
        userHashMaps.put(FIreBaseHelper.UserPass_key, user.getPassword());
        userHashMaps.put(FIreBaseHelper.UserName_key, user.getName());
        userHashMaps.put(FIreBaseHelper.UserPhone_key, user.getPhone());
        return userHashMaps;
    }

    private static Map<String, Object> prepareData2Save(Restaurant restaurant) {
        Map<String, Object> userHashMaps = new HashMap<>();
        userHashMaps.put(FIreBaseHelper.UserCity_key, restaurant.getCity());
        userHashMaps.put(FIreBaseHelper.UserEmail_key, restaurant.getEmail());
        userHashMaps.put(FIreBaseHelper.UserPass_key, restaurant.getPassword());
        userHashMaps.put(FIreBaseHelper.UserName_key, restaurant.getName());
        userHashMaps.put(FIreBaseHelper.UserPhone_key, restaurant.getPhone());
        userHashMaps.put(FIreBaseHelper.RestaurantAdress_key, restaurant.getAdress());
        return userHashMaps;
    }

    private static Map<String, Object> prepareData2Save(Delivery deliver) {
        Map<String, Object> deliverData = new HashMap<>(); // Renamed for clarity
        deliverData.put(FIreBaseHelper.UserCity_key, deliver.getCity());
        deliverData.put(FIreBaseHelper.UserEmail_key, deliver.getEmail());
        deliverData.put(FIreBaseHelper.UserPass_key, deliver.getPassword());
        deliverData.put(FIreBaseHelper.UserName_key, deliver.getName());
        deliverData.put(FIreBaseHelper.UserPhone_key, deliver.getPhone());
        deliverData.put(FIreBaseHelper.TransportType_key, deliver.getTransportType());
        deliverData.put(FIreBaseHelper.IsDeliverAvailable_key, deliver.getIsAvailable()); // Corrected key usage

        // Handle Location:
        // How you store location depends on your Delivery class and Firestore structure.
        // Option A: Store latitude and longitude as separate fields (common)
        if (deliver.getLocation() != null) {
            deliverData.put(DeliverLatitude_key, deliver.getLocation().getLatitude());
            deliverData.put(DeliverLongitude_key, deliver.getLocation().getLongitude());
        } else {
            // Decide how to handle null location during initial save
            deliverData.put(DeliverLatitude_key, null); // Or some default
            deliverData.put(DeliverLongitude_key, null); // Or some default
        }

        // Option B: Store as a Firestore GeoPoint (recommended for geoqueries)
        // if (deliver.getLocation() != null) {
        //     deliverData.put(DeliverLocationGeoPoint_key, new GeoPoint(deliver.getLocation().getLatitude(), deliver.getLocation().getLongitude()));
        // } else {
        //     deliverData.put(DeliverLocationGeoPoint_key, null);
        // }

        // Option C: If deliver.getLocation() returns a String or complex object that Firestore can't directly map,
        // you might need to reconsider. The original code had:
        // deliverData.put(FIreBaseHelper.DeliverLocation_key, deliver.getLocation());
        // This is fine if deliver.getLocation() returns something Firestore can serialize (like a String or another Map).
        // If it's an android.location.Location object, Firestore will try to serialize it, which might not be what you want.
        // For the sake of the setDeliveryAvailability modification, we'll assume latitude/longitude fields.

        return deliverData;
    }


    public static void headToFirebase(Object object, Context context) {
        Map<String, Object> data; // No need to initialize here

        // Corrected logic for instanceof checks
        if (object instanceof Delivery) { // Check most specific first
            Delivery delivery = (Delivery) object;
            data = prepareData2Save(delivery);
            db.collection(Delivery_collection).add(data)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Delivery added with ID: " + documentReference.getId());
                        Toast.makeText(context, "Delivery account created", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error adding delivery", e);
                        Toast.makeText(context, "Error creating delivery account: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        } else if (object instanceof Restaurant) {
            Restaurant restaurant = (Restaurant) object;
            data = prepareData2Save(restaurant);
            db.collection(Restaurant_collection).add(data)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "Restaurant added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding restaurant", e));
        } else if (object instanceof User) { // General user last
            User user = (User) object;
            data = prepareData2Save(user);
            db.collection(User_collection).add(data)
                    .addOnSuccessListener(documentReference -> Log.d(TAG, "User added with ID: " + documentReference.getId()))
                    .addOnFailureListener(e -> Log.w(TAG, "Error adding user", e));
        } else {
            Log.e(TAG, "Unsupported object type for headToFirebase: " + object.getClass().getName());
        }
    }

    public static void isDeliveryAccountExist(String email, Context context, final AccountExistListener listener) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "Email is null or empty. Cannot check delivery account existence.");
            if (listener != null) listener.onAccountExistResult(false);
            return;
        }
        db.collection(Delivery_collection)
                .whereEqualTo(UserEmail_key, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onAccountExistResult(!task.getResult().isEmpty());
                    } else {
                        Log.w(TAG, "Error checking delivery account existence for email: " + email, task.getException());
                        Toast.makeText(context, "Error checking delivery account.", Toast.LENGTH_LONG).show();
                        listener.onAccountExistResult(false);
                    }
                });
    }

    public static void isAccountExist(String email, Context context, final AccountExistListener listener) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "Email is null or empty. Cannot check user account existence.");
            if (listener != null) listener.onAccountExistResult(false);
            return;
        }
        db.collection(User_collection)
                .whereEqualTo(UserEmail_key, email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listener.onAccountExistResult(!task.getResult().isEmpty());
                    } else {
                        Log.w(TAG, "Error checking user account existence.", task.getException());
                        Toast.makeText(context, "Error checking user account.", Toast.LENGTH_LONG).show();
                        listener.onAccountExistResult(false);
                    }
                });
    }

    public interface AccountExistListener {
        void onAccountExistResult(boolean exists);
    }

    public static void setDeliveryAvailability(String email, boolean isAvailable, @Nullable Location location, Context context, final UpdateListener listener) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "Email is null or empty. Cannot update delivery availability.");
            if (listener != null) listener.onFailure(new IllegalArgumentException("Email cannot be null or empty"));
            return;
        }

        db.collection(Delivery_collection)
                .whereEqualTo(UserEmail_key, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                String message = "No delivery account found with email: " + email;
                                Log.w(TAG, message);
                                if (listener != null) listener.onFailure(new Exception(message));
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference docRef = db.collection(Delivery_collection).document(document.getId());
                                Map<String, Object> updates = new HashMap<>();
                                updates.put(IsDeliverAvailable_key, isAvailable); // Use IsDeliverAvailable_key consistently


                                if (location != null) {
                                    // Option A: Store as separate latitude/longitude fields
                                    updates.put(DeliverLatitude_key, location.getLatitude());
                                    updates.put(DeliverLongitude_key, location.getLongitude());

                                } else {

                                    // updates.put(DeliverLatitude_key, null);
                                    // updates.put(DeliverLongitude_key, null);
                                    // updates.put(DeliverLocationGeoPoint_key, null);
                                }

                                docRef.update(updates) // Update with the map of changes
                                        .addOnSuccessListener(aVoid -> {
                                            String locMsg = (location != null) ? " and location" : "";
                                            Log.d(TAG, "Delivery availability" + locMsg + " successfully updated for " + email + " to " + isAvailable);
                                            if (listener != null) listener.onSuccess();
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.w(TAG, "Error updating delivery availability for " + email, e);
                                            if (listener != null) listener.onFailure(e);
                                        });
                                break; // Process only the first matching document (assuming email is unique)
                            }
                        } else {
                            Log.w(TAG, "Error getting documents to update availability: ", task.getException());
                            if (listener != null) listener.onFailure(task.getException());
                        }
                    }
                });
    }


    public interface UpdateListener {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void getDeliveryAvailability(String email, Context context, final AvailabilityListener listener) {
        if (email == null || email.isEmpty()) {
            Log.e(TAG, "Email is null or empty. Cannot get delivery availability.");
            if (listener != null) listener.onError(new IllegalArgumentException("Email cannot be null or empty"));
            return;
        }

        db.collection(Delivery_collection)
                .whereEqualTo(UserEmail_key, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().isEmpty()) {
                                Log.w(TAG, "No delivery account found with email: " + email + " when fetching status.");
                                if (listener != null) listener.onNotFound();
                                return;
                            }
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Boolean isAvailable = document.getBoolean(IsDeliverAvailable_key); // Use IsDeliverAvailable_key
                                if (isAvailable == null) {
                                    Log.w(TAG, IsDeliverAvailable_key + " field is missing or not a boolean for email: " + email);
                                    if (listener != null) listener.onAvailabilityFetched(false);
                                } else {
                                    Log.d(TAG, "Fetched " + IsDeliverAvailable_key + " for " + email + ": " + isAvailable);
                                    if (listener != null) listener.onAvailabilityFetched(isAvailable);
                                }
                                break;
                            }
                        } else {
                            Log.w(TAG, "Error getting document to fetch availability: ", task.getException());
                            if (listener != null) listener.onError(task.getException());
                        }
                    }
                });
    }

    public interface AvailabilityListener {
        void onAvailabilityFetched(Boolean isAvailable);
        void onNotFound();
        void onError(Exception e);
    }
}