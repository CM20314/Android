package com.cm20314.mapapp.services;

import com.cm20314.mapapp.models.Coordinate;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, Coordinate> TEXT_OFFSETS = new HashMap<String, Coordinate>();

    public static void Initialise(){
        TEXT_OFFSETS.put("7W", new Coordinate(-9, 0));
        TEXT_OFFSETS.put("2W", new Coordinate(0, -3));
        TEXT_OFFSETS.put("9W", new Coordinate(-2.5, -15));
        TEXT_OFFSETS.put("5W", new Coordinate(0, 15));
        TEXT_OFFSETS.put("WH", new Coordinate(0, 20));
        TEXT_OFFSETS.put("4W Cafe", new Coordinate(0, 1));
        TEXT_OFFSETS.put("3WN", new Coordinate(1, 9));
        TEXT_OFFSETS.put("3WA", new Coordinate(0, 15));
        TEXT_OFFSETS.put("3S", new Coordinate(2, 3));
        TEXT_OFFSETS.put("1WN", new Coordinate(0, 10));
        TEXT_OFFSETS.put("6WS", new Coordinate(-5, -5));
        TEXT_OFFSETS.put("UH", new Coordinate(0, 6));
        TEXT_OFFSETS.put("Chaplaincy", new Coordinate(15, -8));
        TEXT_OFFSETS.put("Norwood House", new Coordinate(-30, 0));
        TEXT_OFFSETS.put("Estates", new Coordinate(5, -5));
        TEXT_OFFSETS.put("4E", new Coordinate(-10, 0));
        TEXT_OFFSETS.put("6E", new Coordinate(20, 10));
        TEXT_OFFSETS.put("4ES", new Coordinate(0, 4));
        TEXT_OFFSETS.put("Lime Tree", new Coordinate(20, -10));
        TEXT_OFFSETS.put("The Edge", new Coordinate(19, -10));
        TEXT_OFFSETS.put("EB", new Coordinate(-10, 20));
        TEXT_OFFSETS.put("STV", new Coordinate(-30, 10));
        TEXT_OFFSETS.put("Library", new Coordinate(3, 3));
        TEXT_OFFSETS.put("Volleyball", new Coordinate(23, 10));
        TEXT_OFFSETS.put("Eastwood Pitches", new Coordinate(25, 30));
        TEXT_OFFSETS.put("Medical Centre", new Coordinate(0, -10));
        TEXT_OFFSETS.put("Bobsleigh Track", new Coordinate(0, 45));
        TEXT_OFFSETS.put("Shooting Range", new Coordinate(-13, 21.5));
        TEXT_OFFSETS.put("South Football Pitch", new Coordinate(-1, 0));
    }
}
