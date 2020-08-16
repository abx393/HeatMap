package com.example.heatmap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int PERMISSION_REQUEST_LOCATION = 0;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private DataBase db;
    private TextView latitudeText, longitudeText;
    private boolean permissionGranted = false;
    private WebView webview;
    private long updateInterval = 100000; //milliseconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webview =(WebView)findViewById(R.id.webView);

        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.loadUrl("https://www.youtube.com");

        //(new DatabaseActivity()).uploadInfo("macaddress", 30.0, 30.0, "timestamp");
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        latitudeText = findViewById(R.id.latitudeText);
        longitudeText = findViewById(R.id.longitudeText);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    latitudeText.setText("latitude " + latitude);
                    longitudeText.setText("longitude " + longitude);
                    DataBase.logLocation(latitude, longitude);
                }
            }
        };
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(updateInterval);

        requestLocationPermission();
    }

    public void getLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            latitudeText.setText("latitude " + latitude);
                            longitudeText.setText("longitude " + longitude);

                            DataBase.logLocation(latitude, longitude);
                        }
                    }
                });
    }

    public void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    //@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Yay", "Permission granted.");
                permissionGranted = true;
                //getLocation();
                startLocationUpdates();

            } else {
                permissionGranted = false;
                Log.d("grantResults[0] " + grantResults[0], "PackageManager.PERMISSION_GRANTED" + PackageManager.PERMISSION_GRANTED);
                Log.d("Darn", "Permission denied.");
            }
        }
    }

    public void requestLocationPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
            Log.d("check 1", "check 1");

        } else {
            Log.d("check 2", "check 2");
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
        }
    }

}