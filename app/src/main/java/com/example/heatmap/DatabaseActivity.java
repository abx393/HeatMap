package com.example.heatmap;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.*;

public class DatabaseActivity {
    private static final int HASH_PRECISION = 9;

    private final CollectionReference users;

    public DatabaseActivity() {
        users = FirebaseFirestore.getInstance().collection("users");
    }

    public void logLocation(double latitude, double longitude) {
        double timestamp = System.currentTimeMillis(); // TODO: replace with call to NTP server
        String id = getMacAddress();
        String hash = geoHash(latitude, longitude);

        Map<String,Object> data = new HashMap<>();
        data.put("timestamp", timestamp);
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        data.put("location", hash);

        users.document(id).set(data);
    }

    private String getMacAddress() {
        // TODO: actually get mac address
        return "MAC-ADDRESS";
    }

    private String geoHash(double latitude, double longitude) {
        // Reference: https://github.com/chrisveness/latlon-geohash/blob/master/latlon-geohash.js
        final String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        int idx = 0;
        int bit = 0;
        boolean evenBit = true;
        StringBuilder hash = new StringBuilder();

        double latMin = -90;
        double latMax = 90;
        double longMin = -180;
        double longMax = 180;

        while (hash.length() < HASH_PRECISION) {
            if (evenBit) {
                double longMid = (longMin + longMax) / 2;
                idx <<= 1;
                if (longitude >= longMid) {
                    idx++;
                    longMin = longMid;
                } else {
                    longMax = longMid;
                }
            } else {
                double latMid = (latMin + latMax) / 2;
                idx <<= 1;
                if (latitude >= latMid) {
                    idx++;
                    latMin = latMid;
                } else {
                    latMax = latMid;
                }
            }
            evenBit = !evenBit;

            if (++bit == 5) {
                hash.append(base32.charAt(idx));
                bit = 0;
                idx = 0;
            }
        }

        return hash.toString();
    }
}
