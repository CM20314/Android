package com.cm20314.mapapp.services;

import android.graphics.Color;

import com.cm20314.mapapp.R;
import com.cm20314.mapapp.models.Coordinate;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

/**
 * System constants
 */
public class Constants {
    /** Root URL for backend requests */
    public static final String API_ROOT = "https://cm20314.azurewebsites.net/api";
    /**Shared preferences key for favourite destinations*/
    public static final String FAVOURITES_SET_KEY = "favourites_set";

    /**Shared preferences key for recent destinations (position 1)*/
    public static final String RECENTS1_KEY = "recents1";
    /**Shared preferences key for recent destinations (position 2)*/
    public static final String RECENTS2_KEY = "recents2";
    /**Shared preferences key for recent destinations (position 3)*/
    public static final String RECENTS3_KEY = "recents3";
    /**Shared preferences key for colours enabled (paths)*/
    public static final String COLOURS_PATHS = "D4_COLS_P";
    /**Shared preferences key for colours enabled (buildings)*/
    public static final String COLOURS_BUILDINGS = "D4_COLS_B";

    /** GPS to Cartesian conversion matrix */
    public static final double[][] gpsToCoordinateMatrix = {
            {-7.62354283e+00, -9.29232860e+00,  3.69981637e+02},
            {4.57287525e+00, 5.00011102e+00, -2.23405501e+02},
            {-1.91384730e-02, 7.25339746e-03, 1.00000000e+00}
    };
    public static float MAX_ZOOM = 10f;
    public static float MIN_ZOOM = 1f;
    public static float PADDING = 200;

    public static final double MAX_DISTANCE_TO_PATH_BEFORE_RECOMPUTING = 20;
    public static final double MAX_DISTANCE_BEFORE_ARRIVED = 3;

    /** Offsets for labels on maps (to appear more aesthetic) */
    public static Map<String, Coordinate> TEXT_OFFSETS = new HashMap<String, Coordinate>();
    /** Categories for buildings (colour-coded) */
    public static Map<String, Integer> CATEGORIES = new HashMap<String, Integer>();
    /** Colours for each building category */
    public static Map<Integer, Integer> CAT_TO_COLOUR = new HashMap<Integer, Integer>();

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

        // 0 - normal
        // 1 - miscellaneous
        // 2 - student spaces
        // 3 - sport facilities
        // 4 - sport pitches
        CATEGORIES.put("1W", 0);
        CATEGORIES.put("2W", 0);
        CATEGORIES.put("3W", 0);
        CATEGORIES.put("4W", 0);
        CATEGORIES.put("5W", 0);
        CATEGORIES.put("6W", 0);
        CATEGORIES.put("7W", 0);
        CATEGORIES.put("8W", 0);
        CATEGORIES.put("9W", 0);
        CATEGORIES.put("10W", 0);
        CATEGORIES.put("1E", 0);
        CATEGORIES.put("2E", 0);
        CATEGORIES.put("3E", 0);
        CATEGORIES.put("4E", 0);
        CATEGORIES.put("5E", 0);
        CATEGORIES.put("6E", 0);
        CATEGORIES.put("7E", 0);
        CATEGORIES.put("8E", 0);
        CATEGORIES.put("9E", 0);
        CATEGORIES.put("10E", 0);
        CATEGORIES.put("1S", 0);
        CATEGORIES.put("2S", 0);
        CATEGORIES.put("3S", 0);
        CATEGORIES.put("4S", 0);
        CATEGORIES.put("4SA", 0);
        CATEGORIES.put("3WA", 0);
        CATEGORIES.put("5S", 1);
        CATEGORIES.put("6WS", 0);
        CATEGORIES.put("3WN", 0);
        CATEGORIES.put("1WN", 0);
        CATEGORIES.put("Library", 2);
        CATEGORIES.put("UH", 2);
        CATEGORIES.put("CB", 0);
        CATEGORIES.put("SU", 2);
        CATEGORIES.put("Chaplaincy", 1);
        CATEGORIES.put("Norwood House", 1);
        CATEGORIES.put("WH", 1);
        CATEGORIES.put("FH", 2);
        CATEGORIES.put("3G", 4);
        CATEGORIES.put("4W Cafe", 2);
        CATEGORIES.put("Astro", 4);
        CATEGORIES.put("Track", 3);
        CATEGORIES.put("Volleyball", 3);
        CATEGORIES.put("Bobsleigh Track", 3);
        CATEGORIES.put("Clay", 3);
        CATEGORIES.put("Eastwood Pitches", 4);
        CATEGORIES.put("Estates", 1);
        CATEGORIES.put("Hockey", 4);
        CATEGORIES.put("Limekiln Pitches", 4);
        CATEGORIES.put("Medical Centre", 1);
        CATEGORIES.put("Tennis Courts", 3);
        CATEGORIES.put("Rugby Pitch", 4);
        CATEGORIES.put("Shooting Range", 3);
        CATEGORIES.put("South Football Pitch", 4);
        CATEGORIES.put("STV", 3);
        CATEGORIES.put("St John's Pitches", 4);
        CATEGORIES.put("The Edge", 2);
        CATEGORIES.put("Lime Tree", 2);

        CAT_TO_COLOUR.put(0, R.color.vibrant_blue_light);
        CAT_TO_COLOUR.put(1, R.color.yellow);
        CAT_TO_COLOUR.put(2, R.color.purple);
        CAT_TO_COLOUR.put(3, R.color.green);
        CAT_TO_COLOUR.put(4, R.color.dark_green);
    }
}
