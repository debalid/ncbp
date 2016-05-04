package com.debalid.ncbp.entity;

import java.util.Date;

/**
 * Entity for Order. It has many-to-one relationship with Client.
 * Created by debalid on 20.04.2016.
 */
public class Order {
    private Long number;
    private Client client;
    private Date date;
    private int priceTotal;

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(int priceTotal) {
        this.priceTotal = priceTotal;
    }
}
