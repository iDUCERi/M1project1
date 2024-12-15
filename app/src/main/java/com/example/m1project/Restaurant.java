package com.example.m1project;

public class Restaurant extends User{

    private String adress;
    private String id;

    public Restaurant(String name, String gmail, String phone, String password, String city, String adress) {
        super(name, gmail, phone, password, city);
        this.adress = adress;
        this.id = id;
    }

    public String getAdress() {
        return adress;
    }

    public String getId(){return id;}

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setId(String id){this.id = id;}

}
