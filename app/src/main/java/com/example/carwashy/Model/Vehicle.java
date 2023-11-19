package com.example.carwashy.Model;

public class Vehicle {
    private String carName;
    private String noPlate;
    private String model;
    private String vehicleType;
    private String imageUri; // New field for image URI

    public Vehicle() {
    }

    public Vehicle(String carName, String noPlate, String model, String vehicleType, String imageUri) {
        this.carName = carName;
        this.noPlate = noPlate;
        this.model = model;
        this.vehicleType = vehicleType;
        this.imageUri = imageUri;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getNoPlate() {
        return noPlate;
    }

    public void setNoPlate(String noPlate) {
        this.noPlate = noPlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getVehicleType() {
        return vehicleType;
    }
    public String getImageUri() {
        return imageUri;
    }
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
}
