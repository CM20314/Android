package com.cm20314.mapapp.services;

public class Constants {
    public static final String API_ROOT = "https://cm20314.azurewebsites.net/api";
    public static final String FAVOURITES_SET_KEY = "favourites_set";

    public static final String RECENTS1_KEY = "recents1";
    public static final String RECENTS2_KEY = "recents2";
    public static final String RECENTS3_KEY = "recents3";
    public static final float[][] gpsToCoordinateMatrix = {
            {(float) 2.290652335897308198e-02, (float) 6.001070753838588789e-02, (float) 2.611513403865067744e+02},
            {(float) 2.382484926304594423e-04, (float) -6.180636884303564171e-04, (float) 4.817540262842802008e+02},
            {(float) 2.985536028808781496e-16, (float) 8.709463814211837462e-16, (float) 9.999999999999998890e-01}
    };
    public static final float[] gpsOffset = {51.383272F, (float) -2.336011};
}
