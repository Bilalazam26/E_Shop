package com.learn.e_shop.Model;


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Cart implements Serializable {

    private String pid, name, price, quantity, discount;

    public Cart () {

    }

    public Cart(String pid, String name, String price, String quantity, String discount) {
        this.pid = pid;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        String time = simpleDateFormat.format(calendar.getTime());
        return time;
    }
}
