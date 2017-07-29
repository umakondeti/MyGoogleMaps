package com.example.umamaheshwari.mygooglemaps;

/**
 * Created by UmaMaheshwari on 1/20/2016.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

public class ProfileActivity extends Activity {


    ImageView iv_back;
    public static CircularImageView profile;
    static ImageView iv_pic;


    private CallbackManager cbm;

    ProgressDialog progress;

    //  Fb  details
    static String ftext, flastname, femail;

    public String profilefbpic;


    //   DB details
    public static String first_name;

    public static String surname;

    public static String email;

    public static String blob_img;
    // fb details

    static TextView tv_fname = null, tv_lname = null, tv_femail = null;

    //db details

    /*
        TextView tv_first_name=null,tv_last_name = null,tv_email = null;;
    */
/*getting  users data from data base reference */
    private DatabaseReference mDatabaseReference;

    private FirebaseUser user;
    private String mUserId;
    Firebase ref;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;

    LinearLayout llv_fb;
    Singleton singleton;
    SessionManager session;
    String username;
    Cursor client_details;
    Bitmap b1;
    String url;
    LoginDataBaseAdapter loginDataBaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.profile);
        init();


        if (AccessToken.getCurrentAccessToken() != null) {
            fbDetails();
        } /*else if(Utilities.mGoogleApiClient.isConnected()){
            ClientDetails();
        }*/ else {

            authListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        // user auth state is changed - user is null
                        // launch login activity
                        //  startActivity(new Intent(ProfileActivity.this, SignInMainActivity.class));
                        finish();
                    }
                }
            };
            if (user != null) {

                mUserId = user.getUid();
                Log.d("muserId", mUserId);
            }
            try {
                setAccountInfo();
            }catch (Exception e)
            {

            }
        }

        iv_back.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(ProfileActivity.this, GoogleMaps.class));
                    }
                });
    }

    public void init() {
        //initialize all view objects

       /* tv_email = (TextView) findViewById(R.id.tv_email);
        tv_first_name = (TextView) findViewById(R.id.tv_first_name);
        tv_last_name = (TextView) findViewById(R.id.tv_last_name);
*/
        llv_fb = (LinearLayout) findViewById(R.id.llv_fb);
/*
        llv_db = (LinearLayout)findViewById(R.id.llv_db);
*/
        tv_fname = (TextView) findViewById(R.id.tv_fname);
        tv_lname = (TextView) findViewById(R.id.tv_lname);
        tv_femail = (TextView) findViewById(R.id.tv_femail);


        loginDataBaseAdapter = new LoginDataBaseAdapter(this);

        cbm = CallbackManager.Factory.create();
        profile = (CircularImageView) findViewById(R.id.iv_pic);
        progress = new ProgressDialog(ProfileActivity.this);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
      //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        iv_back = (ImageView) findViewById(R.id.iv_back);
//get firebase auth instance
        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        singleton = Singleton.getInstance();
        session = new SessionManager(this);
    }

    public void setAccountInfo() {
        if (session.isLoggedIn()) {
            HashMap<String, String> map = session.getUserDetails();
            String username = map.get("display_name");
            String userProfilePic = map.get("login_image");
            String emailId = map.get("email");
            String gender = map.get("gender");
            Log.d("display_name", "" + username);
            Log.d("login_image", userProfilePic);
            Log.d("emailId", emailId);
            Log.d("gender", gender);
            if (userProfilePic != null) {
                try {
                    byte[] decodedString = Base64.decode(userProfilePic, Base64.DEFAULT);
                    Bitmap userBitmapPic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                  //  Bitmap imageBitmap = BitmapFactory.decodeFile(userProfilePic);
                    profile.setImageBitmap(userBitmapPic);
                    //  Picasso.with(ProfileActivity.this).load(userProfilePic).into(profile);
                }catch(Exception e){
                    Log.d("iamin catch", " "+"haha");
                    Picasso.with(ProfileActivity.this).load(userProfilePic).into(profile);
                }
            } else
                profile.setImageResource(R.drawable.maps);
            tv_fname.setText("");
            tv_lname.setText("");
            tv_femail.setText("");
            tv_fname.setText(username);
            tv_lname.setText(gender);
            tv_femail.setText(emailId);

        }
        Log.d("retrieve_users_data", "clicked");

        /*mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mDatabaseReference.child("my_maps_user").child(mUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    Log.d("my_app_user",""+ snapshot);
                    MyMapsUser userData = null;
                    userData= postSnapshot.getValue(MyMapsUser.class);
                    String username = userData.getFirstName();
                    String userProfilePic = userData.getUserProfilePic();
                    String emailId = userData.getEmailId();
                    Log.d("retrieve_username", ""+username);
                    Log.d("retrieve_userProfilePic",""+userProfilePic);
                    Log.d("retrieve_emailId",""+emailId);
                    if (userProfilePic != null) {
                        *//*byte[] decodedString = Base64.decode(userProfilePic, Base64.DEFAULT);
                        Bitmap userBitmapPic = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        profile.setImageBitmap(userBitmapPic);*//*
                        Picasso.with(ProfileActivity.this).load(userProfilePic).into(profile);
                    }
                    else
                        profile.setImageResource(R.drawable.maps);
                    tv_fname.setText("");
                    tv_lname.setText("");
                    tv_femail.setText("");
                    tv_fname.setText(username);
                    tv_lname.setText(userData.getLastName());
                    tv_femail.setText(emailId);
                    break;
    *//* You could extract the values of object using the getter methods
     * and display it in your GUI.

     *  climate.getCity()
     *  climate.getTemperature()
     *  climate.getHumidity()
     *  climate.getPressure()
     **//*
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.d("retrieve_error", firebaseError.getDetails());
       *//*
        * You may print the error message.
               **//*
            }
        });*/
       /* loginDataBaseAdapter = loginDataBaseAdapter.open();
        client_details = loginDataBaseAdapter.getClientDetails(SignInMainActivity.userName);
        Log.d("gbhb", SignInMainActivity.userName);
        if (client_details != null) {
            client_details.moveToFirst();
            do {

                blob_img = client_details.getString(client_details.getColumnIndex("IMAGE"));

                first_name = client_details.getString(client_details.getColumnIndex("FIRSTNAME"));
                surname = client_details.getString(client_details.getColumnIndex("LASTNAME"));
                email = client_details.getString(client_details.getColumnIndex("USERNAME"));
                Log.d("blob_img"," "+blob_img);
            } while (client_details.moveToNext());

            tv_fname.append(first_name);
            tv_lname.append(surname);
            tv_femail.append(email);
            if(blob_img!=null)
                profile.setImageBitmap(BitmapFactory.decodeFile(blob_img));
            else
                profile.setImageResource(R.drawable.maps);

        }
        loginDataBaseAdapter.close();
*/
    }

    public void fbDetails() {
        llv_fb.setVisibility(View.VISIBLE);
        tv_fname.setText("");
        tv_lname.setText("");
        tv_femail.setText("");
        if (MainActivity.email == null) {
            tv_fname.setText(Utilities.strUserName);
            tv_lname.setText(MainActivity.lastname);
            profilefbpic = MainActivity.profile_image;
            Log.d("profilefbpic", "" + profilefbpic);
            if (!TextUtils.isEmpty(profilefbpic)) {
                new FbNumberImage().
                        execute();
            }
        } else {

            loginDataBaseAdapter = loginDataBaseAdapter.open();
            Log.d("gbhb", MainActivity.email);
            client_details = loginDataBaseAdapter.getFbClientDetails(MainActivity.email);
            if (client_details.moveToFirst()) {
                Log.d("gbhb", "" + client_details.getCount());
                Log.d("ftext", "" + client_details.getColumnIndex("FIRSTNAME"));
                ftext = client_details.getString(client_details.getColumnIndex("FIRSTNAME"));
                flastname = client_details.getString(3);
                femail = client_details.getString(client_details.getColumnIndex("USERNAME"));
                client_details.close();

                tv_fname.setText(Utilities.strUserName);
                tv_lname.setText(flastname);
                tv_femail.setText(femail);
            }

            loginDataBaseAdapter.close();

            url = MainActivity.url;
            Log.d("fb_pic_url", "" + url);
            if (!TextUtils.isEmpty(url.trim())) {
                new DownloadImage().execute();
            }
        }
    }

   /* public void ClientDetails()
    {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                details();
            }
        }, 3000);
//        new Details().execute();
    }
*/


  /*  public  void details() {
        progress.dismiss();
        // get Instance  of Database Adapter
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        client_details = loginDataBaseAdapter.getClientDetails(MainActivity.userName);
        Log.d("gbhb", SignInMainActivity.userName);
        if (client_details != null) {
            client_details.moveToFirst();
            do {

                first_name = client_details.getString(client_details.getColumnIndex("FIRSTNAME"));
                surname = client_details.getString(client_details.getColumnIndex("LASTNAME"));
                email = client_details.getString(client_details.getColumnIndex("USERNAME"));


            } while (client_details.moveToNext());

            tv_fname.append(first_name);
            tv_lname.append(surname);
            tv_femail.append(email);

        }
        loginDataBaseAdapter.close();


    }

*/


    class DownloadImage extends AsyncTask<Bitmap, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap bitmap = null;
            try {
                URL url2 = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            profile.setImageBitmap(bitmap);
        }
    }


    class FbNumberImage extends AsyncTask<Bitmap, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap bitmap = null;
            try {
                URL url2 = new URL(profilefbpic);
                HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            profile.setImageBitmap(bitmap);
        }
    }

    public void ClientDetails() {
       /* llv_fb.setVisibility(View.VISIBLE);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        client_details = loginDataBaseAdapter.getGoogleClientDetails(MainActivity.userEmail);
        Log.d("gbhb", MainActivity.userEmail);
        if (client_details != null) {
            Log.d("gbhb", "" + client_details.getCount());
            client_details.moveToNext();
            do {
                Log.d("ftext", "" + client_details.getColumnIndex("FIRSTNAME"));
                ftext = client_details.getString(client_details.getColumnIndex("FIRSTNAME"));
                flastname = client_details.getString(3);
                femail = client_details.getString(client_details.getColumnIndex("USERNAME"));


            } while (client_details.moveToNext());

            tv_fname.setText(ftext);
            tv_lname.setText(flastname);
            tv_femail.setText(femail);

        }
        loginDataBaseAdapter.close();*/
        tv_fname.setText(Utilities.strUserName);
        tv_lname.setVisibility(View.GONE);
        tv_femail.setText(Utilities.strUserEmail);
        new UpdateProfilePicTask().execute(Utilities.strProfielUrl);

    }

    private class UpdateProfilePicTask extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
/*
            Log.e("urll is ",url);
*/
            Bitmap profilePic = null;
            if (url != null && !url.isEmpty()) {
                try {
                    URL downloadURL = new URL(url);
                    HttpURLConnection conn = (HttpURLConnection) downloadURL.openConnection();
                    InputStream is = conn.getInputStream();
                    profilePic = BitmapFactory.decodeStream(is);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return profilePic;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            if (result != null)
                profile.setImageBitmap(result);
        }

    }

}




















































