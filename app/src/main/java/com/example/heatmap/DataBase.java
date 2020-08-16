package com.example.heatmap;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.core.content.ContextCompat.getSystemService;

public class DataBase {
    private static final int HASH_PRECISION = 9;

    public static void logLocation(double latitude, double longitude) {
        double timestamp = System.currentTimeMillis(); // TODO: replace with call to NTP server
        String id = getMacAddress();
        String hash = geoHash(latitude, longitude);

        Map<String, Object> data = new HashMap<>();
        data.put("timestamp", timestamp);
        data.put("latitude", latitude);
        data.put("longitude", longitude);
        data.put("location", hash);

        FirebaseFirestore.getInstance().collection("users").document(id).set(data);
    }

    private static String getMacAddress() {
        String stringMac = "";
        try {
            List<NetworkInterface> networkInterfaceList = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface networkInterface:networkInterfaceList){
                if (networkInterface.getName().equalsIgnoreCase("wlan0")){
                    for (int i=0; i<networkInterface.getHardwareAddress().length;i++){
                        String stringMacByte = Integer.toHexString(networkInterface.getHardwareAddress()[i] & 0xFF);

                        if (stringMacByte.length() == 1){
                            stringMacByte = "0" + stringMacByte;
                        }

                        stringMac = stringMac + stringMacByte.toUpperCase() + ":";
                    }
                    break;
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        }
        return stringMac;
    }

    private static String geoHash(double latitude, double longitude) {
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
