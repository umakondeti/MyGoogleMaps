package com.example.umamaheshwari.mygooglemaps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;


public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	
	// Editor for Shared preferences
	Editor editor;
	
	// Context
	Context _context;

	
	// Shared pref mode
	int PRIVATE_MODE = 0;
	
	// Sharedpref file name
	private static final String PREF_NAME = "DealsWebUserInfo";
	
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	
	// User name (make variable public to access from outside)
	public static final String KEY_DISPLAY_NAME = "display_name";
	public static final String KEY_GENDER = "gender";

	// Email address (make variable public to access from outside)
	public static final String KEY_EMAIL = "email";
	public static final String KEY_IMAGE = "login_image";
	public static final String KEY_PHONE_NUMBER = "phone_number";


	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	
	/**
	 * Create login session
	 * */
	public void createLoginSession(String display_name,String email,String profile_pic_user,String gender,String phone_numer){


		// Storing name in pref
		editor.putBoolean(IS_LOGIN, true);

		editor.putString(KEY_DISPLAY_NAME, display_name);
		editor.putString(KEY_GENDER, gender);
		editor.putString(KEY_PHONE_NUMBER, phone_numer);

		// Storing email in pref
		editor.putString(KEY_EMAIL, email);
		editor.putString(KEY_IMAGE, profile_pic_user);
		// commit changes
		editor.commit();
	}	
	
	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, SignInMainActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			
			// Staring Login Activity
			_context.startActivity(i);
		}
		
	}
	
	
	
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_DISPLAY_NAME, pref.getString(KEY_DISPLAY_NAME, null));
		user.put(KEY_GENDER, pref.getString(KEY_GENDER, null));
		// user email id
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		user.put(KEY_IMAGE, pref.getString(KEY_IMAGE, null));
		// return user
		return user;
	}

	public  String getLogInUserDisplayName(){
		// user name
		String userDisplayName= pref.getString(KEY_DISPLAY_NAME, null);

		// return userDisplayName
		return userDisplayName;
	}
	/**
	 * Clear session details
	 * */
	public void logoutUser(){
		// Clearing all data from Shared Preferences

		editor.clear();
		editor.commit();
		
		/*// After logout redirect user to Loing Activity
		Intent i = new Intent(_context, Login_Activity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);*/
	}
	
	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}
