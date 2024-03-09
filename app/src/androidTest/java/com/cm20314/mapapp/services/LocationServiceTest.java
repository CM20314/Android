package com.cm20314.mapapp.services;

import android.content.Context;
import android.location.LocationListener;
import android.os.Build;
import android.os.Looper;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.Test;
import org.junit.runner.RunWith;

//mock location
import android.location.Location;
import android.location.LocationManager;

import static org.junit.Assert.*;

import com.cm20314.mapapp.MainActivity;
import com.cm20314.mapapp.services.LocationService;

@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {

    private LocationService locationService;


    // gives us location permissions for the instrumented test
    @Rule
    public GrantPermissionRule mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
    @Before
    public void createLocationService(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        locationService = new LocationService(context, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

            }
        });
    }


    /**
     * Test technically could fail under legitimate circumstances where
     * the user is in a very specific location off the
     * west coast of africa but we'll assume that isn't going to happen
     */
    @Test
    public void gpsCoordinatesArentJustZero(){

        LocationManager locationManager = (LocationManager) InstrumentationRegistry.getInstrumentation().getContext()
                .getSystemService(android.content.Context.LOCATION_SERVICE);
        Location mockLocation = new Location(LocationManager.GPS_PROVIDER);
        mockLocation.setLatitude(51.3787);
        mockLocation.setLongitude(-2.3275); // Lat and logitude of top corner in campus
        mockLocation.setAccuracy(1.0f); // Some random accuracy
        mockLocation.setTime(System.currentTimeMillis()); // Example timestamp
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mockLocation.setElapsedRealtimeNanos(System.nanoTime()); // Example elapsed time
        }
        locationManager.setTestProviderLocation(LocationManager.GPS_PROVIDER, mockLocation);
        //idk what this line does but otherwise the location service doesn't work
        Looper.prepare();
        float[] coords = locationService.getLatAndLong();
        assertTrue(coords[0] != 0 || coords[1] != 0);
    }
}