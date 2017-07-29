package com.example.umamaheshwari.mygooglemaps.adapter;

/**
 * Created by user1 on 03-Jun-17.
 */

public class Buddies {

    String name;
    String key;
    String id;

    Buddies(String name, String keys,String contact_id) {
        this.name = name;
        this.key = keys;
        this.id=contact_id;
    }
    public Buddies(String name, String keys) {
        this.name = name;
        this.key = keys;
    }
    Buddies(String name) {
        this.name = name;
        //  this.phone_num = phonenumber;
    }

    public String getPhone_num() {
        return key;
    }

    public void setPhone_num(String keys) {
        this.key = keys;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }


    public void setName(String name) {
        this.name = name;
    }

}
