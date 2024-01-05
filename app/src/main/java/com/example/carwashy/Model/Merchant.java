package com.example.carwashy.Model;

public class Merchant {
    private String email;
    private String password;
    private String phonenumber;

    public Merchant() {
    }

    public Merchant(String email, String password, String phonenumber) {
        this.email = email;
        this.password = password;
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

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}
