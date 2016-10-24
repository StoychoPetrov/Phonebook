package com.example.stoycho.phonebook.models;

/**
 * Created by Stoycho on 10/20/2016.
 */

public class User {

    private int id;
    private String firstName;
    private String lastName;
    private int countryId;
    private String email;
    private String phoneNumber;
    private String gender;

    public User(){}

    public User(String firstName, String lastName, int countryId, String email, String phoneNumber, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.countryId = countryId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getCountry() {
        return countryId;
    }

    public void setCountry(int countryId) {
        this.countryId = countryId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String male) {
        this.gender = male;
    }
}
