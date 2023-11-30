package com.example.carwashy.Model;

import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class BookingInfo {

    private String noplate;
    private String receipt;
    private List<Service> carset;
    private String valet;
    private String totalcost;
    private String carName;
    private String status;
    private String address;
    private String date;
    private String session;
    private String timestart;
    private String customerphone;
    private String valetphone;
    private String carsetJson;
    private String currentaddress;
    private double totalServiceTime;
    private String timefinish;

    // List to store services
    private List<Service> services;

    public BookingInfo() {

        this.noplate = noplate;
        this.carset = new ArrayList<>();
        this.valet = valet;
        this.totalcost = totalcost;
        this.carName = carName;
        this.address = address;
        this.status = "Pending";
        this.date = date;
        this.session = session;
        this.timestart = "Pending";
        this.timefinish = "Pending";
        this.customerphone = "NONE";
        this.valetphone = valetphone;
        this.currentaddress = "NONE";
        this.totalServiceTime =totalServiceTime;
        this.services = services;
        this.receipt = receipt;}

    public String getCarsetJson() {
        Gson gson = new Gson();
        return gson.toJson(carset);
    }
    public String getNoPlate() {
        return noplate;
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
    public String getTimeStart() {
        return timestart;
    }
    public String getTimeFinish() {
        return timefinish;
    }

    public List<Service> getServices() {
        return services;
    }
    public String getPhone() {
        return customerphone;
    }
    public String getValetPhone() {
        return valetphone;
    }
    public String getSession() {
        return session;
    }
    public String getCurrentaddress() {
        return currentaddress;
    }
    public double getTotalServiceTime() {
        return totalServiceTime;
    }
    public String getReceipt() {
        return receipt;
    }


    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
    public void setTotalServiceTime(double totalServiceTime) {
        this.totalServiceTime = totalServiceTime;
    }
    public void setCarsetJson(String carsetJson) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>(){}.getType();
        carset = gson.fromJson(carsetJson, type);
    }
    public void setPhone(String customerphone) {
        this.customerphone = customerphone;
    }
    public void setValetPhone(String valetphone) {
        this.valetphone = valetphone;
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
    public void setTimeStart(String timestart) {
        this.timestart = timestart;
    }
    public void setTimeFinish(String timefinish) {
        this.timefinish = timefinish;
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
    public void setNoPlate(String noPlate) {
        this.noplate = noPlate;
    }
    public void setAddress(String address) {
        this.address = address;
    }


    // Helper method to convert the list of services to a JSON string
    public static String convertServicesListToJson(List<Service> services) {
        Gson gson = new Gson();
        return gson.toJson(services);
    }

    // Helper method to convert a JSON string to a list of services
    public static List<Service> convertJsonToServicesList(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>(){}.getType();
        return gson.fromJson(json, type);
    }

}