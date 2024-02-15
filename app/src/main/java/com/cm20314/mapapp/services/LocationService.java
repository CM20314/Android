package com.cm20314.mapapp.services;

import com.cm20314.mapapp.models.Coordinate;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import androidx.core.app.ActivityCompat;

public class LocationService {

    private Context context;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private float latitude;
    private float longitude;


    /*
    * Constructs a LocationService with the given context.
    * @param context The context used to access system services.
    */
    public LocationService(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // Do something with the location if needed
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
    }

    /**
     * Performs a matrix transformation on the latitude and longitude
     * to get the coordinates of the map
     * @param latAndLong Latitude and longitude
     * @return Transformed x and y coordinates
     */
    private float[] transformCoordinates(float[] latAndLong){
        float[] output = {0,0, 0};
        float count = 0;

        for (int i = 0; i < 2; i++){

            for (int j = 0; i < 2; j++){
                count += Constants.gpsToCoordinateMatrix[j][i] * latAndLong[j];
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
        latAndLong[0] = latAndLong[0] - Constants.gpsOffset[0];
        latAndLong[1] = latAndLong[1] - Constants.gpsOffset[1];
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
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latAndLong[0] = (float) lastKnownLocation.getLatitude();
                latAndLong[1] = (float) lastKnownLocation.getLongitude();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return latAndLong;
    }




}
