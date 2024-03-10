package com.cm20314.mapapp.services;

import com.cm20314.mapapp.models.Coordinate;

import org.junit.Test;

public class LocationServiceTests {
    @Test
    public void TransformationTest1(){
        Coordinate c1 = LocationService.transformCoords(new Coordinate(51.380328600115746, -2.325626301763966));
        Coordinate c2 = LocationService.transformCoords(new Coordinate(51.37903241260382, -2.332014659522025));
        Coordinate c3 = LocationService.transformCoords(new Coordinate(51.38027053091781, -2.3294318151545763));
        Coordinate c4 = LocationService.transformCoords(new Coordinate(51.37634775658724, -2.325800509715518));

        double[] c5 = LocationService.transformCoordinates(new double[]{51.37903241260382, -2.332014659522025});
        double[] c6 = LocationService.transformCoordinates(new double[]{51.37903241260382, -2.332014659522025});
    }
}
