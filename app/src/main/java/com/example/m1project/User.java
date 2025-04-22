package com.example.m1project;

public class User {

    private String name;
    private String email;
    private String phone;
    private String password;
    private String passwordAgain;
    private String city;


    public User(String name, String email, String phone, String password, String city, String passwordAgain) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.passwordAgain=passwordAgain;
        this.city = city;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getCity() {
        return city;
    }

    public String getPasswordAgain()
    {
        return  passwordAgain;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setemail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setPasswordAgain(String passwordAgain){
        this.passwordAgain = passwordAgain;
    }

}
