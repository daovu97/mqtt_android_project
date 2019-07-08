package com.daovu67.iotapp.models;

public class Card {
    private Devices device;
    private String cardName;
    private String status;
    private int cardImage;
    private int Color;

    public String getStatus() {
        return device.getData();
    }


    public String getCardName() {
        return cardName;
    }

    public void setCardName(String name) {
        cardName = name;
    }


    public int getCardImage() {
        return device.getImage();
    }


    public int getColor() {
        return device.getColor();
    }

    public void setColor(int color) {
        Color = color;
    }

    public Devices getDevice() {
        return device;
    }

    public void setDevice(Devices device) {
        this.device = device;
    }

    public Card(Devices device, String name) {
        this.device = device;
        cardName = name;
    }
}
