package com.cm20314.mapapp.services;

/**
 * Handles elevation tracking and simulation
 */
public class ElevationService {
    /**
     * The elevation of the user (defaults to 2)
     */
    private int elevation = 2;

    public ElevationService(){
    }

    public ElevationService(int elevation){
        this.elevation = elevation;
    }

    /**
     * Sets the elevation of the user
     * @param newElevation New user elevation
     */
    public void setElevation(int newElevation){
        if (newElevation > -10 && newElevation < 10) {
            elevation = newElevation;
        }
    }

    /**
     * Increases elevation by 1
     */
    public void increaseElevation(){
        setElevation(elevation + 1);
    }

    /**
     * Decreases elevation by 1
     */
    public void decreaseElevation(){
        setElevation(elevation -1 );
    }

    /**
     * Returns the current user elevation
     * @return elevation
     */
    public int getElevation(){
        return elevation;
    }
}
