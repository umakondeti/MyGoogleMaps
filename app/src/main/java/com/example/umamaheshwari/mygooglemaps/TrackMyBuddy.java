package com.example.umamaheshwari.mygooglemaps;

/**
 * Created by UmaMaheshwari on 1/18/2016.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umamaheshwari.mygooglemaps.Interfaces.getSelectedDateListener;
import com.example.umamaheshwari.mygooglemaps.adapter.Buddies;
import com.example.umamaheshwari.mygooglemaps.adapter.UserBuddiesAdapter;
import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
//import com.google.android.gms.appdatasearch.GetRecentContextCall;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.READ_CONTACTS;
import static android.util.TypedValue.*;


public class TrackMyBuddy extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    public static final String TAG = GoogleMaps.class.getSimpleName();
    ImageView iv_click, iv_arrow;
    // Google Map
    List<Address> addresses;
    Geocoder geocoder;
    Cursor client_details;
    double currentLatitude;
    Context mContext;
    double currentLongitude;
    Location mCurrentLocation;
    static Location location;
    String resp;
    RelativeLayout selectBuddyToTrackRelativeLayout;
    LoginDataBaseAdapter loginDataBaseAdapter;
    Dialog dialog;
    Singleton singleton;
    CheckBox ckbox;
    MyMapsUser track_user;
    Utilities utilities;
    String firstName, lastName;
    protected LocationManager locationManager;
    LinearLayout ll_profile, ll_contacts, ll_location_history, ll_messages, ll_logout;
    static String d_time, time, UserName, address, city, state, country, date, userName;
    String mLastUpdateTime;
    String l_address, l_city, l_state, l_country, contact_phonenumber;
    static double lat, lon;
    PolylineOptions polyLineOptions;
    SharedPreferences sharedPreferences;
    SupportMapFragment fm;
    String track_user_buddy_name;
    String track_user_buddy_key;
    private ArrayList<LatLng> arrayPoints = null;
    LinearLayout llv_tracklayout;
    boolean isGPSEnabled, isNetworkEnabled = false;
    static String j_address1, j_address2, j_city, j_state, j_country, j_pin;
    SessionManager session;
    // firebase auth
    private FirebaseAuth auth;
    private FirebaseUser mFirebaseuser;
    public DatabaseReference mDatabaseReference;
    private String mUserId;
    private DatabaseReference mRootRef;
    DatabaseReference googleMapsRef, googleMapsRefs;
    UserBuddiesAdapter user_buddies_adapter;
    Button trackMyBuddy;
    int value_count = 0;
    Firebase ref;
    int c = 0;
    TextView track_Name, track_user_key;
    String selectedBuddyName;
    TextView getYourBuddies;
    String selecteUserkey;
    boolean isInTracking=false;
    private AddBuddyChildReciever receiver;
    int all_user_buddies_count = 0, retreive_user_buddy_count = 0;
    /*
        private static final String TAG = "LocationActivity";
    */
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; //

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private Marker myMarker;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    PendingIntent pendingintent;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    getSelectedDateListener listeners;
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 2; // 1 minute
    private static final int PICK_CONTACT = 0;
    private static final int REQUEST_READ_CONTACTS = 0;
    RelativeLayout rll_home;
    public boolean gotDetails = false;
    String selected_date;
    int selected_date_child_count;
    int all_date_child_count = 0;
    String userDisplayName;
    EditText search_buddy;
    Button btnTrack;
    RelativeLayout rll_search_buddy;
    PolylineOptions polylineOptions;
    String selected_dates;
    SearchView user_buddies_search_view;
    ListView listview_buddies;
    ProgressBar gettingBuddiesProgressBar;
    String searchBuddyUserId, searchBuddyEmailId;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    String search_buddy_uid;
    int value_counts;
    String search_emailID;
    LatLng latLng;
    ProgressBar progressBar;
    SearchView search_user_buddies;
    TextView trakingBuddyTextView;
    RadioButton radioStartButton, radioStopButton;
    int get_all_details_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_mybuddy);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        rll_home = (RelativeLayout) findViewById(R.id.rll_home);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        radioStartButton = (RadioButton) findViewById(R.id.rb_start);
        radioStopButton = (RadioButton) findViewById(R.id.rb_stop);
        sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
        arrayPoints = new ArrayList<LatLng>();
        polylineOptions = new PolylineOptions();
        listview_buddies = (ListView) findViewById(R.id.listview_buddies);
        progressBar = (ProgressBar) findViewById(R.id.pb_trackmybuddy);
        listview_buddies.setOnItemClickListener(this);
        singleton = Singleton.getInstance();
        session = new SessionManager(getApplicationContext());
        getYourBuddies = (TextView) findViewById(R.id.tv_getting_buddies);
        gettingBuddiesProgressBar = (ProgressBar) findViewById(R.id.pb_getting_buddies);
        gettingBuddiesProgressBar.setVisibility(View.GONE);
        selectBuddyToTrackRelativeLayout = (RelativeLayout) findViewById(R.id.select_buddy_totrack);
        user_buddies_search_view = (SearchView) findViewById(R.id.user_buddy_search_view);
        search_user_buddies = (SearchView) findViewById(R.id.user_buddy_search_view);
        search_user_buddies.setOnQueryTextListener(this);
        AutoCompleteTextView search_text = (AutoCompleteTextView) user_buddies_search_view.findViewById(user_buddies_search_view.getContext().getResources().getIdentifier("android:id/search_src_text", null, null));
        search_text.setTextSize(COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.text_small));

        trakingBuddyTextView = (TextView) findViewById(R.id.tv_combine);
        radioStartButton.setOnClickListener(myOptionOnClickListener);
        radioStopButton.setOnClickListener(myOptionOnClickListener);
        utilities = new Utilities(this);
        track_user = new MyMapsUser();
        Utilities.prevValueCounts=0;
        // track_user.user_buddies.clear();
       /* if (auth != null) {
            mUserId = mFirebaseuser.getUid();
        }*/
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        mFirebaseuser = FirebaseAuth.getInstance().getCurrentUser();
      /*  rll_search_buddy = (RelativeLayout) findViewById(R.id.rll_search_buddy);
        search_buddy = (EditText) findViewById(R.id.ed_search_buddy);*/
        sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
        llv_tracklayout = (LinearLayout) findViewById(R.id.llv_track_layout);
/*
        btnTrack = (Button) findViewById(R.id.btn_track);
*/
        trackMyBuddy = (Button) findViewById(R.id.btn_track_my_buddy);
        // Initialize Database Reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);
        Firebase.setAndroidContext(TrackMyBuddy.this);
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        receiver = new AddBuddyChildReciever();

        //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
     /*   iv_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuOnclickAttach();
            }

        });*/

        iv_arrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();

                    }
                });
        Utilities.strUserName = singleton.getLoginUserDisplayName();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5*60 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(2*60*1000); // 1 second, in milliseconds
       /* if (Utilities.strUserName == null) {
            Toast.makeText(getApplicationContext(), "Something goes wrong,please Login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GoogleMaps.this, MainActivity.class));
        }*/
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        d_time = dateFormat.format(date);
        Log.d("d_time", "" + d_time);
        auth = FirebaseAuth.getInstance();
        mFirebaseuser = auth.getCurrentUser();
        // Initialize Database Reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);
        Firebase.setAndroidContext(getApplicationContext());

        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        if (mFirebaseuser != null) {

            mUserId = mFirebaseuser.getUid();
            Log.d("inside_loginmuserId", mUserId);
        }
        if (session.getLogInUserDisplayName().equalsIgnoreCase("null null")) {

            Toast.makeText(getApplicationContext(), "Something went wrong, Please Login Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(TrackMyBuddy.this, SignInMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            userDisplayName = session.getLogInUserDisplayName();
            Log.d("check_display_name", "" + userDisplayName);
        }
        Log.d("intial_buddies_list", "  " + Utilities.registeredUsersBuddiesList.size());

        Utilities.registeredUsersBuddiesList.clear();
        displayUserBudddies();
        Log.d("tracking_status",""+Singleton.getTrackingStatus());
        if(Singleton.getTrackingStatus())
        {
            llv_tracklayout.setVisibility(View.VISIBLE);
            selectBuddyToTrackRelativeLayout.setVisibility(View.GONE);
            radioStartButton.setChecked(true);
            startTracking();

        }else
        {
            llv_tracklayout.setVisibility(View.GONE);
            selectBuddyToTrackRelativeLayout.setVisibility(View.VISIBLE);
        }
        trackMyBuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected_buddies_count = user_buddies_adapter.selected_contacts.size();
                Log.d("selected_buddiesuuu", "==->" + selected_buddies_count);
                if (selected_buddies_count == 0) {
                    Toast.makeText(getApplicationContext(), "you didnt select your buddy ,please select your buddy to track", Toast.LENGTH_LONG).show();
                } else if (selected_buddies_count == 1) {
                    for (int i = 0; i < selected_buddies_count; i++) {
                        String selected[] = user_buddies_adapter.selected_contacts.get(i).split("-~-");
                        track_user_buddy_name = selected[0];
                        track_user_buddy_key = selected[1];
                        // Contacts users = new Contacts("" + track_user_buddy_name, track_user_buddy_key);

                        Log.d(" Tracked_data", "--->" + track_user_buddy_name + " " + track_user_buddy_key);
                        trakingBuddyTextView.setText("Track" + " " + track_user_buddy_name);
                        llv_tracklayout.setVisibility(View.VISIBLE);
                        selectBuddyToTrackRelativeLayout.setVisibility(View.GONE);

                    }
                } else if (selected_buddies_count > 1) {
                    Toast.makeText(getApplicationContext(), "we can track only one buddy at a time ,please select only one buddy", Toast.LENGTH_LONG).show();

                }
                Log.d("choodu", " " + selected_buddies_count);
            }
        });
        /*btnTrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rll_search_buddy.setVisibility(View.GONE);
                search_emailID = search_buddy.getText().toString().trim().replace('.', ',');
                if (search_emailID != null)
                    singleton.setSelectedUserEmailId(search_emailID);
                IntentFilter filter = new IntentFilter();
                filter.addCategory(Intent.CATEGORY_DEFAULT);
                //registerReceiver(receiver, filter);
                registerBroadcastReceiver();
                // startAlert();
                *//*startService(new Intent(getApplicationContext(), AddBuddyChildReciever.class));
                Calendar cal = Calendar.getInstance();
                Intent intent = new Intent(TrackMyBuddy.this, AddBuddyChildReciever.class);
                PendingIntent pintent = PendingIntent
                        .getService(getApplicationContext(), 0, intent, 0);

                AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                // Start service every hour
                alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                        30*1000, pintent);*//*
            }
        });*/

    }

    RadioButton.OnClickListener myOptionOnClickListener = new RadioButton.OnClickListener() {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (radioStartButton.isChecked()) {
                startTracking();
            } else if (radioStopButton.isChecked()) {
                stopTracking();


            }

        }
    };

    private void stopTracking() {
        //   mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
        unregisterBroadcastReceiver();
        isInTracking=false;
        Singleton.setTrackingStatus(isInTracking);

        gettingBuddiesProgressBar.setVisibility(View.GONE);
    }

    private void startTracking() {
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        //registerReceiver(receiver, filter);
        Utilities.prevValueCounts=0;
        Log.d("prev_vs_value_counts", ""+Utilities.prevValueCounts+"-~-"+value_counts);
        registerBroadcastReceiver();
        isInTracking=true;
        Singleton.setTrackingStatus(isInTracking);
        // mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);
        gettingBuddiesProgressBar.setVisibility(View.VISIBLE);
    }

    private void displayUserBudddies() {
        mDatabaseReference.keepSynced(true);
        mDatabaseReference.child("my_maps_user").child(mUserId).child(session.getLogInUserDisplayName()).child("Buddies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("track_datasnapshot", dataSnapshot.toString());
                Log.d("track_child_count", "   " + dataSnapshot.getChildrenCount());
                all_user_buddies_count = (int) dataSnapshot.getChildrenCount();
                if (all_user_buddies_count == 0) {
                    getYourBuddies.setText("you don't have any buddies ,please add buddies in Buddies Section");
                    progressBar.setVisibility(View.GONE);

                } else

                    //dataSnapshot.getChildrenCount();
                    mDatabaseReference.child("my_maps_user").child(mUserId).child(session.getLogInUserDisplayName()).child("Buddies").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            Log.d("track_child_dat", dataSnapshot.toString());
                            getUserBuddies(dataSnapshot, s);
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {

                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mDatabaseReference.keepSynced(true);


    }

    private void getUserBuddies(DataSnapshot dataSnapshot, String s) {
        progressBar.setVisibility(View.GONE);
        getYourBuddies.setVisibility(View.GONE);
        Log.d("inside_track_get_budd", " " + dataSnapshot.toString());
        Log.d("TrackBUDDY_shot", "" + dataSnapshot.getKey());
        Log.d("track_buddy_svalue", "" + dataSnapshot.getValue());
        retreive_user_buddy_count++;

        // loginDataBaseAdapter.insertRegisteredUsersEntry();
        Log.d("total_buddies", "" + all_user_buddies_count);
        Log.d("added_buddies", "" + retreive_user_buddy_count);
        try {
    /*        String users_details = "" + dataSnapshot.getValue();
            String[] allUsersData = users_details.split("#");
            String username = dataSnapshot.getKey();
            String username_key = allUsersData[0];
            String user_email = allUsersData[1];
            String user_phone_num = allUsersData[2];*/
            String buddy_name = dataSnapshot.getKey();
            String buudy_key = dataSnapshot.getValue().toString();
            Buddies users = new Buddies(buddy_name, buudy_key);
            Utilities.registeredUsersBuddiesList.add(users);
            utilities.registeredSampleUsersBuddiesList.add(buddy_name + "-~-" + buudy_key);
            user_buddies_adapter = new UserBuddiesAdapter(getApplicationContext(), Utilities.registeredUsersBuddiesList, utilities.registeredSampleUsersBuddiesList);
            listview_buddies.setAdapter(user_buddies_adapter);

            Log.d("geting_budddies_data", "" + buddy_name + "--~--" + buudy_key + Utilities.registeredUsersBuddiesList.size());


            // if (total_buddies == added_buddies) {
            Log.d("inside", "" + retreive_user_buddy_count);
            Log.d("size", "" + Utilities.registeredUsersBuddiesList.size());
        } catch (Exception e) {
            utilities.showAlert(e.getMessage());
        }


        //track_user.user_buddies.add(dataSnapshot.getKey() + "#" + dataSnapshot.getValue().toString());
/*
        if (all_user_buddies_count == retreive_user_buddy_count) {
            String Buddy_username = dataSnapshot.getKey();
            String buddy_key = (String) dataSnapshot.getValue();
            track_user = new MyMapsUser(Buddy_username, buddy_key);
            track_user.user_buddies.add(track_user);
            user_buddies_adapter = new ContactsListAdapter(getApplicationContext(), Utilities.registeredUsersArrayList);
            listview_buddies.setAdapter(user_buddies_adapter);
        }*/
    }

    public void getAllDateDetails(final String budd_name) {
        mFirebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        get_all_details_count++;
        Log.d("getAllDateDetails_count", "" + get_all_details_count);
        // mFirebaseuser = auth.getCurrentUser();
        // auth.getCurrentUser();
        // Initialize Database Reference

        //this mRootRef contains the root of the json tree
        if (mFirebaseuser != null) {

            mUserId = mFirebaseuser.getUid();
            Log.d("mapdetails_loginmuserId", mUserId);
        }
        Log.d("be_in_right_way", budd_name);

        Firebase.setAndroidContext(TrackMyBuddy.this);
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/");

        //Log.d("getDate", "" + db);
        if (mUserId != null) {
            mRootRef = FirebaseDatabase.getInstance().getReference();
            userDisplayName = session.getLogInUserDisplayName();
            Log.d("getting_username", "" + userDisplayName);
           /* googleMapsRef = mRootRef.child("my_maps_user").child("emailToUid").child(budd_name);


            Log.d("googleMapsRef", "" + googleMapsRef);


            googleMapsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("hellooo(tel)_in_track", dataSnapshot.toString());
                    value_count = (int) dataSnapshot.getChildrenCount();
                    Log.d("value_count", "" + value_count);
                    //search_buddy_uid = "" + dataSnapshot.getValue();
                    Log.d("selected_emailId", dataSnapshot.getKey());
                    Log.d("selected_userId", "" + dataSnapshot.getValue());
                    String getUserId = "" + dataSnapshot.getValue();
                    String selected_user_id[] = getUserId.split("#");
                    searchBuddyUserId = selected_user_id[0];
                    searchBuddyEmailId = selected_user_id[1];
                    Log.d("getting_details", searchBuddyUserId + " " + searchBuddyEmailId);
                    //  if (value_count != 0) {*/
            singleton.setAllDateValues("" + value_count);
            loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.deleteAllLocationsEntry();
            loginDataBaseAdapter.close();

            RequestTrackData(new getSelectedDateListener() {
                @Override
                public void getSelectedDateResult(Cursor client_details) {
                    gettingBuddiesProgressBar.setVisibility(View.GONE);

                    int after_client_count = client_details.getCount();
                    Log.d("after_client_countuuu", "" + after_client_count);
                    Log.d("selected_date", "" + selected_date);
                    if (client_details.getCount() != 0) {
                        Log.d("c", "" + client_details.getCount());
                        int count = client_details.getCount();
                        if (count != 0) {
                            client_details.moveToFirst();

                            for (int i = 0; i < count; i++) {
                                do {
                                    Log.e("Client", String.valueOf(client_details.getColumnIndex("UserName")));
                                    if (client_details.getColumnIndex("UserName") != -1) {
                                        userName = client_details.getString(client_details.getColumnIndex("UserName"));
                                        Log.e("Mapdetails_UserName", "" + userName);
                                        lat = client_details.getDouble(client_details.getColumnIndex("lat"));
                                        Log.e("Mapdetails_latitude", "" + lat);
                                        lon = client_details.getDouble(client_details.getColumnIndex("lon"));
                                        Log.e("Mapdetails_longitude", "" + lon);
                                        date = client_details.getString(client_details.getColumnIndex("RECORDED_DATE"));
                                        time = client_details.getString(client_details.getColumnIndex("time"));
                                        address = client_details.getString(client_details.getColumnIndex("address"));
                                        Log.e("Mapdetails_address", "" + address);
                                        state = client_details.getString(client_details.getColumnIndex("state"));
                                        Log.e("Mapdetails_time", "" + time);

                                        country = client_details.getString(client_details.getColumnIndex("country"));
                                        city = client_details.getString(client_details.getColumnIndex("City"));
                                        if (i == 0) {
                                            sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("count_name", "red");
                                            editor.commit();
                                        } else if (i == (count - 1)) {
                                            sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putString("count_name", "magenta");
                                            editor.commit();
                                        }
                                        boolean status = checkInternetConnection();
                                        if (status) {
                                            if ((address == null) && (state == null) && (city == null) && (country == null)) {
                                                //getAddress(lat, lon);
                                            }
                                        }
                                    }
                                    addedMarker();

                                    sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.remove("count_name");
                                    editor.apply();
                                    editor.commit();
                                    Log.e("Mapdetails_time", "" + time);
                                    Log.e("Mapdetails_time", "" + date);
                                    Log.e("Mapdetails_city", "" + city);
                                    Log.e("Mapdetails_state", "" + state);
                                    Log.e("Mapdetails_country", "" + country);

                                    i++;
                                } while (client_details.moveToNext());
                            }
                        } else {
                            Toast.makeText(TrackMyBuddy.this, track_user_buddy_name + " didnt use google maps on that date " + d_time, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(TrackMyBuddy.this, track_user_buddy_name + " didnt use google maps on that date " + d_time, Toast.LENGTH_LONG).show();
                    }
                    loginDataBaseAdapter.close();
                }

                @Override
                public void getSelDateErrorResult(Cursor String) {

                }

                @Override
                public void getRecentAddedLocation() {

                           /* rll_search_buddy.setVisibility(View.GONE);
                            search_emailID = search_buddy.getText().toString().trim().replace('.', ',');*/
                    Log.d("entered_emailId", "" + search_emailID);
                    //getAllDateDetails(search_emailID);
                }


            });


        }


        /*        @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());


                }*/
        //});
        // }
    }

    private void addedMarker() {
        int counts = 0;
        c = 0;
        sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
        String count_name = sharedPreferences.getString("count_name", "");
        if (count_name.equals("red")) {
            counts = 1;
        } else if (count_name.equals("magenta")) {
            counts = (client_details.getCount() - 1);
        }
        Log.d("added_marker_count", " " + counts);
        Log.d("usernamu", "" + time);

        latLng = new LatLng(lat, lon);
        if (counts == 1) {
            googleMap.clear();
            arrayPoints.clear();
            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(userName + "\n" + time + "\n" + d_time)
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            c++;

        } else if (counts == (client_details.getCount() - 1)) {
            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(userName + "\n" + time + "\n" + d_time)
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            c++;
            Log.d("my_pink_color", "" + time + "--->" + d_time);

        } else {
            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(userName + "\n" + time + "\n" + d_time)
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
            c++;

        }
        arrayPoints.add(latLng);
        polylineOptions.addAll(arrayPoints);
        polylineOptions.width(5);
        polylineOptions.color(Color.BLUE);
        googleMap.addPolyline(polylineOptions);
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(TrackMyBuddy.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(TrackMyBuddy.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(TrackMyBuddy.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        /*    move to current location*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // Zoom in, animating the camera.
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.

        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));


       /* myMarker = googleMap.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lon))
                .title(time)
                .snippet("My First App")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
*/

    }


    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(rll_home, R.string.alert_allow_contacts, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("trackmybuddy", "onresume");
        setUpMapIfNeeded();
        mGoogleApiClient.connect();

        IntentFilter filter = new IntentFilter("android.intent.action.TIME_TICK");
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // unregisterReceiver(receiver);

       /* if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationRequest, pendingintent);
            mGoogleApiClient.disconnect();
        }*/
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            fm = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

            fm.getMapAsync(this);

            if (googleMap != null) {
                setUpMap();

            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            handleNewLocation(location);
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                handleNewLocation(location);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        googleMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //googleMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        googleMap.addMarker(options);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);
        polyLineOptions = new PolylineOptions();

        polyLineOptions.add(latLng);
        polyLineOptions.width(2);
        polyLineOptions.color(Color.BLUE);


        googleMap.addPolyline(polyLineOptions);
        //float zoomLevel = 16.0;//This goes up to 21
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        Utilities.prevlatitude = location.getLatitude();
        Utilities.prevlongitude = location.getLongitude();
        Log.e("prevlat", "" + Utilities.prevlatitude);
        Log.e("prevlon", "" + Utilities.prevlongitude);
        //startAlert();


    }

    private String getLocationAddress(List<Address> addresses) {
        if (addresses != null && addresses.size() > 0) {
            Address address = addresses.get(0);


            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                Log.d("location", "--->" + address.getMaxAddressLineIndex() + "..." + address.getAddressLine(i));
                sb.append(address.getAddressLine(i)).append("\n");
            }

            return sb.toString();
        } else {
            return null;
        }
    }


    public void RequestTrackData(final getSelectedDateListener listener) {
        this.listeners = listener;
        //publishProgress("Sleeping..."); // Calls onProgressUpdate()
        Log.d("calling Request", "requestTrackData");

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);
        Log.d("selec_daes", "" + selected_dates);
        Log.d("search_buddy_id", "" + track_user_buddy_key + "  undu  " + track_user_buddy_name);
        googleMapsRefs = mRootRef.child("my_maps_user").child(track_user_buddy_key).child(track_user_buddy_name).child("maps");
        googleMapsRefs.keepSynced(true);
        Log.d("inside_click", "" + session.getLogInUserDisplayName());
        Log.d("inside_maps", "" + googleMapsRefs);
        //  String email = escapeEmailAddress("theja@gmail.com");
        new ExtractMapDetails().execute();

    }
    protected class ExtractMapDetails extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {



                googleMapsRefs.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        value_counts = (int) dataSnapshot.getChildrenCount();
                        all_date_child_count = 0;

                        Log.d("datasnapuuu", "" + dataSnapshot.getChildrenCount());
                        Utilities.prevValueCounts = Utilities.postValueCounts;
                        Log.d("prevValueCounts(b)", "" + Utilities.prevValueCounts + "!@#$$" + value_counts);
                        if (value_counts == 0) {
                            gettingBuddiesProgressBar.setVisibility(View.GONE);
                            Toast.makeText(TrackMyBuddy.this, track_user_buddy_name + " didnt use google maps on this date " + d_time, Toast.LENGTH_SHORT).show();

                        }
                        Utilities.postValueCounts = value_counts;
                        Log.d("prevValueCounts(a)", "" + Utilities.postValueCounts + "!@#$$" + value_counts);

                        Log.d("value_counts", "" + value_counts);
                        if (value_counts != 0)
                            googleMapsRefs.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                                @Override
                                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                                    Log.d(TAG, "onChildAdded_track:" + dataSnapshot.getValue());
                                    getSearchUserDetails(dataSnapshot, listeners);
                                }

                                @Override
                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                    Log.d(TAG, "onChildChanged(t):" + dataSnapshot.getKey());
                                    getSearchUserDetails(dataSnapshot, listeners);

                                    // A comment has changed, use the key to determine if we are displaying this
                                    // comment and if so displayed the changed comment.
                                    MyMapsUser newComment = dataSnapshot.getValue(MyMapsUser.class);
                                    String commentKey = dataSnapshot.getKey();

                                    // ...
                                }

                                @Override
                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                    // adapter.remove((String) dataSnapshot.child("title").getValue());
                                    Log.d(TAG, "onChildRemoved(t):" + dataSnapshot.getKey());
                                    getSearchUserDetails(dataSnapshot, listeners);

                                    // A comment has changed, use the key to determine if we are displaying this
                                    // comment and if so remove it.
                                    String commentKey = dataSnapshot.getKey();

                                    // ...
                                }

                                @Override
                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                    Log.d(TAG, "onChildMoved(t):" + dataSnapshot.getKey());
                                    getSearchUserDetails(dataSnapshot, listeners);

                                    // A comment has changed position, use the key to determine if we are
                                    // displaying this comment and if so move it.
                                    MyMapsUser movedComment = dataSnapshot.getValue(MyMapsUser.class);
                                    String commentKey = dataSnapshot.getKey();

                                    // ...
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.w(TAG, "postComments:onCancelled(t)", databaseError.toException());
                                    Toast.makeText(getApplicationContext(), "Failed to load comments.",
                                            Toast.LENGTH_SHORT).show();
                                }
                            });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    private String escapeEmailAddress(String email) {


        // Replace '.' (not allowed in a Firebase key) with ',' (not allowed in an email address)
        email = email.toLowerCase();
        email = email.replace(".", ",");
        return email;
    }

    private void getSearchUserDetails(DataSnapshot dataSnapshot, final getSelectedDateListener listener) {
        this.listeners = listener;
        selected_dates = d_time.replace('/', '-');
        long retrieve_count = dataSnapshot.getChildrenCount();
        Log.d("retrieve_count_track", "" + retrieve_count);
        Log.d("before_all_date_child", "" + all_date_child_count + "~!~" + value_counts);

        all_date_child_count++;
        Log.d("all_date_child_coutrack", "" + all_date_child_count + "~!~" + value_counts);
        String all_date_values = singleton.getAllDateValues();
        Log.d("singleton_alldate_track", "" + all_date_values + "!~!" + all_date_child_count);
        // A new comment has been added, add it to the displayed list
        MyMapsUser comment = dataSnapshot.getValue(MyMapsUser.class);
        String retrieve_date = comment.getDate();
        Log.d("retrieve_date", "" + retrieve_date);
        Log.d("comment", "" + comment);
        Log.d("datasnapshot", "" + dataSnapshot.toString());
        Log.d("selected_datesss", "" + selected_dates);
        Log.d("selected_date(md)", "" + selected_date);
        boolean isSelected = false;
        Log.d("testdate_track", "" + d_time);

        Log.d("RETRIEVE_datasna_track", dataSnapshot.getKey() + "--~!!!--" + selected_dates);
        String[] compareKeysWithDate = dataSnapshot.getKey().split("@");
        if (compareKeysWithDate[0].equals(selected_dates)) {
            Log.d("comparekeyswithdate", "" + compareKeysWithDate[0] + "!@#$%^&*" + selected_dates);
            loginDataBaseAdapter = loginDataBaseAdapter.open();
            // loginDataBaseAdapter.deleteAllLocationsEntry();
            // for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            selected_date_child_count++;
            Log.d("postsnapshotcount_track", "" + selected_date_child_count);
            Log.d("selected_date_res_track", "" + dataSnapshot);
            Log.d("know_childrens_track", "" + dataSnapshot.getChildren());
            String key = dataSnapshot.getKey();
            // Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();
            MyMapsUser selectedDateUserDetails = dataSnapshot.getValue(MyMapsUser.class);
            String UserName = dataSnapshot.child("UserName").getValue(String.class);
            String dates = dataSnapshot.child("Date").getValue(String.class);
            Log.d("UserNameAndDate", "" + UserName + " " + dates);
            String date = selectedDateUserDetails.getDate();
            String userName = selectedDateUserDetails.getUserName();
            Double latitude = Double.parseDouble(selectedDateUserDetails.getLatitude());
            Double longitude = Double.parseDouble(selectedDateUserDetails.getLongitude());
            String time = selectedDateUserDetails.getTime();
            String address = selectedDateUserDetails.getAddress();
            String city = selectedDateUserDetails.getCity();
            String state = selectedDateUserDetails.getState();
            String country = selectedDateUserDetails.getCountry();
            Log.d("secret_username", "" + userName);
            Log.d("secret_time", "" + time);

            // if((UserName!=null)&&(latitude!=null)&&(longitude!=null))
            loginDataBaseAdapter.insertMapsEntry(UserName, latitude, longitude, dates, time, address, city, state, country);
            loginDataBaseAdapter.close();
            Log.d("date+db(track)", "" + date + "~~!!~~" + d_time + "~~!!~~" + selected_date);
            if (selected_date_child_count == 1)
                Log.d("selectedDateUsertrack", "" + key + "~" + dates + "~" + UserName + "~" + latitude + "~" + longitude + "~" + time + "~" + address + "~" + city + "~" + state + "~" + country);
            Log.d("selected_datasnaptrack", key + "--->" + selected_date);
            Log.d("userDetails_track", "" + selectedDateUserDetails);
            Log.d("-------------->", "finished" + "---------------->");
            Log.d("EQUAL_DATES", "" + all_date_child_count + " " + value_counts);
            Log.d("chealldate_sel_date(t)", "" + all_date_child_count + " " + value_counts);

            if (all_date_child_count == value_counts) {
                Log.d("printing_counts", "" + all_date_child_count + " " + value_counts);

                all_date_child_count = 0;

                loginDataBaseAdapter = loginDataBaseAdapter.open();
                client_details = loginDataBaseAdapter.getMapsClientDetails(d_time);
                loginDataBaseAdapter.close();

                Log.d("acidity_chal(t)....", "" + client_details.getCount());
                //}
                listeners.getSelectedDateResult(client_details);
                loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.deleteAllLocationsEntry();
                loginDataBaseAdapter.close();
                Log.d("before(t)", "" + client_details);
            }
            Log.d("got_details_track", "" + gotDetails);
        }

//if((all_date_child_count!=0)&& (value_count!=null)) {
          /*  Log.d("EQUAL_DATES", "" + all_date_child_count + " " + value_counts);

            all_date_child_count = 0;
            loginDataBaseAdapter = loginDataBaseAdapter.open();
            client_details = loginDataBaseAdapter.getMapsClientDetails(d_time);

            Log.d("acidity_chal(t)....", "" + client_details.getCount());
            //}
            listeners.getSelectedDateResult(client_details);

            Log.d("before(t)", "" + client_details);*/


    }


    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    public void onClick(View v) {
       /* switch (v.getId()) {

            case R.id.ll_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.ll_contacts:
                acessContacts();

*//*
              displayContacts();
*//*

                *//*AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMaps.this);
                builder1.setMessage(getResources().getString(R.string.alert_allow_contacts));
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                startActivity(new Intent(getApplicationContext(), ContactRegistrationActivity.class));

                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();*//*

                break;
            case R.id.ll_location_history:

                startActivity(new Intent(getApplicationContext(), MapDetails.class));
                break;
            case R.id.ll_messages:

                startActivity(new Intent(TrackMyBuddy.this, TrackMyBuddy.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                // sendSMSMessage();

                break;
            case R.id.ll_logout:
                onPressed();

               *//* if (AccessToken.getCurrentAccessToken() != null) {

                    LoginManager.getInstance().logOut();
                }
                else
                {

                    if (Utilities.mGoogleApiClient.isConnected())
                    {
                        Plus.AccountApi.clearDefaultAccount(Utilities.mGoogleApiClient);
                        Utilities.mGoogleApiClient.disconnect();
                        Utilities.mGoogleApiClient.connect();
                        ProfileActivity.tv_fname.setText("");
                        ProfileActivity.tv_lname.setText("");
                        ProfileActivity.tv_femail.setText("");
                    }
                }
                Toast.makeText(getApplicationContext(), "you logged out successfully", Toast.LENGTH_SHORT).show();


                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();*//*
                break;
        }*/
    }

    private void acessContacts() {
        boolean allow_contacts = mayRequestContacts();
        if (allow_contacts) {
            startActivity(new Intent(getApplicationContext(), ContactRegistrationActivity.class));

        } else
            dialog.cancel();
        // This Build is < 6 , you can Access contacts here.
    }


    private void displayContacts() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        handleNewLocation(location);
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            handleNewLocation(location);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location == null) {
            sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("address", "pendingintent");
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingintent);
                    pendingintent = PendingIntent.getActivity(this, 0, new Intent(this, MyBroadcastReciever.class), PendingIntent.FLAG_ONE_SHOT);

                }
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingintent);
                pendingintent = PendingIntent.getActivity(this, 0, new Intent(this, MyBroadcastReciever.class), PendingIntent.FLAG_ONE_SHOT);
            }
           /* buildGoogleApiClient();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingintent);
            pendingintent = PendingIntent.getActivity(this, 0,new Intent(this,MyBroadcastReciever.class),PendingIntent.FLAG_ONE_SHOT);//FLAG_ONE_SHOT - only once pIntent can use the myIntent*/

        } else {
            handleNewLocation(location);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            /*
             * Thrown if Google Play services canceled the original
             * PendingIntent
             */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
        /*
         * If no resolution is available, display a dialog to the
         * user with the error.
         */
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);

        switch (reqCode) {
            case (PICK_CONTACT):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        // TODO Fetch other Contact details as you want to use
                        Log.d("contact_name", name);

                    }
                }
                break;
        }
    }

    public void startAlert() {
        Intent i = new Intent(this, AddBuddyChildReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, i, 0);

        Log.d("calling_reciever_serv", "starting....");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 30 * 60 * 1000, pendingIntent);
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    public void signOut() {
        auth.signOut();
        session.logoutUser();
        singleton.setLoginUserDisplayName(null);
        singleton.setLoginUserEmailId(null);
        singleton.setLoginActiveStatus(null);
        Toast.makeText(getApplicationContext(), "you logged out successfully", Toast.LENGTH_SHORT).show();

    }


    public static JSONObject getLocationInfo(double lat, double lng) {

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();

            StrictMode.setThreadPolicy(policy);

            HttpGet httpGet = new HttpGet(
                    "http://maps.googleapis.com/maps/api/geocode/json?latlng="
                            + lat + "," + lng + "&sensor=true");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }

            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("response", "" + jsonObject);
            return jsonObject;
        }
        return null;
    }

    public static String getCurrentLocationViaJSON(double lat, double lng) {

        JSONObject jsonObj = getLocationInfo(lat, lng);
        Log.i("JSON string =>", jsonObj.toString());

        String Address1 = "";
        String Address2 = "";
        String City = "";
        String State = "";
        String Country = "";
        String County = "";
        String PIN = "";

        String currentLocation = "";

        try {
            String status = jsonObj.getString("status").toString();
            Log.i("status", status);

            if (status.equalsIgnoreCase("OK")) {
                JSONArray Results = jsonObj.getJSONArray("results");
                JSONObject zero = Results.getJSONObject(0);
                JSONArray address_components = zero
                        .getJSONArray("address_components");

                for (int i = 0; i < address_components.length(); i++) {
                    JSONObject zero2 = address_components.getJSONObject(i);
                    String long_name = zero2.getString("long_name");
                    JSONArray mtypes = zero2.getJSONArray("types");
                    String Type = mtypes.getString(0);

                    if (Type.equalsIgnoreCase("street_number")) {
                        Address1 = long_name + " ";
                    } else if (Type.equalsIgnoreCase("route")) {
                        Address1 = Address1 + long_name;
                    } else if (Type.equalsIgnoreCase("sublocality")) {
                        Address2 = long_name;
                    } else if (Type.equalsIgnoreCase("locality")) {
                        // Address2 = Address2 + long_name + ", ";
                        City = long_name;
                    } else if (Type
                            .equalsIgnoreCase("administrative_area_level_2")) {
                        County = long_name;
                    } else if (Type
                            .equalsIgnoreCase("administrative_area_level_1")) {
                        State = long_name;
                    } else if (Type.equalsIgnoreCase("country")) {
                        Country = long_name;
                    } else if (Type.equalsIgnoreCase("postal_code")) {
                        PIN = long_name;
                    }

                }

                currentLocation = Address1 + "," + Address2 + "," + City + ","
                        + State + "," + Country + "," + PIN;
                j_address1 = Address1;
                j_address2 = Address2;
                j_city = City;
                j_state = State;
                j_country = Country;
                j_pin = PIN;
            }
        } catch (Exception e) {

        }
        return currentLocation;

    }

    private boolean checkInternetConnection() {

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, "wifi_track Connected ", Toast.LENGTH_LONG).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, "mobile_track Connected ", Toast.LENGTH_LONG).show();
                return true;
            } else {

                Toast.makeText(this, " Not Connected,Internet connection required ", Toast.LENGTH_LONG).show();

            }

        }
        // get Connectivity Manager object to check connection
      /*  ConnectivityManager connec =(ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
            return true;
        }else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(this, " Not Connected,Internet connection required ", Toast.LENGTH_LONG).show();
            return false;
        }*/
        return false;

    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(rll_home, R.string.alert_allow_location, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {

                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(TrackMyBuddy.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        });
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledAlertToUser();
            }

            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                            showGPSDisabledAlertToUser();
                        }

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        googleMap.getUiSettings().setZoomControlsEnabled(true);
                        googleMap.setMyLocationEnabled(true);
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case REQUEST_READ_CONTACTS:
                if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted , Access contacts here or do whatever you need.
                    startActivity(new Intent(getApplicationContext(), ContactRegistrationActivity.class));

                }

                // other 'case' lines to check for other
                // permissions this app might request
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);

                        fm.getMapAsync(TrackMyBuddy.this);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMaps) {
        googleMap = googleMaps;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

            }
        } else {
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

        }

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        track_Name = (TextView) view.findViewById(R.id.tv_name);
        track_user_key = (TextView) view.findViewById(R.id.tv_number);
        // PhoneNumber = (TextView) view.findViewById(R.id.tv_number);
        ckbox = (CheckBox) view.findViewById(R.id.cb_select);
        ckbox.performClick();
        if (ckbox.isChecked()) {
            //  Toast.makeText(view.getContext(), "checked", Toast.LENGTH_LONG).show();

            selectedBuddyName = track_Name.getText().toString().trim();
            selecteUserkey = track_user_key.getText().toString().trim();
            track_user.selected_buddies.clear();
            track_user.selected_buddies.add(searchBuddyEmailId + "~" + selecteUserkey);

           /* loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.insertContactsEntry(Name.getText().toString(), PhoneNumber.getText().toString());
            loginDataBaseAdapter.close();*/
            //  Toast.makeText(view.getContext(), track_Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + track_Name, Toast.LENGTH_LONG).show();


        } else if (!ckbox.isChecked()) {
            //user.selected_buddies.remove(Name.getText().toString()+ "~" +user_key);

            // Toast.makeText(view.getContext(), "unchecked", Toast.LENGTH_LONG).show();
        }
        //Toast.makeText(view.getContext(), track_Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + track_Name, Toast.LENGTH_LONG).show();
    }

    private String isCheckedOrNot(CheckBox checkbox) {
        if (checkbox.isChecked())

            return "is checked";

        else
            return "is not checked";
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        try {


            user_buddies_adapter.getFilter().filter(s);
        } catch (Exception e) {

        }
        return false;
    }

    public class AddBuddyChildService extends Service {

        public AddBuddyChildService() {
            super();
        }

        public AddBuddyChildService(String name) {
            super();
        }

        @Override
        public IBinder onBind(Intent arg0) {
            // TODO Auto-generated method stub
            Toast.makeText(getApplicationContext(), " MyService Created ", Toast.LENGTH_LONG).show();

            return null;

        }

        @Override
        public void onStart(Intent intent, int startId) {
            // TODO Auto-generated method stub
            super.onStart(intent, startId);

            Log.d("entered_emailId", "" + search_emailID);

            if ((Utilities.prevValueCounts != 0) && (value_counts > Utilities.prevValueCounts)) ;

            // getAllDateDetails(search_emailID);
            Log.d("TrackMyBuddy", "FirstService started");
            Toast.makeText(this, " MyService Started ", Toast.LENGTH_LONG).show();

        }

        @Override
        public void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();
            Log.d("Trackmybuddy", "FirstService destroyed");
            Toast.makeText(getApplicationContext(), " MyService Stopped ", Toast.LENGTH_LONG).show();

        }

    }

    public class AddBuddyChildReciever extends BroadcastReceiver {
        Context context;

        @Override
        public void onReceive(Context context, Intent intent) {
            this.context = context;
            TrackMyBuddy track = new TrackMyBuddy();

            TrackMyBuddy.AddBuddyChildReciever innerObject = track.new AddBuddyChildReciever();
            Log.d("inside_reciever", track_user_buddy_name + "--->" + track_user_buddy_key);

            if (track_user_buddy_name != null) {
                Log.d("prev_value_counts", "" + Utilities.prevValueCounts + "!@#$$" + value_counts);
                  if ((Utilities.prevValueCounts == 0)) {
                Log.d("inside_reciever", track_user_buddy_name + "--->" + track_user_buddy_key);
                TrackMyBuddy.this.getAllDateDetails(track_user_buddy_name);
                 }else
                if ((Utilities.prevValueCounts != 0) && (value_counts > Utilities.prevValueCounts)) {
                    TrackMyBuddy.this.getAllDateDetails(track_user_buddy_name);
                }
            } else {
                Toast.makeText(getApplicationContext(), "you didnt  select your buddy", Toast.LENGTH_LONG).show();
            }
        }


    }

    public void registerBroadcastReceiver() {

        this.registerReceiver(receiver, new IntentFilter(
                "android.intent.action.TIME_TICK"));
        Intent i = new Intent(this, AddBuddyChildReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, i, 0);

        Log.d("calling_reciever_serv", "starting....");
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 30 * 60 * 1000, pendingIntent);
        Toast.makeText(this, "Registered broadcast receiver", Toast.LENGTH_SHORT)
                .show();
    }

    /**
     * This method disables the Broadcast receiver
     *
     * @param //view
     */
    public void unregisterBroadcastReceiver() {

        this.unregisterReceiver(receiver);
        googleMap.clear();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
        Toast.makeText(this, "unregistered broadcst receiver", Toast.LENGTH_SHORT)
                .show();
    }
}

