package com.example.umamaheshwari.mygooglemaps;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user1 on 24-Feb-17.
 */
public class MyMapsUser {

    private String DisplayName, emailId, userProfilePic, userName, latitude, longitude, date, time, address, city, state, country, Gender, phonennumber, userkey, BuddyName, selectedBuddyName;
    private DatabaseReference mDatabase;
    public ArrayList<String> user_buddies;

    public ArrayList<String> selected_buddies;

    public MyMapsUser(String DisplayName, String Email, String userProfilePic, String gender, String phone_num) {
        this.DisplayName = DisplayName;
        this.emailId = Email;
        this.userProfilePic = userProfilePic;
        this.Gender = gender;
        this.phonennumber = phone_num;
        selected_buddies = new ArrayList<>();
        user_buddies = new ArrayList<>();
    }

    public MyMapsUser(String userName, String latitude, String longitude, String date, String time, String address, String city, String state, String country) {
        this.userName = userName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.date = date;
        this.time = time;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        selected_buddies = new ArrayList<>();
        Log.d("full adress"," "+address +city +state+country);

    }

    public MyMapsUser(String registered_username, String user_key) {
        this.selectedBuddyName = registered_username;
        this.userkey = user_key;
        Log.d("hi_select", selectedBuddyName);
        Log.d("hi_userkey", userkey);
    }

    public void setDisplayName(String DisplayName) {
        this.DisplayName = DisplayName;
    }

    public MyMapsUser() {
        selected_buddies = new ArrayList<>();
        user_buddies = new ArrayList<>();

    }

    public void setEmailId(String email) {
        this.emailId = email;
    }

    public void setGender(String gender) {
        this.Gender = gender;
    }

    public void setPhoneNumber(String phone_num) {
        this.phonennumber = phone_num;
    }

    public void setUserProfilePic(String imageurl) {

        this.userProfilePic = imageurl;
    }

    public void setUserName(String user_name) {
        this.userName = user_name;
    }

    public void setLatitude(String lat) {
        this.latitude = lat;
    }

    public void setLongitude(String lon) {
        this.longitude = lon;

    }

    public void setDate(String dat) {
        this.date = dat;
    }

    public void setTime(String tim) {
        this.time = tim;
    }

    public void setAddress(String addres) {
        this.address = addres;
    }

    public void setCity(String _city) {
        this.city = _city;
    }

    public void setState(String _state) {
        this.state = _state;
    }

    public void setCountry(String _country) {
        this.country = _country;
    }

    public String getUserProfilePic() {
        return userProfilePic;
    }

    public String getGender() {
        return Gender;
    }

    public String getPhoneNumber() {
        return phonennumber;
    }


    public String getEmailId() {
        return emailId;
    }

    String getUserName() {
        return userName;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getCountry() {
        return country;
    }

    String getDisplayName() {
        return DisplayName;
    }

    public Map<String, Object> toMapDetails() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("UserName", userName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("Date", date);
        result.put("time", time);
        result.put("address", address);
        result.put("city", city);
        result.put("state", state);
        result.put("country", country);
        return result;
    }

    public Map<String, Object> toUserDetails() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("DisplayName", DisplayName);
        result.put("userProfilePic", userProfilePic);
        result.put("emailId", emailId);
        result.put("gender", Gender);
        result.put("phoneNumber", phonennumber);
        return result;
    }

    public ArrayList<String> addMyBuddies() {


        return selected_buddies;
    }

    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("latitude", latitude);
        result.put("longitude", longitude);
        result.put("date", date);
        result.put("time", time);
        result.put("address", address);
        result.put("city", city);
        result.put("state", state);
        result.put("country", country);

        return result;
    }

    public Map<String, Object> getRegisteredUsers() {
        HashMap<String, Object> registered_users = new HashMap<>();
        registered_users.put(selectedBuddyName, userkey);

        return registered_users;
    }

    public Map<String, Object> getSelectedBuddies(ArrayList<String> selected_buddies) {
        HashMap<String, Object> selectedBuddies = new HashMap<>();
        this.user_buddies = selected_buddies;
        Log.d("user_buddies_siz", "  " + selected_buddies.size());
        if (user_buddies.size() != 0) {
            for (int i = 0; i < user_buddies.size(); i++) {
                String select_buddies_statement = user_buddies.get(i);
                String[] selected_Buddies = select_buddies_statement.split("~");
                String selectedBuddiesUserName = selected_Buddies[0];
                String selectedBuddyKey = selected_Buddies[1];
                Log.d("modifiedha?", " " + selectedBuddiesUserName + "---->" + selectedBuddyKey);
                selectedBuddies.put(selectedBuddiesUserName, selectedBuddyKey);
                //selectedBuddies.put("userKey",userkey);
                Log.d("add_select", selectedBuddiesUserName);
                Log.d("add_userkey", selectedBuddyKey);
            }
        }
        return selectedBuddies;

    }

    public Map<String, Object> setEmailToUId(String EmailId, String userId, String UserDisplayName, String user_pofile_pic, String phone_num) {

        HashMap<String, Object> allusersinfo = new HashMap<>();
        allusersinfo.put(UserDisplayName, userId + "#" + EmailId + "#" + phone_num + "#" + user_pofile_pic);
        // allusersinfo.put("UserDisplayName",userDisplayName);

        return allusersinfo;
    }

    private void writeNewPost(String userId, String userName, String title, String body) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        MyMapsUser post = new MyMapsUser(DisplayName, userProfilePic, emailId, Gender, phonennumber);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);

        mDatabase.updateChildren(childUpdates);
    }

}
