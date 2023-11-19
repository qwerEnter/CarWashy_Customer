package com.example.carwashy.Model;

public class Service {

    private String name;

    private String description;
    private double cost; // Changed to double for numeric values
    private double points;
    private String imageUri;

    public Service() {
    }

    public Service(String name, String description, double cost, double points, String imageUri) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.points = points;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPoints() {
        return points;
    }
    public double getCost() {
        return cost;
    }

    public String getImageUri() {
        return imageUri;
    }
}

