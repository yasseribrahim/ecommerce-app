package com.ecommerce.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Product implements Parcelable {
    private String id;
    private String image;
    private Long price;
    private String name;
    private Long quantity;

    public Product() {
    }

    public Product(String id, String image, Long price, String name, Long quantity) {
        this.id = id;
        this.image = image;
        this.price = price;
        this.name = name;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.image);
        dest.writeValue(this.price);
        dest.writeString(this.name);
        dest.writeValue(this.quantity);
    }

    protected Product(Parcel in) {
        this.id = in.readString();
        this.image = in.readString();
        this.price = (Long) in.readValue(Long.class.getClassLoader());
        this.name = in.readString();
        this.quantity = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<Product> CREATOR = new Parcelable.Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}