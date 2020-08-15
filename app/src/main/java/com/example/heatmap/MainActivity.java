package com.example.heatmap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback{

    private static final int PERMISSION_REQUEST_LOCATION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //WebView myWebView = (WebView) findViewById(R.id.webview);
        //myWebView.loadUrl("http://www.example.com");

        requestLocationPermission();

    }

    //@Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Yay", "Permission granted.");
            } else {
                Log.d("Fuck", "Permission denied.");
            }
        }
    }

    public void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,
                                                new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                                                PERMISSION_REQUEST_LOCATION);

        }
    }

}
