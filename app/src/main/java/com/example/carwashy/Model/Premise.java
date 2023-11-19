package com.example.carwashy.Model;

import java.util.ArrayList;
import java.util.List;

public class Premise {
    private String address;
    private String state;

    public Premise() {
    }

    public Premise(String address, String state) {
        this.address = address;
        this.state = state;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

}
