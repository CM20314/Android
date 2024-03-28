package com.cm20314.mapapp;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;

import android.os.Bundle;

import com.cm20314.mapapp.models.Coordinate;
import com.cm20314.mapapp.services.Constants;
import com.cm20314.mapapp.services.ElevationService;
import com.cm20314.mapapp.services.HttpRequestService;
import com.cm20314.mapapp.services.LocationService;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.cm20314.mapapp.databinding.ActivityMainBinding;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import android.Manifest;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import java.io.StringWriter;


public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {

    private ActivityMainBinding binding;

    public static ElevationService elevationService = new ElevationService();
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), BuildConfig.APPCENTER_KEY,
                Analytics.class, Crashes.class);

        Constants.Initialise();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_map, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //getting location permissions
        if (!permissionsGranted()){
            getLocationPermissions();
        }

        HttpRequestService.progressIndicator = findViewById(R.id.progress_indicator);
        findViewById(R.id.nav_bar_logo).setOnLongClickListener(this);
        preferences = getApplicationContext().getSharedPreferences(getDefaultSharedPreferencesName(), MODE_PRIVATE);

    }

    private String getDefaultSharedPreferencesName() {
        return getApplicationContext().getPackageName() + "_preferences";
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

    @Override
    public boolean onLongClick(View v) {
        boolean colourModeActivated = preferences.getBoolean("D4_COLS", false);
        boolean newColourModeActivated = !colourModeActivated;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("D4_COLS", newColourModeActivated);
        editor.apply();
        String toastText = newColourModeActivated ? "Colour mode activated" : "Colour mode deactivated";
        try{
            runOnUiThread(() -> Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_SHORT).show());
        }
        catch(Exception ex){
            System.out.println("ERROR");
        }
        return false;
    }
}