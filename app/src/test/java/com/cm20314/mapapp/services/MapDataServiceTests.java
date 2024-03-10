package com.cm20314.mapapp.services;

import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.MapDataResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

public class MapDataServiceTests {
    private MapDataService mapDataService;

    @Before
    public void Initialise(){
        mapDataService = new MapDataService();
    }

    @Test
    public void MapUrlTest1(){
        String output = mapDataService.constructMapRequestUrl(0, 0);
        String expected = "https://cm20314.azurewebsites.net/api/map?buildingId=0&roomId=0";
        assertEquals(output, expected);
    }
    @Test
    public void MapUrlTest2(){
        String output = mapDataService.constructMapRequestUrl(-1, 0);
        String expected = "https://cm20314.azurewebsites.net/api/map?buildingId=-1&roomId=0";
        assertEquals(output, expected);
    }
    @Test
    public void MapUrlTest3(){
        String output = mapDataService.constructMapRequestUrl(2, 3);
        String expected = "https://cm20314.azurewebsites.net/api/map?buildingId=2&roomId=3";
        assertEquals(expected, output);
    }
    @Test
    public void SearchContainersUrlTest1(){
        String output = mapDataService.constructContainerSearchUrl(null);
        String expected = "https://cm20314.azurewebsites.net/api/search?query=null";
        assertEquals(expected, output);
    }
    @Test
    public void SearchContainersTest2(){
        String output = mapDataService.constructContainerSearchUrl("");
        String expected = "https://cm20314.azurewebsites.net/api/search?query=null";
        assertEquals(expected, output);
    }
    @Test
    public void SearchContainersTest3(){
        String output = mapDataService.constructContainerSearchUrl("CB1.1");
        String expected = "https://cm20314.azurewebsites.net/api/search?query=CB1.1";
        assertEquals(expected, output);
    }
    @Test
    public void SearchContainersTest4(){
        String output = mapDataService.constructContainerSearchUrl("CB 1.1");
        String expected = "https://cm20314.azurewebsites.net/api/search?query=CB 1.1";
        assertEquals(expected, output);
    }
}
