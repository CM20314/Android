package com.cm20314.mapapp.services;

import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.RouteRequestData;
import com.cm20314.mapapp.models.RouteResponseData;

public class RoutingService {
    public void requestPath(RouteRequestData requestData,
                            IHttpRequestCallback<RouteResponseData> callback){
        HttpRequestService<RouteRequestData, RouteResponseData> httpRequestService = new HttpRequestService<>();
        httpRequestService.sendHttpRequest(
                HttpMethod.POST,
                Constants.API_ROOT + "/directions",
                requestData,
                true,
                callback,
                RouteResponseData.class
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
