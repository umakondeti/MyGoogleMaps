package com.example.umamaheshwari.mygooglemaps;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user1 on 10/12/2016.
 */

public class Singleton {
    private static Singleton _singleton = null;
    private boolean online;
    public static String dealsType;
    public static String selectedSubCategory;
    public String singleActivityData;
    public String descriptionData;
    public String loginUserId;
    public int screenSize;
    public String LoginUserDisplayName = "";
    public String LoginUserEmailId,LoginUserId;
    public String LoginStatus;
    public String LoginImage, username, latitude, longitude, date, time, address, city, state, country, loginUserAddress, firstname, lastname;
    public String allDateValues, selectedDateValues;
    public static String insertLikeStaus;
    public static String active_login;
    public static boolean ReloadPage,trackingStatus;
    public String profileData,selectedUserEmailId;

    public static boolean editProfileSaveStatus = false, profileActivityBackPressedStatus = false;

    public static List<String> categoriesData = new ArrayList<String>();
    public static List<String> subCategoriesData = new ArrayList<String>();
    public List<String> hottestDeals = new ArrayList<String>();
    public List<String> latestDeals = new ArrayList<String>();
    public ArrayList<String> user_likes_data = new ArrayList<String>();
    public static List<String> userProfileData = new ArrayList<String>();

    boolean RefreshPage = false;

    public static Singleton getInstance() {
        if (_singleton == null) {
            _singleton = new Singleton();
        }
        return _singleton;
    }

    public static boolean isReloadPage() {
        return ReloadPage;
    }

    public void setUsername(String user_name) {
        this.username = user_name;
    }

    public String getProfileData() {
        return profileData;
    }

    public void setFirstName(String firstName) {
        this.firstname = firstName;
    }

    public void setSelectedUserEmailId(String sel_email) {
        this.selectedUserEmailId = sel_email;
    }
public String getSelUserEmailId(){return  selectedUserEmailId;}
    public void setLastName(String lastName) {
        this.lastname = lastName;
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
    public static   void setTrackingStatus(boolean login_status) {
        trackingStatus = login_status;
    }
    public static boolean getTrackingStatus() {
        return trackingStatus;
    }

    public void setState(String _state) {
        this.state = _state;
    }

    public void setCountry(String _country) {
        this.country = _country;
    }


    public String getUsername() {
        return username;
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

    public String getFirstName() {
        return firstname;
    }

    public String getLastName() {
        return lastname;
    }


    public void setLoginImage(String loginImage) {
        LoginImage = loginImage;
    }

    public String getLoginStatus() {
        return LoginStatus;
    }

    public void setLoginStatus(String loginStatus) {
        LoginStatus = loginStatus;
    }

    public String getLoginUserDisplayName() {
        return LoginUserDisplayName;
    }

    public void setLoginUserDisplayName(String loginUserDisplayName) {
        LoginUserDisplayName = loginUserDisplayName;
    }


    public String getLoginUserEmailId() {
        return LoginUserEmailId;
    }

    public void setLoginUserEmailId(String loginUserEmailId) {
        LoginUserEmailId = loginUserEmailId;
    }

    public String getLoginUserId() {
        return loginUserId;
    }

    public void setLoginUserId(String loginUserId) {
        this.loginUserId = loginUserId;
    }

    public void setLoginUserAddress(String login_address) {
        this.loginUserAddress = login_address;
    }

    public String getLoginUserAddress() {
        return loginUserAddress;
    }

    public String getDescriptionData() {
        return descriptionData;
    }

    public void setDescriptionData(String descriptionData) {
        this.descriptionData = descriptionData;
    }

    public static String getSelectedCategoryId() {
        return selectedSubCategory;
    }

    public static void setSelectedCategoryId(String selectedSubCategory) {
        Singleton.selectedSubCategory = selectedSubCategory;
    }

    public boolean isOnline() {
        return online;
    }

    public static String getDealsType() {
        return dealsType;
    }

    public String getLoginImage() {
        return LoginImage;
    }

    public static void setDealsType(String dealsType) {
        Singleton.dealsType = dealsType;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setAllDateValues(String all_date_values) {
        this.allDateValues = all_date_values;
    }

    public void setSelectedDateValues(String sel_date_values) {
        this.selectedDateValues = sel_date_values;
    }

    public String getSelectedDateValues() {
        return selectedDateValues;
    }

    public String getAllDateValues() {
        return allDateValues;
    }

    public String getSingleActivityData() {
        return singleActivityData;
    }

    public void setSingleActivityData(String singleActivityData) {
        this.singleActivityData = singleActivityData;
    }

    public boolean isRefreshPage() {
        return RefreshPage;
    }

    public void setRefreshPage(boolean refreshPage) {
        RefreshPage = refreshPage;
    }


    public void setLoginActiveStatus(String loginActiveStatus) {
        active_login = loginActiveStatus;
    }

    public String getLoginActiveStatus() {
        return active_login;
    }

    public int getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(int screenSize) {
        this.screenSize = screenSize;
    }

    public void setInsertLikeStatus(String insertLikeStaus) {
        Singleton.insertLikeStaus = insertLikeStaus;
    }

    public String getInsertLikeStatus() {
        return insertLikeStaus;
    }


}
