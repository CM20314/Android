package com.cm20314.mapapp.services;

import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.MapDataResponse;
import com.cm20314.mapapp.models.MapSearchResponse;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class MapDataService {
    public void getMap(int buildingId, int roomId, IHttpRequestCallback<MapDataResponse> callback){
        HttpRequestService<Object, MapDataResponse> httpRequestService = new HttpRequestService<>();
        httpRequestService.sendHttpRequest(
                HttpMethod.GET,
                constructMapRequestUrl(buildingId, roomId),
                null,
                false,
                callback,
                MapDataResponse.class
        );
    }

    public void searchContainers(String query, IHttpRequestCallback<MapSearchResponse> callback){
        HttpRequestService<Object, MapSearchResponse> httpRequestService = new HttpRequestService<>();
        httpRequestService.sendHttpRequest(
                HttpMethod.GET,
                constructContainerSearchUrl(query),
                null,
                false,
                callback,
                MapSearchResponse.class
        );
    }

    public String constructMapRequestUrl(int buildingId, int roomId){
        return Constants.API_ROOT + "/map?buildingId=" + buildingId + "&roomId=" + roomId;
    }
    public String constructContainerSearchUrl(String query){
        if(query == null) query = "null";
        else if(query.equals("")) query="null";
        return Constants.API_ROOT + "/search?query="+query;
    }
}
