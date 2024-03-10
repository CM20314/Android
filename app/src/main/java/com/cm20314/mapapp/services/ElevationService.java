package com.cm20314.mapapp.services;

public class ElevationService {
    private int elevation;

    public ElevationService(){
        // defaults to 2 because most things are on that level
        elevation = 2;
    }

    public void setElevation(int newElevation){
        if (newElevation > -10 && newElevation < 10) {
            elevation = newElevation;
        }
    }

    public void increaseElevation(){
        setElevation(elevation + 1);
    }

    public void decreaseElevation(){
        setElevation(elevation -1 );
    }

    public int getElevation(){
        return elevation;
    }
}
