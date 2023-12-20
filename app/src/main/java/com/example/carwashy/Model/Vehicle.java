package com.example.carwashy.Model;

public class Vehicle {
    private String customer_id;
    private String carName;
    private String noPlate;
    private String model;
    private String vehicleType;
    private String imageUri; // New field for image URI

    public Vehicle() {
    }

    public Vehicle(String customer_id, String carName, String noPlate, String model, String vehicleType, String imageUri) {
        this.customer_id = customer_id;
        this.carName = carName;
        this.noPlate = noPlate;
        this.model = model;
        this.vehicleType = vehicleType;
        this.imageUri = imageUri;
    }

    public String getCarName() {
        return carName;
    }
    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
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
