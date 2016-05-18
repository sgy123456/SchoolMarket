package com.nupt.shrimp.schoolmarket.model.bean;

/**
 * Created by liucundong on 2015/11/17.
 */
public class BuyCartItem implements Comparable<BuyCartItem> {
    public int id;
    public String title;
    public String imageName;
    public double price;
    public String catrgory;
    public int num;
    private boolean isChecked = false;

    public boolean getIsChecked() {
        return isChecked;
    }
    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }

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
    public int compareTo(BuyCartItem another) {
        return this.id - another.id;
    }
}