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

    // Method to rotate the coordinate anticlockwise by degrees
    public void rotate(double degrees) {
        double radians = Math.toRadians(degrees);
        double newX = x * Math.cos(radians) - y * Math.sin(radians);
        double newY = x * Math.sin(radians) + y * Math.cos(radians);
        x = newX;
        y = newY;
    }

    // Method to offset the coordinate by the specified values
    public void offset(double offsetX, double offsetY) {
        x += offsetX;
        y += offsetY;
    }

    // Method to scale the coordinate to fit within the specified range
    public void scale(double minX, double minY, double maxX, double maxY) {
        double scaledX = (x - minX) / (maxX - minX) * 1000;
        double scaledY = (y - minY) / (maxY - minY) * 1000;
        x = scaledX;
        y = scaledY;
    }
}
