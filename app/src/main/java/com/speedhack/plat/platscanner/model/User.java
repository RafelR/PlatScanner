package com.speedhack.plat.platscanner.model;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String fullname;
    private String email;
    private String dateBirth;
    private String gender;
    private String phoneNumber;
    private String brand;
    private String plat;
    private float rating=0;
    private int totalRating=0;
    private int totalRated=0;
    private ArrayList<Komentar> comments=null;
    private ArrayList<Komentar> toComments=null;


    public User(String fullname, String email, String dateBirth, String gender, String phoneNumber, String brand, String plat) {
        this.fullname = fullname;
        this.email = email;
        this.dateBirth = dateBirth;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.brand = brand;
        this.plat = plat;
    }

    public User(){

    }


    public String getFullname() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public String getDateBirth() {
        return dateBirth;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getBrand() {
        return brand;
    }

    public String getPlat() {
        return plat;
    }

    public float getRating() {
        return rating;
    }

    public int getTotalRating() {
        return totalRating;
    }

    public int getTotalRated() {
        return totalRated;
    }

    public ArrayList<Komentar> getToComments() {
        return toComments;
    }

    public ArrayList<Komentar> getComments() {
        return comments;
    }


}
