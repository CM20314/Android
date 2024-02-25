package com.cm20314.mapapp.models;

public class NodeArc extends Entity{
    public int node1Id;
    public int node2Id;
    public boolean stepFree;
    public double cost;
    public int nodeArcType;
    public boolean requiresUsageRequest;
    public Node node1;
    public Node node2;
}
