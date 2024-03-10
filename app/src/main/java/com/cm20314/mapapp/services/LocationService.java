package com.cm20314.mapapp.services;

import com.cm20314.mapapp.models.Coordinate;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.Manifest;
import android.os.Looper;

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
    public static double[] transformCoordinates(double[] latAndLong){
//        latAndLong[0] = latAndLong[0] - Constants.gpsOffset[0];
//        latAndLong[1] = latAndLong[1] - Constants.gpsOffset[1];

//        latAndLong[0] =  ((1000/360.0) * (180 + latAndLong[1]));
//        latAndLong[1] =  ((1000/180.0) * (90 - latAndLong[0]));

        double[] inputPoint = {latAndLong[0], latAndLong[1], 1.0};
        double[] homographicPoint = multiplyMatrices(Constants.gpsToCoordinateMatrix, inputPoint);


        return homographicPoint;
    }

    public static double[] multiplyMatrices(double[][] homography, double[] point) {
        double[] result = new double[3];

        double first = homography[0][0] * point[0];
        double second = homography[0][1] * point[1];
        double third = homography[0][2] * point[2];

        result[0] = homography[0][0] * point[0] + homography[0][1] * point[1] + homography[0][2] * point[2];
        result[1] = homography[1][0] * point[0] + homography[1][1] * point[1] + homography[1][2] * point[2];
        result[2] = homography[2][0] * point[0] + homography[2][1] * point[1] + homography[2][2] * point[2];

        // Normalize the result to ensure the last element is 1.0
        for (int i = 0; i < 3; i++) {
            result[i] /= result[2];
        }

        return result;
    }

    public static Coordinate transformCoords(Coordinate coord){
        coord.x =  ((1000/360.0) * (180 + coord.y));
        coord.y =  ((1000/180.0) * (90 - coord.x));

        System.out.println(coord.x + "," +coord.y);

        return coord;
    }

    /**
     * Gets the location of the device as coordinates on the map.
     * If the device is not on university campus, the returned
     * coordinates are (500,500)
     * @return Device's coordinates
     */
    public Coordinate getLocation(){
        double[] latAndLong = getLatAndLong();
        //return transformCoords(new Coordinate(latAndLong[0], latAndLong[1]));
        double[] coordinateValues = transformCoordinates(latAndLong);
        Coordinate location = new Coordinate(coordinateValues[0], coordinateValues[1]);
        location.stretch(0.9, 1.25);
        return location;
    }


    /**
     * Uses Fused Location client to get the current location of the device.
     *
     * @return An array with the latitude at index 0, and longitude at index 1.
     */
    public double[] getLatAndLong() {
        double[] latAndLong = new double[2];
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latAndLong[0] = lastKnownLocation.getLatitude();
                latAndLong[1] =  lastKnownLocation.getLongitude();
            }
        }
        return latAndLong;
    }




}
