package com.example.m1project;

public class Restaurant extends User{

    private String address;
    private String id;

    public Restaurant(String name, String gmail, String phone, String password,String passwordAgain, String city, String address) {
        super(name, gmail, phone, password,passwordAgain, city);
        this.address = address;
        this.id = id;
    }

    public String getAdress() {
        return address;
    }

    public String getId(){return id;}

    public void setAdress(String adress) {
        this.address = adress;
    }

    public void setId(String id){this.id = id;}

}
