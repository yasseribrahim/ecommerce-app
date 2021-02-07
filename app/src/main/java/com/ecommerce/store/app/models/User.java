package com.ecommerce.store.app.models;

public class User {
    public String fullname, mail, birtydate, jop, gender;

    public User() {
    }

    public User(String Fullname, String Mail, String Birtydate, String Jop, String Gender) {
        this.fullname = Fullname;
        this.mail = Mail;
        this.birtydate = Birtydate;
        this.jop = Jop;
        this.gender = Gender;
    }
}
