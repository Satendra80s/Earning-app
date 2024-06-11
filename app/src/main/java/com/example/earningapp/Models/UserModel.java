package com.example.earningapp.Models;

public class UserModel {
   private String name, email, mobileNumber, profile, referCode;

    private int coins,spins;

    public UserModel(String name, String email, String mobileNumber, String profile, String referCode, int coins, int spins) {
        this.name = name;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.profile = profile;
        this.referCode = referCode;
        this.coins = coins;
        this.spins = spins;
    }

    public UserModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getReferCode() {
        return referCode;
    }

    public void setReferCode(String referCode) {
        this.referCode = referCode;
    }

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public int getSpins() {
        return spins;
    }

    public void setSpins(int spins) {
        this.spins = spins;
    }
}