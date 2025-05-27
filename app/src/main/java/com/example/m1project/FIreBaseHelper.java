package com.example.m1project;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class FIreBaseHelper {
    //dddddd77777121213
    public static final String User_collection="Users";
    public static final String Delivery_collection="DeliveryC";
    public static final String Restaurant_collection="RestaurantC";

    public static final String UserName_key= "User_name";
    public static final String UserEmail_key="User_email";
    public static final String UserCity_key="User_city";
    public static final String UserPass_key="User_pass";
    public static final String UserPhone_key="User_phone";



    public static final String RestaurantAdress_key="Restaurant_adress";

    public static final String TransportType_key="Transport_type";
    public static final String DeliverLocation_key="Deliver_Location";
    public static final String IsDeliverAvailable="Is_Deliver_Available";
    public static FirebaseFirestore db=FirebaseFirestore.getInstance();

    private static Map<String, Object> prepareData2Save(User user){

        Map<String,Object> userHashMaps= new HashMap<>();
        //userHashMaps.put(FireBaseHelper.UserId_key,user.getId());
        //Object FIreBaseHalper;
        userHashMaps.put(FIreBaseHelper.UserCity_key,user.getCity());
        userHashMaps.put(FIreBaseHelper.UserEmail_key,user.getEmail());
        userHashMaps.put(FIreBaseHelper.UserPass_key,user.getPassword());
        userHashMaps.put(FIreBaseHelper.UserName_key,user.getName());
        userHashMaps.put(FIreBaseHelper.UserPhone_key,user.getPhone());
        return userHashMaps;

    }

    private static Map<String, Object> prepareData2Save(Restaurant restaurant){

        Map<String,Object> userHashMaps= new HashMap<>();
        //userHashMaps.put(FireBaseHalper.UserId_key,user.getId());
        Object FIreBaseHalper;
        userHashMaps.put(FIreBaseHelper.UserCity_key,restaurant.getCity());
        userHashMaps.put(FIreBaseHelper.UserEmail_key,restaurant.getEmail());
        userHashMaps.put(FIreBaseHelper.UserPass_key,restaurant.getPassword());
        userHashMaps.put(FIreBaseHelper.UserName_key,restaurant.getName());
        userHashMaps.put(FIreBaseHelper.UserPhone_key,restaurant.getPhone());
        userHashMaps.put(FIreBaseHelper.RestaurantAdress_key,restaurant.getAdress());
        return userHashMaps;

    }

    private static Map<String, Object> prepareData2Save(Delivery deliver){

        Map<String,Object> userHashMaps= new HashMap<>();
        //userHashMaps.put(FireBaseHalper.UserId_key,user.getId());
        Object FIreBaseHalper;
        userHashMaps.put(FIreBaseHelper.UserCity_key,deliver.getCity());
        userHashMaps.put(FIreBaseHelper.UserEmail_key,deliver.getEmail());
        userHashMaps.put(FIreBaseHelper.UserPass_key,deliver.getPassword());
        userHashMaps.put(FIreBaseHelper.UserName_key,deliver.getName());
        userHashMaps.put(FIreBaseHelper.UserPhone_key,deliver.getPhone());

        userHashMaps.put(FIreBaseHelper.TransportType_key,deliver.getTransportType());
        userHashMaps.put(FIreBaseHelper.DeliverLocation_key,deliver.getLocation());
        userHashMaps.put(FIreBaseHelper.IsDeliverAvailable,deliver.getIsAvailable());
        return userHashMaps;

    }

    public static void headToFirebase (Object object, Context context){
        Map<String,Object> data;

            User user= (User) object;
            data=prepareData2Save(user);

        if (object instanceof Restaurant){
            Restaurant restaurant = (Restaurant) object;
            data=prepareData2Save(restaurant);
        }
        if (object instanceof Delivery){
            Delivery delivery = (Delivery) object;
            data=prepareData2Save(delivery);
        }


        if (object instanceof User) {
            //prepareData2Save(user);
            db.collection(User_collection)
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            //Toast.makeText(context, "succeses", Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }

        if (object instanceof Delivery) {
            //prepareData2Save(user);
            db.collection(Delivery_collection)
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            Toast.makeText(context, "succeses", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }

        if (object instanceof Restaurant) {
            //prepareData2Save(user);
            db.collection(Restaurant_collection)
                    .add(data)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            //Toast.makeText(context, "succeses", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
        }

    }

    public static void isAccountExist(String email, Context context, final AccountExistListener listener) {
        db.collection(User_collection)
                .whereEqualTo(UserEmail_key, email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean exists = !task.getResult().isEmpty();
                            listener.onAccountExistResult(exists);
                        } else {
                            Log.w(TAG, "Error checking account existence.", task.getException());
                            Toast.makeText(context, "Error checking account existence.", Toast.LENGTH_LONG).show();
                            listener.onAccountExistResult(false); // Assume doesn't exist on error
                        }
                    }
                });
    }

    public interface AccountExistListener {
        void onAccountExistResult(boolean exists);
    }

}
