package com.cm20314.mapapp.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.cm20314.mapapp.models.Coordinate;

import org.junit.Test;

public class LocationServiceTests {
    @Test
    public void TransformationTest1(){
        Coordinate c1 = LocationService.transformCoords(new Coordinate(51.380328600115746, -2.325626301763966));
        assertEquals(c1, new Coordinate(493.5399269395445, -2241.8884829974695));
    }
    @Test
    public void TransformationTest2(){
        double[] c2 = LocationService.transformCoordinates(new double[]{51.37903241260382, -2.332014659522025});
        assertEquals(new Coordinate(c2[0], c2[1]), new Coordinate(167.65408688534748, 501.299904817792));
    }
}
