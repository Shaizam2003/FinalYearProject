package com.example.halalcheck3;

public class MenuItem {

    private String itemName;

    private double itemPrice;

    public MenuItem() {
    }

    public MenuItem(String itemName, double itemPrice) {
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }



    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemName() {
        return itemName;
    }


    public double getItemPrice() {
        return itemPrice;
    }
}



