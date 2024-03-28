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

    public NodeArcDirection updateDirectionCommand(List<NodeArcDirection> nodeArcDirections, Coordinate location, TextView directionTextView){
        NodeArcDirection direction = getDirectionCommand(nodeArcDirections, location);
        if(direction == null){
            return null;
        }
        directionTextView.setText(direction.direction);
        return direction;
    }

    public NodeArcDirection getDirectionCommand(List<NodeArcDirection> nodeArcDirections, Coordinate location){
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
            return null;
        }

        if(nearestNodeArcDirection != null){
            if(nearestNodeArcDirection.direction.equals("")){
                nearestNodeArcDirection.direction = "Walk";
            }

            return nearestNodeArcDirection;
        }

        return null;
    }
}
