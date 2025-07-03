package com.shiprouting.ship_routing;

import jakarta.persistence.*;

@Entity
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "source_node_id", nullable = false)
    private Node source;

    @ManyToOne
    @JoinColumn(name = "destination_node_id", nullable = false)
    private Node destination;

    private double distance;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public Node getSource() {
        return source;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getDestination() {
        return destination;
    }

    public void setDestination(Node destination) {
        this.destination = destination;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
