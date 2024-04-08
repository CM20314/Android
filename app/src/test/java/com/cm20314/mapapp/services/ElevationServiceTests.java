package com.cm20314.mapapp.services;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ElevationServiceTests {
    private ElevationService elevationService;

    @Before
    public void Initialise(){
    }

    @Test
    public void ElevationTest1(){
        elevationService = new ElevationService();
        int elevation = elevationService.getElevation();
        assertEquals(2, elevation);
    }
    @Test
    public void ElevationTest2(){
        elevationService = new ElevationService();
        elevationService.increaseElevation();
        int elevation = elevationService.getElevation();
        assertEquals(3, elevation);
    }
    @Test
    public void ElevationTest3(){
        elevationService = new ElevationService();
        elevationService.decreaseElevation();
        elevationService.decreaseElevation();
        elevationService.decreaseElevation();
        int elevation = elevationService.getElevation();
        assertEquals(-1, elevation);
    }
    @Test
    public void ElevationTest4(){
        elevationService = new ElevationService();
        elevationService.setElevation(-1);
        int elevation = elevationService.getElevation();
        assertEquals(-1, elevation);
    }
}
