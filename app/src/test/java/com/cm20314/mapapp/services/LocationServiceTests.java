package com.cm20314.mapapp.services;

import com.cm20314.mapapp.models.Coordinate;

import org.junit.Test;

public class LocationServiceTests {
    @Test
    public void TransformationTest1(){
        Coordinate c1 = LocationService.transformCoords(new Coordinate(51.376373450826826, -2.337616921348447));
        Coordinate c2 = LocationService.transformCoords(new Coordinate(51.38133693676351, -2.3171331381696136));
    }
}
