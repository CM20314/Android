package com.cm20314.mapapp.services;

import android.widget.TextView;

import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.Coordinate;
import com.cm20314.mapapp.models.Node;
import com.cm20314.mapapp.models.NodeArc;
import com.cm20314.mapapp.models.NodeArcDirection;
import com.cm20314.mapapp.models.RouteRequestData;
import com.cm20314.mapapp.models.RouteResponseData;

import java.util.List;

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

    public boolean updateDirectionCommand(List<NodeArcDirection> nodeArcDirections, Coordinate location, TextView directionTextView){
        String directionCommand = getDirectionCommand(nodeArcDirections, location);
        if(directionCommand.equals("false")){
            return false;
        }
        directionTextView.setText(directionCommand);
        return true;
    }

    public String getDirectionCommand(List<NodeArcDirection> nodeArcDirections, Coordinate location){
        NodeArcDirection nearestNodeArcDirection = null;
        double shortestDistance = Integer.MAX_VALUE;

        for(int i = 0; i < nodeArcDirections.size(); i++){
            double distance = Math.pow((Math.pow(location.x - nodeArcDirections.get(i).nodeArc.node2.coordinate.x, 2) +Math.pow(location.y - nodeArcDirections.get(i).nodeArc.node2.coordinate.y, 2)), 0.5);
            if(distance < shortestDistance){
                nearestNodeArcDirection = nodeArcDirections.get(i);
                shortestDistance = distance;
            }
        }

        if(shortestDistance > Constants.MAX_DISTANCE_TO_PATH_BEFORE_RECOMPUTING){
            return "false";
        }

        if(nearestNodeArcDirection != null){
            String directionCommand = nearestNodeArcDirection.direction;
            if(directionCommand.equals("")){
                directionCommand = "Walk";
            }

            return directionCommand;
        }

        return "false";
    }
}
