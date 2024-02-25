package com.cm20314.mapapp.services;

public class Constants {
    public static final String API_ROOT = "https://cm20314.azurewebsites.net/api";
    public static final String FAVOURITES_SET_KEY = "favourites_set";

    public static final String RECENTS1_KEY = "recents1";
    public static final String RECENTS2_KEY = "recents2";
    public static final String RECENTS3_KEY = "recents3";
    public static final float[][] gpsToCoordinateMatrix = {
            {(float) -3.980168073387700645e+01, (float) 5.198541668693133033e+01, (float) 2.166363287078652320e+03},
            {(float) -7.478535695938515460e+01, (float) -5.015665286744024343e+00, (float) 3.831210580740634668e+03},
            {(float) 6.821997535570057970e-18, (float) -4.878909776184769953e-19, (float) 9.999999999996478720e-04},
    };


    public static final float[] gpsOffset = {51.383272F, (float) -2.336011};
}
