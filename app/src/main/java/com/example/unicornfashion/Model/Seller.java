package com.example.unicornfashion.Model;

public class Seller {
    private String company,date,image,pid,seller_name,time,type;

    public Seller() {

    }

    public Seller(String company, String date, String image, String pid, String seller_name, String time, String type) {
        this.company = company;
        this.date = date;
        this.image = image;
        this.pid = pid;
        this.seller_name = seller_name;
        this.time = time;
        this.type = type;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getSeller_name() {
        return seller_name;
    }

    public void setSeller_name(String seller_name) {
        this.seller_name = seller_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
