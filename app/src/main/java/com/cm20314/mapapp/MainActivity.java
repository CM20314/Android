package com.cm20314.mapapp;

import android.content.SharedPreferences;

import android.content.pm.PackageManager;

import android.os.Bundle;

import com.cm20314.mapapp.services.Constants;
import com.cm20314.mapapp.services.ElevationService;
import com.cm20314.mapapp.services.HttpRequestService;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cm20314.mapapp.databinding.ActivityMainBinding;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import android.Manifest;
import android.view.KeyEvent;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity{

    public static ElevationService elevationService = new ElevationService();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), BuildConfig.APPCENTER_KEY,
                Analytics.class, Crashes.class);

        Constants.Initialise();

        com.cm20314.mapapp.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (!permissionsGranted()){
            getLocationPermissions();
        }

        HttpRequestService.progressIndicator = findViewById(R.id.progress_indicator);
        String defaultSharedPreferencesName = getApplicationContext().getPackageName() + "_preferences";
        preferences = getApplicationContext().getSharedPreferences(defaultSharedPreferencesName, MODE_PRIVATE);
    }

    /**
     * Checks for coarse and fine location permissions
     * @return True if permissions are granted
     */
    private boolean permissionsGranted(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Requests location permissions from the user
     */
    private void getLocationPermissions(){
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                80085);
    }

    /**
     * Handles key events to simulate elevation tracking
     * @param event The key event.
     *
     * @return True
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    elevationService.increaseElevation();
                    Toast.makeText(getApplicationContext(), String.format("Floor %s", elevationService.getElevation()), Toast.LENGTH_SHORT).show();
                }
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    elevationService.decreaseElevation();
                    Toast.makeText(getApplicationContext(), String.format("Floor %s", elevationService.getElevation()), Toast.LENGTH_SHORT).show();
                }
                break;
        }
        return true;
     }
}