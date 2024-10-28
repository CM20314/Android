package com.cm20314.mapapp.models;

import androidx.annotation.Nullable;

public class Coordinate {
    public double x;
    public double y;
    public int z;
    public int id;
    public String matchHandle = "";

    public Coordinate(){

    }

    public Coordinate(double x, double y){
        this.x = x;
        this.y = y;
    }

    public void stretch(double factorX, double factorY){
        x = (x - 500) * factorX + 500;
        y = (y - 416) * factorY + 416;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj == null || obj.getClass() != Coordinate.class){
            return false;
        }
        return (this.x - ((Coordinate)obj).x < 0.001) && (this.y - ((Coordinate)obj).y < 0.001);
    }
}
