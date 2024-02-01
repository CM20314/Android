package com.example.cm20314.services;

import com.example.cm20314.interfaces.IHttpRequestCallback;
import com.example.cm20314.models.MapDataResponse;

import java.util.List;

public class MapDataService {
    public void getMap(int buildingId, int roomId, IHttpRequestCallback<MapDataResponse> callback){
        HttpRequestService<Object, MapDataResponse> httpRequestService = new HttpRequestService<>();
        httpRequestService.sendHttpRequest(
                HttpMethod.GET,
                Constants.API_ROOT + "/map?buildingId=" + buildingId + "&roomId=" + roomId,
                null,
                false,
                callback
                /*new IHttpRequestCallback<MapDataResponse>() {
                    @Override
                    public void onCompleted(HttpRequestService.HttpRequestResponse<MapDataResponse> response) {
                        System.out.println("Completed HTTP Request.");
                        callback.onCompleted(response);
                    }

                    @Override
                    public void onException() {
                        System.out.println("Failed HTTP Request.");
                        callback.onException();
                    }
                }*/
        );
    }

    public void searchContainers(String query, IHttpRequestCallback<List<String>> callback){
        HttpRequestService<Object, List<String>> httpRequestService = new HttpRequestService<>();
        httpRequestService.sendHttpRequest(
                HttpMethod.GET,
                Constants.API_ROOT + "/search?query="+query,
                null,
                false,
                callback
                /*new IHttpRequestCallback<List<String>>() {
                    @Override
                    public void onCompleted(HttpRequestService.HttpRequestResponse<List<String>> response) {
                        // response.Content
                        System.out.println("Completed HTTP Request.");
                        callback.onCompleted(response);
                    }

                    @Override
                    public void onException() {
                        System.out.println("Completed HTTP Request.");
                        callback.onException();
                    }
                }*/
        );
    }
}
