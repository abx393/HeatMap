package com.example.heatmap;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.*;

public class DatabaseActivity {
    private CollectionReference Users = FirebaseFirestore.getInstance().collection("users");

    public void UploadInfo(String MacAdress, double longitude, double latitude, String Timestamp) {

        GeoPoint Coordinates = new GeoPoint(latitude,longitude);
        Map<String, Object> info = new HashMap<>();
        info.put("lcoation",Coordinates);
        info.put("Time",Timestamp);
        Users.add(MacAdress);
        Users.document(MacAdress).set(info);
    }
}
