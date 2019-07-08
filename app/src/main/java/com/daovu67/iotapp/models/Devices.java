package com.daovu67.iotapp.models;

public class Devices {
    private String name;
    private String data;
    private int image;
    private int color;

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public Devices(String name, String data, int image, int color) {
        this.name = name;
        this.data = data;
        this.image = image;
        this.color = color;
    }

    public int getImage() {
        return image;
    }


    public void setImage(int image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

}
