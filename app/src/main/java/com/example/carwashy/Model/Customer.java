package com.example.carwashy.Model;

public class Customer {

    private String email;
    private String password;
    private String name;
    private String address;
    private String phonenumber;

    // Required empty constructor for Firebase
    public Customer() {
    }

    // Constructor with parameters
    public Customer(String email, String password, String name, String address, String phonenumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phonenumber = phonenumber;
    }

    // Getters and setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
