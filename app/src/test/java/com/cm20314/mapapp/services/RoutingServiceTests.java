package com.cm20314.mapapp.services;

import static org.junit.Assert.*;

import android.content.Context;

import com.cm20314.mapapp.models.Coordinate;
import com.cm20314.mapapp.models.RouteResponseData;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RoutingServiceTests {
    private RouteResponseData testRouteData;
    private RoutingService routingService;
    @Before
    public void Initialise() {
        String json = "{\"nodeArcDirections\":[{\"nodeArc\":{\"node1Id\":21165,\"node2Id\":21163,\"stepFree\":true,\"cost\":13.044787898185035,\"nodeArcType\":0,\"requiresUsageRequest\":false,\"node1\":{\"floor\":-100,\"coordinateId\":21164,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":200.73782775732963,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21164},\"junctionSize\":0,\"id\":21165},\"node2\":{\"floor\":-100,\"coordinateId\":21162,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":213.78261565551466,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21162},\"junctionSize\":0,\"id\":21163},\"isMapDisplayablePath\":true,\"id\":21175},\"direction\":\"\"},{\"nodeArc\":{\"node1Id\":21163,\"node2Id\":21161,\"stepFree\":true,\"cost\":11.925724347686895,\"nodeArcType\":0,\"requiresUsageRequest\":false,\"node1\":{\"floor\":-100,\"coordinateId\":21162,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":213.78261565551466,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21162},\"junctionSize\":0,\"id\":21163},\"node2\":{\"floor\":-100,\"coordinateId\":21160,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":225.70834000320156,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21160},\"junctionSize\":0,\"id\":21161},\"isMapDisplayablePath\":true,\"id\":21174},\"direction\":\"\"},{\"nodeArc\":{\"node1Id\":21161,\"node2Id\":21159,\"stepFree\":true,\"cost\":11.84568592924603,\"nodeArcType\":0,\"requiresUsageRequest\":false,\"node1\":{\"floor\":-100,\"coordinateId\":21160,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":225.70834000320156,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21160},\"junctionSize\":0,\"id\":21161},\"node2\":{\"floor\":-100,\"coordinateId\":21158,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":237.5540259324476,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21158},\"junctionSize\":0,\"id\":21159},\"isMapDisplayablePath\":true,\"id\":21173},\"direction\":\"\"},{\"nodeArc\":{\"node1Id\":21159,\"node2Id\":21157,\"stepFree\":true,\"cost\":15.687530014406917,\"nodeArcType\":0,\"requiresUsageRequest\":false,\"node1\":{\"floor\":-100,\"coordinateId\":21158,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":237.5540259324476,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21158},\"junctionSize\":0,\"id\":21159},\"node2\":{\"floor\":-100,\"coordinateId\":21156,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":253.2415559468545,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21156},\"junctionSize\":0,\"id\":21157},\"isMapDisplayablePath\":true,\"id\":21172},\"direction\":\"\"},{\"nodeArc\":{\"node1Id\":21157,\"node2Id\":21155,\"stepFree\":true,\"cost\":32.01536737634066,\"nodeArcType\":0,\"requiresUsageRequest\":false,\"node1\":{\"floor\":-100,\"coordinateId\":21156,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":253.2415559468545,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21156},\"junctionSize\":0,\"id\":21157},\"node2\":{\"floor\":-100,\"coordinateId\":21154,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":285.25692332319517,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21154},\"junctionSize\":0,\"id\":21155},\"isMapDisplayablePath\":true,\"id\":21171},\"direction\":\"\"},{\"nodeArc\":{\"node1Id\":21155,\"node2Id\":25123,\"stepFree\":true,\"cost\":2.8288282375536937,\"nodeArcType\":0,\"requiresUsageRequest\":false,\"node1\":{\"floor\":-100,\"coordinateId\":21154,\"buildingId\":0,\"matchHandle\":\"\",\"coordinate\":{\"x\":285.25692332319517,\"y\":460.9137682087405,\"z\":0,\"matchHandle\":\"\",\"id\":21154},\"junctionSize\":0,\"id\":21155},\"node2\":{\"floor\":2,\"coordinateId\":25122,\"buildingId\":25115,\"matchHandle\":\"\",\"coordinate\":{\"x\":285.25692332319517,\"y\":463.7425964462942,\"z\":2,\"matchHandle\":\"\",\"id\":25122},\"junctionSize\":0,\"id\":25123},\"isMapDisplayablePath\":false,\"id\":25124},\"direction\":\"Turn Right\"}],\"success\":true,\"errorMessage\":\"\"}";
        testRouteData = new Gson().fromJson(json, RouteResponseData.class);

        routingService = new RoutingService();
    }

    @Test
    public void DirectionTextTest1(){
        String directionCommand = routingService.getDirectionCommand(testRouteData.nodeArcDirections, new Coordinate(200, 460));
        String expectedOutput = "Walk";

        assertEquals(expectedOutput, directionCommand);
    }

    @Test
    public void DirectionTextTest2(){
        String directionCommand = routingService.getDirectionCommand(testRouteData.nodeArcDirections, new Coordinate(183, 460));
        String expectedOutput = "false";

        assertEquals(expectedOutput, directionCommand);
    }

    @Test
    public void DirectionTextTest3(){
        String directionCommand = routingService.getDirectionCommand(testRouteData.nodeArcDirections, new Coordinate(285, 460));
        String expectedOutput = "Arrive";

        assertEquals(expectedOutput, directionCommand);
    }

    @Test
    public void DirectionTextTest4(){
        String directionCommand = routingService.getDirectionCommand(testRouteData.nodeArcDirections, new Coordinate(285, 463));
        String expectedOutput = "Arrive";

        assertEquals(expectedOutput, directionCommand);
    }
}
