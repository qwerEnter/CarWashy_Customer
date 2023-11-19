package com.example.carwashy.Model;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.List;

public class CarWashRecord {

    private String noPlate;
    private List<Service> carset;
    private String valet;
    private String totalcost;
    private String carName;
    private String status;
    private String address;
    private String date;
    private String session;
    private String phone;
    private String currentaddress;
    private String receipt; // New field for image URI

    private String timeslot;

    // List to store services
    private List<Service> services;

    public CarWashRecord() {

    }



    public String getCarsetJson() {
        Gson gson = new Gson();
        return gson.toJson(carset);
    }
    public List<Service> getCarset() {
        return carset;
    }
    public String getValet() {
        return valet;
    }
    public String getTotalcost() {
        return totalcost;
    }
    public String getCarName() {
        return carName;
    }
    public String getAddress() {
        return address;
    }
    public String getStatus() {
        return status;
    }
    public String getDate() {
        return date;
    }
    public String getTimeslot() {
        return timeslot;
    }
    public List<Service> getServices() {
        return services;
    }
    public String getPhone() {
        return phone;
    }
    public String getSession() {
        return session;
    }
    public String getCurrentaddress() {
        return currentaddress;
    }

    public String getNoPlate() {
        return noPlate;
    }

    public void setNoPlate(String noPlate) {
        this.noPlate = noPlate;
    }

    public void setCarsetJson(String carsetJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>(){}.getType();
        carset = gson.fromJson(carsetJson, type);
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setCurrentaddress(String currentaddress) {
        this.currentaddress = currentaddress;
    }
    public void setSession(String session) {
        this.session = session;
    }
    public void setServices(List<Service> services) {
        this.services = services;
    }
    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setCarset(List<Service> carset) {
        this.carset = carset;
    }
    public void setValet(String valet) {
        this.valet = valet;
    }
    public void setTotalcost(String totalcost) {
        this.totalcost = totalcost;
    }
    public void setCarName(String carName) {
        this.carName = carName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getreceipt() {
        return receipt;
    }

    public void setImageUri(String receipt) {
        this.receipt = receipt;
    }

    // Helper method to convert the list of services to a JSON string
    public static String convertServicesListToJson(List<Service> services) {
        Gson gson = new Gson();
        return gson.toJson(services);
    }

    // Helper method to convert a JSON string to a list of services
    public static List<CarWashRecord> convertJsonToServicesList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<CarWashRecord>>(){}.getType();
        return gson.fromJson(json, type);
    }

}