package com.example.m1project;

import android.widget.ImageView;

public class Restaurant extends User{

    private String address;
    //

    public Restaurant(String name, String gmail, String phone, String password,String passwordAgain, String city, String address) {
        super(name, gmail, phone, password,city, passwordAgain);
        this.address = address;
    }

    public String getAdress() {
        return address;
    }

    //public ImageView getId(){return this.foodpic;}

    public void setAdress(String adress) {
        this.address = adress;
    }

    //public void setId(String id){this.foodpic = foodpic;}

}
