package com.example.heatmap;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
    private String CHANNEL_ID = "channel_id";
    private int notificationId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createNotificationChannel();

        densePopulationNotification();

        webview =(WebView)findViewById(R.id.webView);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        webview.loadUrl("https://www.google.com/maps");

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

                    densePopulationCheck(latitude, longitude);
                }


            }
        };
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(updateInterval);

        requestLocationPermission();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "channel0";
            String channelDescription = "channel_description";
            int channelImportance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, channelName, channelImportance);
            channel.setDescription(channelDescription);

            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public void densePopulationCheck(double latitude, double longitude) {
        DataBase.getUsersInRange(latitude, longitude, 30, numUsers -> {
            if (numUsers > 30) {
                densePopulationNotification();
            }
        });
    }

    public void densePopulationNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Densely populated area!")
                .setContentText("Lots of people around you! Please wear your mask.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, builder.build());
        notificationId++;
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