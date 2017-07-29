package com.example.umamaheshwari.mygooglemaps;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {
    SessionManager session;
    // firebase auth
    private FirebaseAuth auth;
    private FirebaseUser mFirebaseuser;
    public DatabaseReference mDatabaseReference;
    public DatabaseReference mRootRef;
    private static int SPLASH_TIME_OUT = 1000;
    Utilities utilities;
    private String mUserId;
    ContactsListAdapter buddies_adapter;
    long added_buddies = 0;
    long total_buddies = 0;
    Firebase ref;
    BuddiesActivity buddies_activity;
    LoginDataBaseAdapter loginDataBaseAdapter;
    private static String url = "https://maps-eb9bd.firebaseio.com/my_maps_user/emailToUid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        mFirebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        utilities = new Utilities(this);
        Utilities.registeredUsersArrayList.clear();
        Firebase.setAndroidContext(SplashScreen.this);
        buddies_activity = new BuddiesActivity();
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/emailToUid");
        // lv_buddies = (ListView) findViewById(R.id.lv_buddies);
       /* new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/
        try {
            loginDataBaseAdapter = new LoginDataBaseAdapter(this);
            loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.deleteAllRegisteredUsersEntry();
            loginDataBaseAdapter.close();
        } catch (Exception e) {
            Utilities.showProgressDialog(this, " go to settings-->app-->clear cache and data");
        }
        getAllRegisteredUsers();
           /* }
        }, SPLASH_TIME_OUT);*/
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
                finish();
                // close this activity
                //finish();
            }
        }, 4000);
    }

    private void getAllRegisteredUsers() {
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        Firebase.setAndroidContext(SplashScreen.this);
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/");

        mRootRef = FirebaseDatabase.getInstance().getReference();
        // userDisplayName = session.getLogInUserDisplayName();
        //  Log.d("getting_username", "" + userDisplayName);
        Query mDatabaseReference_query = mRootRef.child("my_maps_user").child("emailToUid");
        Log.d("url", "" + mDatabaseReference_query);
       /* Utilities.registered_users.clear();
        Log.d("calling volley", "-------->" + url);
        StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("response", "--->" + response);
                        try {

                            Log.d("inside_volley", "-------->");

                           *//* JSONArray jArray = new JSONArray(response);
                            String sectionId = null, sectionName = null,ImageUrl=null;


                            for (int i = 0; i < jArray.length(); i++) {

                                JSONObject jsonobject = jArray.getJSONObject(i);


                                //check the condition key exists in jsonResponse
                                if (jArray.getJSONObject(i).has("CategoryId")) {

                                    sectionId = jsonobject.getString("CategoryId");
                                } else {

                                }
                                if (jArray.getJSONObject(i).has("CategoryName")) {
                                    sectionName = jsonobject.getString("CategoryName");
                                }
                                if (jArray.getJSONObject(i).has("ImageUrl")) {
                                    ImageUrl = jsonobject.getString("ImageUrl");
                                }
*//*

                            // singleton.categoriesData.add(sectionId + "-~-" + sectionName+"-~-"+ImageUrl);


                            //}


                            Intent i = new Intent(SplashScreen.this, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            finish();

                        } catch (Exception e) {

                            Log.d("Splash screen catch", "--->" + e.getMessage());
                        }


                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("volley_error",""+error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();

                *//*Log.d("category error", "--->" + message);*//*
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

           *//* @Override
            public byte[] getBody() throws AuthFailureError {

                return mRequestBody.getBytes();
            }*//*
           @Override
           public Map<String, String> getHeaders() throws AuthFailureError {
               HashMap<String, String> params = new HashMap<String, String>();
               params.put("Content-Type", "application/json");
               String creds = String.format("%s:%s","username","password");
               String auth = "Basic " + Base64.encodeToString(creds.getBytes(), Base64.DEFAULT);
               params.put("Authorization", auth);
               return params;

           }
        };

        Volley.newRequestQueue(SplashScreen.this).add(postRequest);





*/


        mDatabaseReference_query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                getDetails(dataSnapshot);

                //buddies_adapter = new ContactsListAdapter(getApplicationContext(), Utilities.registered_users);
                // lv_buddies.setAdapter(buddies_adapter);
                // }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                getDetails(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                getDetails(dataSnapshot);

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                getDetails(dataSnapshot);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("database error", databaseError.getMessage());
            }
        });

        // loginDataBaseAdapter.close();
    }

    private void getDetails(DataSnapshot dataSnapshot) {
    /*    buddies_activity.progress_buddies=(ProgressBar)buddies_activity.findViewById(R.id.pb_buddies);
        buddies_activity.progress_buddies.setVisibility(View.GONE);*/
        Log.d("BUDDY_shot", "" + dataSnapshot.getKey());
        Log.d("buddy_svalue", "" + dataSnapshot.getValue());
        added_buddies++;
        // loginDataBaseAdapter.insertRegisteredUsersEntry();
        Log.d("total_buddies", "" + total_buddies);
        Log.d("added_buddies", "" + added_buddies);
        try {
            String users_details = "" + dataSnapshot.getValue();
            String[] allUsersData = users_details.split("#");
            String username = dataSnapshot.getKey();
            String username_key = allUsersData[0];
            String user_email = allUsersData[1];
            String user_phone_num = allUsersData[2];
            Contacts users = new Contacts("" + dataSnapshot.getKey(), username_key);
            Utilities.registeredUsersArrayList.add(users);

            Log.d("geting_data", "" + username + "--~--" + username_key + "--~--" + user_email + "--~--" + user_phone_num);

            loginDataBaseAdapter.insertRegisteredUsersEntry(username, user_phone_num);
            Log.d("user_details", users_details);
            // if (total_buddies == added_buddies) {
            Log.d("inside", "" + added_buddies);
            Log.d("size", "" + Utilities.registeredUsersArrayList.size());
        } catch (Exception e) {
            utilities.showAlert(e.getMessage());
        }


    }


}
