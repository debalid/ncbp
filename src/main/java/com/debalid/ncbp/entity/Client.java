package com.debalid.ncbp.entity;

/**
 * Entity for client. It has one-to-many relationship with Order.
 * Created by debalid on 20.04.2016.
 */
public class Client {
    private Integer id;
    private String title;
    private String phone;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
