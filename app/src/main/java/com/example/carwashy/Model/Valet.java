package com.example.carwashy.Model;

public class Valet {

    private String location;
    private String phonenumber;
    private String name;

    private String imageUri; // New field for image URI
    public Valet() {
    }

    public Valet(String location, String phonenumber, String name, String imageUri) {
        this.location = location;
        this.phonenumber = phonenumber;
        this.name = name;
        this.imageUri = imageUri;
    }

    public String getLocation() {
        return location;
    }
    public String getImageUri() {
        return imageUri;
    }
    public String getPhonenumber() {
        return phonenumber;
    }

    public String getName() {
        return name;
    }
}
