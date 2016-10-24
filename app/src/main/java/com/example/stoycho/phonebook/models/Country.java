package com.example.stoycho.phonebook.models;

/**
 * Created by Stoycho on 10/20/2016.
 */

public class Country {

    private int id;
    private String countryName;
    private String callingCode;

    public Country(){}

    public Country(String countryName, String callingCode) {
        this.id = id;
        this.countryName = countryName;
        this.callingCode = callingCode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCallingCode() {
        return callingCode;
    }

    public void setCallingCode(String callingCode) {
        this.callingCode = callingCode;
    }
}
