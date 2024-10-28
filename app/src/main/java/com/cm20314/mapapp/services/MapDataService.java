package com.cm20314.mapapp.services;

import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.MapDataResponse;
import com.cm20314.mapapp.models.MapSearchResponse;

/**
 * Handles map-related requests to the backend
 */
public class MapDataService {
    /**
     * Sends a map retrieval request to the backend
     * @param buildingId Building ID (if requesting a specific building's map)
     * @param roomId Room ID (if requesting a specific room's map)
     * @param callback Callback to pass on the response
     */
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

    /**
     * Sends a building/room search request to the backend
     * @param query Search query
     * @param callback Callback to pass on the response
     */
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

    /**
     * Constructs the map request URL from the provided building and room IDs
     * @param buildingId Building ID to request
     * @param roomId Room ID to request
     * @return Request URL
     */
    public String constructMapRequestUrl(int buildingId, int roomId){
        return Constants.API_ROOT + "/map?buildingId=" + buildingId + "&roomId=" + roomId;
    }

    /**
     * Constructs the search request URL from the query
     * @param query Search query
     * @return Request URL
     */
    public String constructContainerSearchUrl(String query){
        if(query == null) query = "null";
        else if(query.equals("")) query="null";
        return Constants.API_ROOT + "/search?query="+query;
    }
}
