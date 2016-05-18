package com.nupt.shrimp.schoolmarket.model.bean;

/**
 * Created by liucundong on 2015/11/17.
 */
public class ItemModel implements Comparable<ItemModel>{
    public int id;
    public String title;
    public String imageName;
    public double price;
    public String catrgory;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCatrgory() {
        return catrgory;
    }

    public void setCatrgory(String catrgory) {
        this.catrgory = catrgory;
    }


    @Override
    public int compareTo(ItemModel another) {
        return this.id - another.id;
    }
}