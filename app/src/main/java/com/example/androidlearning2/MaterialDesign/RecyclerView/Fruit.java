package com.example.androidlearning2.MaterialDesign.RecyclerView;

public class Fruit {

    private String name;

    private int imageId;

    public Fruit(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }
    //en

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }
}
