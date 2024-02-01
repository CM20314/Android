package com.example.cm20314.services;

import com.example.cm20314.interfaces.IHttpRequestCallback;
import com.example.cm20314.models.Coordinate;
import com.example.cm20314.models.MapDataResponse;
import com.example.cm20314.models.RouteRequestData;

public class PathfindingService {
    public void requestPath(RouteRequestData requestData,
                            IHttpRequestCallback<Object> callback){
        HttpRequestService<RouteRequestData, Object> httpRequestService = new HttpRequestService<>();
        httpRequestService.sendHttpRequest(
                HttpMethod.GET,
                Constants.API_ROOT + "/directions",
                requestData,
                true,
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
}
