package com.example.carwashy.Model;

import java.util.ArrayList;
import java.util.List;

public class Premise {
    private String merchant_id;
    private String address;
    private String state;
    private List<Service> services;
    private List<Valet> valets;

    public Premise() {
    }

    public Premise(String address, String state) {
        this.address = address;
        this.state = state;
        this.services = new ArrayList<>();
        this.valets = new ArrayList<>();
    }

    public String getMerchant_id() {
        return merchant_id;
    }

    public void setMerchant_id(String merchant_id) {
        this.merchant_id = merchant_id;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public List<Service> getServices() {
        return services;
    }
    public void addService(Service service) {
        services.add(service);
    }

    public List<Valet> getValets() {
        return valets;
    }
    public void addValet(Valet valet) {
        valets.add(valet);
    }

}
