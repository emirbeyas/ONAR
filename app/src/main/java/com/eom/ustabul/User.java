package com.eom.ustabul;

import java.util.ArrayList;

public class User {
    private String name;
    private String surname;
    private String mail;
    private String phone;
    private ArrayList<String> activeCategories;
    private String city;

    public User(){

    }

    public User(String name, String surname, String mail, String phone, ArrayList<String> activeCategories, String city){
        this.name = name;
        this.surname = surname;
        this.mail = mail;
        this.phone = phone;
        this.activeCategories = activeCategories;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<String> getActiveCategories() {
        return activeCategories;
    }

    public void setActiveCategories(ArrayList<String> activeCategories) {
        this.activeCategories = activeCategories;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCity() {
        return city;
    }
}
