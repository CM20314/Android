package com.cm20314.mapapp.models;

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
}
