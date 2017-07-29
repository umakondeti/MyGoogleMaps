package com.example.umamaheshwari.mygooglemaps;

/**
 * Created by user1 on 12-Oct-16.
 */

public class Contacts {

    String name;
    String phone_num;
    String id;

    Contacts(String name, String phonenumber,String contact_id) {
        this.name = name;
        this.phone_num = phonenumber;
        this.id=contact_id;
    }
    Contacts(String name, String phonenumber) {
        this.name = name;
        this.phone_num = phonenumber;
    }
    Contacts(String name) {
        this.name = name;
      //  this.phone_num = phonenumber;
    }

    public String getPhone_num() {
        return phone_num;
    }

    public void setPhone_num(String phone_no) {
        this.phone_num = phone_no;
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
