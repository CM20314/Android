package com.cm20314.mapapp.services;

import com.cm20314.mapapp.models.Coordinate;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class LocationService {

    private Context context;
    private LocationManager locationManager;


    /*
     * Constructs a LocationService with the given context.
     * @param context The context used to access system services.
     */
    public LocationService(Context context, LocationListener listener) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 2, listener);
        }
    }

    /**
     * Performs a matrix transformation on the latitude and longitude
     * to get the coordinates of the map
     * @param latAndLong Latitude and longitude
     * @return Transformed x and y coordinates
     */
    private float[] transformCoordinates(float[] latAndLong){
//        latAndLong[0] = latAndLong[0] - Constants.gpsOffset[0];
//        latAndLong[1] = latAndLong[1] - Constants.gpsOffset[1];
        float[] originalCoordinates = {latAndLong[0], latAndLong[1], 1};
        float[] output = {0,0, 0};
        float count = 0;

        for (int i = 0; i < 3; i++){
            count = 0;
            for (int j = 0; j < 3; j++){
                count += 1000 * Constants.gpsToCoordinateMatrix[i][j] * originalCoordinates[j];
            }
            output[i] = count;
        }

        return new float[] {output[0], output[1]};

    }


    /**
     * Gets the location of the device as coordinates on the map.
     * If the device is not on university campus, the returned
     * coordinates are (500,500)
     * @return Device's coordinates
     */
    public Coordinate getLocation(){
        float[] latAndLong = getLatAndLong();
        float[] coordinateValues = transformCoordinates(latAndLong);
        return new Coordinate(coordinateValues[0], coordinateValues[1]);
    }


    /**
     * Uses Fused Location client to get the current location of the device.
     *
     * @return An array with the latitude at index 0, and longitude at index 1.
     */
    public float[] getLatAndLong() {
        float[] latAndLong = new float[2];
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latAndLong[0] = (float) lastKnownLocation.getLatitude();
                latAndLong[1] = (float) lastKnownLocation.getLongitude();
            }
        }
        return latAndLong;
    }




}
