package com.ecommerce.store.app.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Order implements Parcelable {
    private String id;
    private long date;
    private String address;
    private List<Product> products;

    public Order() {
    }

    public Order(String id, long date, String address, List<Product> products) {
        this.id = id;
        this.date = date;
        this.address = address;
        this.products = products;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getTotal() {
        int total = 0;
        for (Product product : products) {
            total += product.getPrice() * product.getQuantity();
        }

        return total;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeLong(this.date);
        dest.writeString(this.address);
        dest.writeList(this.products);
    }

    protected Order(Parcel in) {
        this.id = in.readString();
        this.date = in.readLong();
        this.address = in.readString();
        this.products = new ArrayList<Product>();
        in.readList(this.products, Product.class.getClassLoader());
    }

    public static final Parcelable.Creator<Order> CREATOR = new Parcelable.Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel source) {
            return new Order(source);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };
}
