package com.cm20314.mapapp.services;

import android.widget.TextView;

import com.cm20314.mapapp.interfaces.IHttpRequestCallback;
import com.cm20314.mapapp.models.Coordinate;
import com.cm20314.mapapp.models.NodeArcDirection;
import com.cm20314.mapapp.models.RouteRequestData;
import com.cm20314.mapapp.models.RouteResponseData;

import java.util.List;

/**
 * Handles routing (pathfinding) requests to the backend
 */
public class RoutingService {
    /**
     * Sends a router request to the backend
     * @param requestData Request data
     * @param callback Callback to return the response
     */
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
        );
    }

    /**
     * Updates direction command on the specified TextView
     * @param nodeArcDirections Entire path
     * @param location Current location
     * @param directionTextView TextView to display direction
     * @return Direction command (e.g. "Turn left")
     */
    public NodeArcDirection updateDirectionCommand(List<NodeArcDirection> nodeArcDirections, Coordinate location, TextView directionTextView){
        NodeArcDirection direction = getDirectionCommand(nodeArcDirections, location);
        if(direction == null){
            return null;
        }
        directionTextView.setText(direction.direction);
        return direction;
    }

    /**
     * Determines the most appropriate direction command given a path (with associated direction commands) and current location.
     * @param nodeArcDirections Path with associated direction commands
     * @param location Current location
     * @return Direction command
     */
    public NodeArcDirection getDirectionCommand(List<NodeArcDirection> nodeArcDirections, Coordinate location){
        // Close enough to destination -> arrived
        double distanceToDestination = Math.pow((Math.pow(location.x - nodeArcDirections.get(nodeArcDirections.size() - 1).nodeArc.node2.coordinate.x, 2)
                +Math.pow(location.y - nodeArcDirections.get(nodeArcDirections.size() - 1).nodeArc.node2.coordinate.y, 2)), 0.5);
        if(distanceToDestination < Constants.MAX_DISTANCE_BEFORE_ARRIVED){
            NodeArcDirection dir = nodeArcDirections.get(nodeArcDirections.size() - 1);
            dir.direction = "Arrive";
            return dir;
        }

        // Find nearest node
        int nearestIndex = 0;
        double shortestDistance = Integer.MAX_VALUE;

        for(int i = 0; i < nodeArcDirections.size(); i++){
            double midX = (nodeArcDirections.get(i).nodeArc.node1.coordinate.x + nodeArcDirections.get(i).nodeArc.node2.coordinate.x) / 2;
            double midY = (nodeArcDirections.get(i).nodeArc.node1.coordinate.y + nodeArcDirections.get(i).nodeArc.node2.coordinate.y) / 2;
            double distance = Math.pow((Math.pow(location.x - midX, 2) +Math.pow(location.y - midY, 2)), 0.5);
            if(distance < shortestDistance){
                nearestIndex = i;
                shortestDistance = distance;
            }
        }

        // Location is too far from path (requires re-computation)
        if(shortestDistance > Constants.MAX_DISTANCE_TO_PATH_BEFORE_RECOMPUTING){
            return null;
        }


        if(nodeArcDirections.size() == nearestIndex + 1){
            nearestIndex -= 1;
        }
        NodeArcDirection nearestNodeArcDirection = nodeArcDirections.get(nearestIndex + 1);

        // No associated direction (walk in a straight line)
        if(nearestNodeArcDirection != null){
            if(nearestNodeArcDirection.direction.equals("")){
                nearestNodeArcDirection.direction = "Walk";
            }

            return nearestNodeArcDirection;
        }

        return null;
    }
}
