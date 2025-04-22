package com.example.m1project;

import java.sql.Time;

public class Order extends User{

    private Time timeOrderd;
    private String delivery_destination;
    private String delivery_time;
    private String order_content;

    public Order(String name, String gmail, String phone, String password, String city,String passwordAgain, Time timeOrderd, String delivery_destination, String delivery_time, String order_content) {
        super(name, gmail, phone, password,passwordAgain, city);
        this.timeOrderd = timeOrderd;
        this.delivery_destination = delivery_destination;
        this.delivery_time = delivery_time;
        this.order_content = order_content;
    }

    public Time getTimeOrderd() {
        return timeOrderd;
    }

    public void setTimeOrderd(Time timeOrderd) {
        this.timeOrderd = timeOrderd;
    }

    public String getDelivery_destination() {
        return delivery_destination;
    }

    public void setDelivery_destination(String delivery_destination) {
        this.delivery_destination = delivery_destination;
    }

    public String getDelivery_time() {
        return delivery_time;
    }

    public void setDelivery_time(String delivery_time) {
        this.delivery_time = delivery_time;
    }

    public String getOrder_content() {
        return order_content;
    }

    public void setOrder_content(String order_content) {
        this.order_content = order_content;
    }

}
