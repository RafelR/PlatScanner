package com.speedhack.plat.platscanner.model;

public class Komentar {
    private String fullname;
    private String comment;
    private double rating;
    private String datetime;

    public Komentar(String fullname, String comment, double rating) {
        this.fullname = fullname;
        this.comment = comment;
        this.rating = rating;
    }

    public Komentar(){

    }

    public String getFullname() {
        return fullname;
    }

    public String getComment() {
        return comment;
    }

    public double getRating() {
        return rating;
    }

    public String getDatetime() {
        return datetime;
    }
}
