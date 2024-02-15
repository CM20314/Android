package com.cm20314.mapapp.services;

public class Constants {
    public static final String API_ROOT = "https://cm20314.azurewebsites.net/api";


    public static final float[][] gpsToCoordinateMatrix = {
            {(float) -0.028126445193626257,(float) 0.055044536962640375, (float)4083.3280941537737},
            {(float)0.04744260641465321, (float)-0.0008323859990831961, (float)-3261.6998130556326},
            {(float)2.185559226919178e-17, (float)6.06069014419397e-17, (float)0.9999999999999994},
    };

    public static final float[] gpsOffset = {51.383272F, (float) -2.336011};
}
