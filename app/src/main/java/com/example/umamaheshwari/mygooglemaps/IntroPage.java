package com.example.umamaheshwari.mygooglemaps;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by chandana on 09-01-2016.
 */
public class IntroPage extends FragmentActivity {
    int[] mResources = {
            R.drawable.first_one,
            R.drawable.intro_three,
            R.drawable.third,
            R.drawable.introduction,
    };
    ViewPager mViewPager;
    Cursor client_details;
    ArrayList<TextView> circles = new ArrayList<TextView>();
    Button btn_home;
    LoginDataBaseAdapter loginDataBaseAdapter;
    TextView[] pageInidcator;
    TextView tv_welcome;
    String fname, lname, femail;
    LinearLayout ll_page_indicator;
    static int pageposition;
    Handler handler = new Handler();
    SessionManager session;
    String displayName;
    Singleton singleton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;
    private String mUserId;
    GoogleMaps google_maps;
    Utilities utilities;
    TrackMyBuddy trackMyBuddy;
    Firebase ref;
RelativeLayout introRootRelativeLayout;
    private Context context;
    private Activity activity;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_page);
        CustomPagerAdapter mCustomPagerAdapter = new CustomPagerAdapter(this);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        singleton = Singleton.getInstance();
        introRootRelativeLayout=(RelativeLayout)findViewById(R.id.rll_intro_root);
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mCustomPagerAdapter);
        utilities= new Utilities(this);
        trackMyBuddy = new TrackMyBuddy();
        session = new SessionManager(this);
        context = getApplicationContext();
        view=introRootRelativeLayout;
        activity = this;
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS is Enabled in your device", Toast.LENGTH_SHORT).show();
            if(checkPermission())
            {
                Snackbar.make(view,"Permission already granted.",Snackbar.LENGTH_LONG).show();

            }else
            {
                requestPermission();
            }
        } else {
            showGPSDisabledAlertToUser();

        }


        btn_home = (Button) findViewById(R.id.btn_home);
        ll_page_indicator = (LinearLayout) findViewById(R.id.ll_page_indicator);
        tv_welcome = (TextView) findViewById(R.id.tv_welcome);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize Database Reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        google_maps = new GoogleMaps();
        //tv_welcome.setText(null);
      /*  if (Utilities.strUserName == null) {
            if (SignInMainActivity.userName != null) {*/
         if (session.isLoggedIn()) {
        displayName = session.getLogInUserDisplayName();
             //trackMyBuddy.displayUserBudddies();
        Log.d("userDisplayName",displayName);
        }else
         {
             startActivity(new Intent(IntroPage.this, SignInMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
         }
               /* loginDataBaseAdapter = loginDataBaseAdapter.open();
                client_details = loginDataBaseAdapter.getClientDetails(SignInMainActivity.userName);
                if (client_details.moveToFirst()) {
                    Log.d("gbhb", "" + client_details.getCount());
                    Log.d("ftext", "" + client_details.getColumnIndex("FIRSTNAME"));
                    fname = client_details.getString(client_details.getColumnIndex("FIRSTNAME"));
                    lname = client_details.getString(client_details.getColumnIndex("LASTNAME"));
                    femail = client_details.getString(client_details.getColumnIndex("USERNAME"));
                    client_details.close();
                }

                loginDataBaseAdapter.close();*/
               try {
                   Log.d("displayname", " " + displayName);
                   if (displayName.equalsIgnoreCase("null")) {
                       tv_welcome.setText("Welcome to Google Maps!");
                       auth.signOut();
                       session.logoutUser();
                       singleton.setLoginUserDisplayName(null);
                       singleton.setLoginUserEmailId(null);
                       singleton.setLoginActiveStatus(null);
                       //google_maps.signOut();
                       Toast.makeText(getApplicationContext(), "Something went wrong, Please Login Again", Toast.LENGTH_SHORT).show();
                       startActivity(new Intent(IntroPage.this, SignInMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                               .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                   } else {
                       tv_welcome.setText("Welcome " + displayName + "!");
                   }
               }catch (Exception e) {
               Log.d("exce[ption_raised",""+e.getMessage());
                   tv_welcome.setText("Welcome to Google Maps!");
                   auth.signOut();
                   session.logoutUser();
                   singleton.setLoginUserDisplayName(null);
                   singleton.setLoginUserEmailId(null);
                   singleton.setLoginActiveStatus(null);
                  // google_maps.signOut();
//Log.d("auth",""+auth.getCurrentUser().getUid());
                   Toast.makeText(getApplicationContext(), "Something went wrong, Please Login Again", Toast.LENGTH_SHORT).show();
                   startActivity(new Intent(IntroPage.this, SignInMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                           .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
               }
            /*} else {
                tv_welcome.setText("Something goes Wrong!!,Please Log in again....  ");
                finish();
            }

        } else {*/
        //  tv_welcome.setText("Welcome " + Utilities.strUserName + "!");


        pageIndicator();

        btn_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(IntroPage.this, GoogleMaps.class);
                startActivity(in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        handler.postDelayed(new Runnable() {
            int item_count = 0;

            @Override
            public void run() {
                item_count++;
                item_count = item_count > 4 ? 0 : item_count;
                mViewPager.setCurrentItem(item_count);

                handler.postDelayed(this, 4000);
            }
        }, 4000);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("scrolling", "" + position);
            }

            @Override
            public void onPageSelected(int position) {
                pageposition = position;
                Log.d("position", "" + position);
                /*if (position > 0) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        drawable = getResources().getDrawable(R.drawable.whiteborder, getTheme());

                    } else {
                        drawable = getResources().getDrawable(R.drawable.whiteborder);

                    }
                    button.setBackground(drawable);
                    textView.setTextColor(Color.parseColor("#FFFFFF"));
                    changeIndicatorColorWhite();
//                    pageInidcator[position].setTextSize(30);
                } else if (position == 0) {
                    if (Build.VERSION.SDK_INT >= 21) {
                        drawable = getResources().getDrawable(R.drawable.roundcorners, getTheme());
//                        changeIndicatorColor();
                    } else {
                        drawable = getResources().getDrawable(R.drawable.roundcorners);

                    }
                    button.setBackground(drawable);
                    textView.setTextColor(Color.parseColor("#008CC9"));
//                    pageInidcator[position].setTextSize(30);
                    changeIndicatorColor();
                }*/
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d("state", "" + state);
                for (int dotSizes = 0; dotSizes < pageInidcator.length; dotSizes++) {
                    if (dotSizes == pageposition) {
                        pageInidcator[dotSizes].setTextSize(30);
                    } else {
                        pageInidcator[dotSizes].setTextSize(20);
                    }

                }
               /* if( state==viewPager. SCROLL_STATE_SETTLING)
                *//*{
                    Log.d("plength",""+pageInidcator.length);

                   for(int dotsize=0;dotsize<pageInidcator.length;dotsize++)
                    {
                       int preposition= pageposition+1;
                        if(dotsize==preposition)
                        {

                            Log.d("dotsize",""+pageposition);
                            pageInidcator[preposition].setTextSize(30);
                        }
                        else
                        {
                            pageInidcator[dotsize].setTextSize(20);
                        }
                        if(preposition>pageInidcator.length)
                        {
                             preposition= pageposition-1;
                            pageInidcator[preposition].setTextSize(30);
                        }
                   }

                *//*}*/
            }
        });
        //setAccountInfo();
    }



    public void pageIndicator() {
        Log.d("indication", "u re in page");
        pageInidcator = new TextView[4];
        for (int i = 0; i < 4; i++) {

            pageInidcator[i] = new TextView(this);
            if (i == 0) {
                pageInidcator[i].setText(Html.fromHtml("&#8226"));
                pageInidcator[i].setTextSize(30);
                pageInidcator[i].setTextColor(Color.parseColor("#181818"));
//                pageInidcator[i].setTextColor(Color.parseColor("#FFFFFF"));
            } else {
//            pageInidcator[i].setText("hello:::");
                pageInidcator[i].setText(Html.fromHtml("&#8226"));
                pageInidcator[i].setTextSize(20);
//                pageInidcator[i].setTextColor(Color.parseColor("#FFFFFF"));
                pageInidcator[i].setTextColor(Color.parseColor("#181818"));
            }
            Log.d("pageindicator", "" + pageInidcator[i]);
            ll_page_indicator.addView(pageInidcator[i]);
        }
    }

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){

            return true;

        } else {

            return false;

        }
    }

    private void requestPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,Manifest.permission.ACCESS_FINE_LOCATION)){
            utilities.showAlert("GPS permission allows us to access location data. Please allow in App Settings for additional functionality.");
            Toast.makeText(context,"GPS permission allows us to access location data. Please allow in App Settings for additional functionality.",Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Snackbar.make(view,"Permission Granted, Now you can access location data.",Snackbar.LENGTH_LONG).show();

                } else {

                    Snackbar.make(view,"Permission Denied, You cannot access location data.",Snackbar.LENGTH_LONG).show();

                }
                break;
        }
    }
    private void showGPSDisabledAlertToUser(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Goto Settings Page To Enable GPS",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int id){
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    class CustomPagerAdapter extends PagerAdapter {

        Context mContext;
        LayoutInflater mLayoutInflater;

        public CustomPagerAdapter(Context context) {
            mContext = context;
            mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return mResources.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((LinearLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View itemView = mLayoutInflater.inflate(R.layout.pager_item, container, false);

            ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
            imageView.setImageResource(mResources[position]);

            container.addView(itemView);

            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }
    }
}
