package com.example.carwashy.Model;

public class Reward {
    private int rewardPoint;
    private String rewardDesc;
    private int discount;

    // Required empty constructor for Firebase
    public Reward() {
    }

    // Constructor with parameters
    public Reward(int rewardPoint, String rewardDesc,int discount) {
        this.rewardPoint = rewardPoint;
        this.rewardDesc = rewardDesc;
        this.discount = discount;
    }

    // Getters
    public int getRewardPoint() {
        return rewardPoint;
    }

    public int getDiscount() {
        return discount;
    }

    public String getRewardDesc() {
        return rewardDesc;
    }
}
