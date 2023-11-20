package com.example.carwashy.Model;

public class Service {

    private String name;
    private String description;
    private double cost,cost2; // Changed to double for numeric values

    private double points;
    private double servicetime,servicetime2;
    private String imageUri;

    public Service() {
    }

    public Service(String name, String description, double cost,double cost2,double points, double servicetime, double servicetime2, String imageUri) {
        this.name = name;
        this.description = description;
        this.cost = cost;
        this.cost2 = cost2;
        this.points = points;
        this.servicetime = servicetime;
        this.servicetime2 = servicetime2;
        this.imageUri = imageUri;
    }

    public String getName() {
        return name;
    }

    public double getPoints() {
        return points;
    }

    public String getDescription() {
        return description;
    }

    public double getCost() {
        return cost;
    }

    public double getServicetime() {
        return servicetime;
    }

    public double getCost2() {
        return cost2;
    }

    public double getServicetime2() {
        return servicetime2;
    }

    public String getImageUri() {
        return imageUri;
    }
}
