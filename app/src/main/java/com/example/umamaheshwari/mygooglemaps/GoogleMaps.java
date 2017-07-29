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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
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
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umamaheshwari.mygooglemaps.Interfaces.getSelectedDateListener;
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
import com.google.android.gms.maps.model.LatLng;
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
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import static android.Manifest.permission.READ_CONTACTS;
import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;

public class GoogleMaps extends FragmentActivity implements OnMapReadyCallback, LocationListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG = GoogleMaps.class.getSimpleName();
    ImageView navigationIconImageView, iv_arrow;
    // Google Map
    List<Address> addresses;
    GoogleMap googleMap;
    Geocoder geocoder;
    Cursor client_details;
    double currentLatitude;
    Context mContext;
    double currentLongitude;
    DrawerLayout drawer;
    NavigationView navigationView;
    private View navHeader;
    View BottomView;
    LinearLayout bottomMenuLinearLayout, llv_buddies;
    private BottomNavigationItemsAdapter bottomNavigationitemsAdapter;
    LatLng latLng;
    Location mCurrentLocation;
    static Location location;
    String resp;
    LoginDataBaseAdapter loginDataBaseAdapter;
    Dialog dialog;
    Singleton singleton;
    String firstName, lastName;
    protected LocationManager locationManager;
    LinearLayout ll_profile, ll_contacts, ll_location_history, ll_messages, ll_logout;
    static String d_time, time, UserName, address, city, state, country, mydate;
    String mLastUpdateTime;
    String l_address, l_city, l_state, l_country, contact_phonenumber;
    static double lat, lon;
    PolylineOptions polyLineOptions;
    SharedPreferences sharedPreferences;
    SupportMapFragment fm;
    boolean isGPSEnabled, isNetworkEnabled = false;
    String userDisplayName;
    getSelectedDateListener listener;
    static String j_address1, j_address2, j_city, j_state, j_country, j_pin;
    SessionManager session;
    // firebase auth
    private FirebaseAuth auth;
    private FirebaseUser mFirebaseuser;
    public DatabaseReference mDatabaseReference;
    private String mUserId;
    private String complete_address_json;

    Firebase ref;
    RecyclerView menu_items;
    LinearLayoutManager menuItemsLinearLayoutMnanager;
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

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    PendingIntent pendingintent;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 10; // 1 minute
    private static final int PICK_CONTACT = 0;
    private static final int REQUEST_READ_CONTACTS = 0;
    RelativeLayout rll_home;
    RadioButton radioNormal, radioHybrid, radioSatellite, radioTerrian;
    int[] bottomNavigationMenuIcons = {R.drawable.profile, R.drawable.messages, R.drawable.contacts};
    String[] bottomNavigationMenuItems = {"Home", "TrackMyBuddy", "Share"};
    Utilities utilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                checkLocationPermission();

            }catch (Exception e)
            {
                Log.d("exception_is",""+e.getMessage());
            }
           /* mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.getUiSettings().setZoomControlsEnabled(true);*/
        }

        FacebookSdk.sdkInitialize(getApplicationContext());

        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        navigationIconImageView = (ImageView) findViewById(R.id.iv_toggle);

        // iv_arrow = (ImageView) findViewById(R.id.iv_toggle);
        rll_home = (RelativeLayout) findViewById(R.id.rll_home);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
        singleton = Singleton.getInstance();
        session = new SessionManager(getApplicationContext());
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        mFirebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize Database Reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);
        BottomView = (View) findViewById(R.id.view_bottom);
        bottomMenuLinearLayout = (LinearLayout) findViewById(R.id.bottom_menu);
        Firebase.setAndroidContext(GoogleMaps.this);
        //  listener=(getSelectedDateListener)GoogleMaps.this;
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        llv_buddies = (LinearLayout) findViewById(R.id.llv_buddies);
        llv_buddies.setOnClickListener(this);
        radioNormal = (RadioButton) findViewById(R.id.rdoNormal);
        radioHybrid = (RadioButton) findViewById(R.id.rdoHybrid);
        radioSatellite = (RadioButton) findViewById(R.id.rdoSatellite);
        radioTerrian = (RadioButton) findViewById(R.id.rdoTerrain);
        radioNormal.setOnClickListener(myOptionOnClickListener);
        radioHybrid.setOnClickListener(myOptionOnClickListener);
        radioSatellite.setOnClickListener(myOptionOnClickListener);
        radioTerrian.setOnClickListener(myOptionOnClickListener);
        utilities = new Utilities(this);
        dialogInitilization();
        //setUpMapIfNeeded();
       /* menu_items = (RecyclerView) findViewById(R.id.menu_items);
        menu_items.setHasFixedSize(true);
        menuItemsLinearLayoutMnanager = new LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
        );
        bottomNavigationitemsAdapter = new BottomNavigationItemsAdapter(getApplicationContext(), bottomNavigationMenuIcons, bottomNavigationMenuItems);
        //  FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        menu_items.setLayoutManager(menuItemsLinearLayoutMnanager);
        menu_items.setAdapter(bottomNavigationitemsAdapter);*/

        navigationIconImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(Gravity.LEFT);

                //menuOnclickAttach();
            }

        });
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
    /*    iv_arrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });*/
        Utilities.strUserName = singleton.getLoginUserDisplayName();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5 * 60 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(2 * 60 * 1000); // 1 second, in milliseconds
       /* if (Utilities.strUserName == null) {
            Toast.makeText(getApplicationContext(), "Something goes wrong,please Login again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GoogleMaps.this, MainActivity.class));
        }*/
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
            Log.d("check_display_name", "" + auth.getCurrentUser().getDisplayName() + "---->" + userDisplayName);

        }
        if (session.getLogInUserDisplayName().equalsIgnoreCase("null")) {

            Toast.makeText(getApplicationContext(), "Something went wrong, Please Login Again", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(GoogleMaps.this, SignInMainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        } else {
            userDisplayName = session.getLogInUserDisplayName();
            Log.d("check_display_name", "" + userDisplayName);
        }

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

    RadioButton.OnClickListener myOptionOnClickListener = new RadioButton.OnClickListener() {

        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (radioNormal.isChecked()) {
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL);
            } else if (radioHybrid.isChecked()) {
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID);
            } else if (radioSatellite.isChecked()) {
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE);
            } else if (radioTerrian.isChecked()) {
                mMap.setMapType(com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN);
            }

        }
    };

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
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();

       /* if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mLocationRequest, pendingintent);
            mGoogleApiClient.disconnect();
        }*/
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            fm = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));

            fm.getMapAsync(this);

            //  mMap = fm.getMap();


            //  mMap.getUiSettings().setZoomControlsEnabled(true);
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();

            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
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
               else if (isGPSEnabled) {
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
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

        latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            mMap.setMyLocationEnabled(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            mMap.getUiSettings().setZoomControlsEnabled(true);

            return;
        }
        mMap.setMyLocationEnabled(true);
        polyLineOptions = new PolylineOptions();

        polyLineOptions.add(latLng);
        polyLineOptions.width(2);
        polyLineOptions.color(Color.BLUE);


        mMap.addPolyline(polyLineOptions);
        //float zoomLevel = 16.0;//This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        Utilities.prevlatitude = location.getLatitude();
        Utilities.prevlongitude = location.getLongitude();
        Log.e("prevlat", "" + Utilities.prevlatitude);
        Log.e("prevlon", "" + Utilities.prevlongitude);
        //startAlert();

        try {
            geocoder = new Geocoder(GoogleMaps.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1);
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            Log.d("prevlat", "" + addresses);
            if (addresses != null && (addresses.size() != 0)) {
                String address = getLocationAddress(addresses);
                Log.d("mouni_address", "" + address);
                l_address = address; // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                l_city = addresses.get(0).getLocality();
                l_state = addresses.get(0).getAdminArea();
                l_country = addresses.get(0).getCountryName();
                if(l_address.equals(""))
                {
                    l_address=addresses.get(0).getAddressLine(0)+addresses.get(0).getMaxAddressLineIndex();
                }

                Log.d("l_address", "" + l_address);
                Log.d("l_city", "" + l_city);
                Log.d("l_state", "" + l_state);
                Log.d("l_country", "" + l_country);


                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Log.d("time", "" + time);
                Date date = new Date();
                d_time = dateFormat.format(date);
                long atTime = location.getTime();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
                Log.d("timer", "" + mLastUpdateTime);

                Log.d("date and time:", dateFormat.format(date));
                sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("address", "geocoder");
                editor.putString("currentlatitude", " " + currentLatitude);
                editor.putString("currentlongitude", " " + currentLongitude);
                editor.putString("d_time", d_time);
                editor.putString("mlastupdatetime", "" + mLastUpdateTime);
                editor.putString("l_address", "" + l_address);
                editor.putString("l_city", l_city);
                editor.putString("l_state", l_state);
                editor.putString("l_country", l_country);
                editor.commit();
                Toast.makeText(getApplicationContext(), "its time to work from geocoder", Toast.LENGTH_SHORT).show();
                singleton.setUsername(session.getLogInUserDisplayName());
                singleton.setLatitude(String.valueOf(currentLatitude));
                singleton.setLongitude(String.valueOf(currentLongitude));
                singleton.setDate(d_time);
                singleton.setTime(mLastUpdateTime);
                singleton.setAddress("" + l_address);
                singleton.setCity(l_city);
                singleton.setState(l_state);
                singleton.setCountry(l_country);
                MyMapsUser user1 = new MyMapsUser(session.getLogInUserDisplayName(), String.valueOf(currentLatitude), String.valueOf(currentLongitude), d_time, mLastUpdateTime, l_address, l_city, l_state, l_country);
                /*user1.setUsername(Utilities.strUserName);
                user1.setLatitude(String.valueOf(currentLatitude));
                user1.setLongitude(String.valueOf(currentLongitude));
                user1.setDate(d_time);
                user1.setTime(mLastUpdateTime);
                user1.setAddress("" + l_address);
                user1.setCity(l_city);
                user1.setState(l_state);
                user1.setCountry(l_country);*/

                if (mFirebaseuser != null) {

                    mUserId = mFirebaseuser.getUid();
                    Log.d("after_loginmuserId", mUserId);
                }
                Log.d("userrr dispkayname", userDisplayName);

                mDatabaseReference.child("my_maps_user").child(auth.getCurrentUser().getUid()).child(session.getLogInUserDisplayName()).child("maps").child(d_time.replace('/', '-') + "@" + mLastUpdateTime).updateChildren(user1.toMapDetails()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            //listener.getRecentAddedLocation();
                            Toast.makeText(getApplicationContext(), "successfully added lat and long in database from geocoder", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "failed to add lat and long in database from geocoder", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                /*loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.insertMapsEntry(Utilities.strUserName, currentLatitude, currentLongitude, d_time, mLastUpdateTime, "" + l_address, l_city, l_state, l_country);
                loginDataBaseAdapter.close();*/

                startAlert();
            } else {
                Toast.makeText(getApplicationContext(), "No addresses returned from geocoder", Toast.LENGTH_SHORT).show();
                complete_address_json = getCurrentLocationViaJSON(currentLatitude, currentLongitude, getApplicationContext());
                Log.d("complete_adress_json", " " + complete_address_json);

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Log.d("time", "" + time);
                Date date = new Date();
                d_time = dateFormat.format(date);
                long atTime = location.getTime();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
                Log.d("timer", "" + mLastUpdateTime);
                Log.d("date and time:", dateFormat.format(date));
                sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putString("address", "json");
                editor1.putString("currentlatitude", " " + currentLatitude);
                editor1.putString("currentlongitude", " " + currentLongitude);
                editor1.putString("d_time", d_time);
                editor1.putString("mlastupdatetime", "" + mLastUpdateTime);
                editor1.putString("j_address", j_address1);
                editor1.putString("j_city", j_city);
                editor1.putString("j_state", j_state);
                editor1.putString("j_country", j_country);
                editor1.commit();
                Log.d("json_addressla", j_address1 + " " + j_address2 + " " + j_city);
                singleton.setUsername(session.getLogInUserDisplayName());
                singleton.setLatitude(String.valueOf(currentLatitude));
                singleton.setLongitude(String.valueOf(currentLongitude));
                singleton.setDate(d_time);
                singleton.setTime(mLastUpdateTime);
                singleton.setAddress(j_address1);
                singleton.setCity(j_city);
                singleton.setState(j_state);
                singleton.setCountry(j_country);
                MyMapsUser user1 = new MyMapsUser(session.getLogInUserDisplayName(), String.valueOf(currentLatitude), String.valueOf(currentLongitude), d_time, mLastUpdateTime, l_address, l_city, l_state, l_country);
            /*    user1.setUsername(Utilities.strUserName);
                user1.setLatitude(String.valueOf(currentLatitude));
                user1.setLongitude(String.valueOf(currentLongitude));
                user1.setDate(d_time);
                user1.setTime(mLastUpdateTime);
                user1.setAddress(l_address);
                user1.setCity(l_city);
                user1.setState(l_state);
                user1.setCountry(l_country);*/
                if (mFirebaseuser != null) {

                    mUserId = mFirebaseuser.getUid();
                    Log.d("after_loginmuserId", mUserId);
                }
                if (session.getLogInUserDisplayName() != "null") {
                    mDatabaseReference.child("my_maps_user").child(auth.getCurrentUser().getUid()).child(session.getLogInUserDisplayName()).child("maps").child(d_time.replace('/', '-') + "@" + mLastUpdateTime).updateChildren(user1.toMapDetails()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "successfully addeds lat and long in database from json", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(getApplicationContext(), "failed to add  lat and long in database from json", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    Toast.makeText(getApplicationContext(), "its time to work from json", Toast.LENGTH_SHORT).show();
              /*  loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.insertMapsEntry(Utilities.strUserName, currentLatitude, currentLongitude, d_time, mLastUpdateTime, j_address1, j_city, j_state, j_country);
                loginDataBaseAdapter.close();*/

                    startAlert();
                } else {
                    Toast.makeText(getApplicationContext(), "please Login Again", Toast.LENGTH_SHORT).show();

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No addresses returned from geocoder", Toast.LENGTH_SHORT).show();
            boolean status = checkInternetConnection();
            if (status) {
                complete_address_json = getCurrentLocationViaJSON(currentLatitude, currentLongitude, getApplicationContext());
                Log.d("complete_adress_json", " " + complete_address_json);
                Toast.makeText(getApplicationContext(), complete_address_json, Toast.LENGTH_SHORT).show();

                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Log.d("time", "" + time);
                Date date = new Date();
                d_time = dateFormat.format(date);
                long atTime = location.getTime();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
                Log.d("timer", "" + mLastUpdateTime);
                Log.d("date and time:", dateFormat.format(date));
                sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor1 = sharedPreferences.edit();
                editor1.putString("address", "json");
                editor1.putString("currentlatitude", " " + currentLatitude);
                editor1.putString("currentlongitude", " " + currentLongitude);
                editor1.putString("d_time", d_time);
                editor1.putString("mlastupdatetime", "" + mLastUpdateTime);
                editor1.putString("j_address", j_address1);
                editor1.putString("j_city", j_city);
                editor1.putString("j_state", j_state);
                editor1.putString("j_country", j_country);
                editor1.commit();
                singleton.setUsername(session.getLogInUserDisplayName());
                singleton.setLatitude(String.valueOf(currentLatitude));
                singleton.setLongitude(String.valueOf(currentLongitude));
                singleton.setDate(d_time);
                singleton.setTime(mLastUpdateTime);
                singleton.setAddress(j_address1);
                singleton.setCity(j_city);
                singleton.setState(j_state);
                singleton.setCountry(j_country);
                MyMapsUser user1 = new MyMapsUser(session.getLogInUserDisplayName(), String.valueOf(currentLatitude), String.valueOf(currentLongitude), d_time, mLastUpdateTime, complete_address_json, j_city, j_state, j_country);
                Log.d("json_addressla", j_address1 + " " + j_address2 + " " + j_city + " " + complete_address_json);

               /* user1.setUsername(Utilities.strUserName);
                user1.setLatitude(String.valueOf(currentLatitude));
                user1.setLongitude(String.valueOf(currentLongitude));
                user1.setDate(d_time);
                user1.setTime(mLastUpdateTime);
                user1.setAddress(l_address);
                user1.setCity(l_city);
                user1.setState(l_state);
                user1.setCountry(l_country);*/
                if (mFirebaseuser != null) {

                    mUserId = mFirebaseuser.getUid();
                    Log.d("after_loginmuserId", mUserId);
                }

                if (session.getLogInUserDisplayName() != "null") {
                    try {
                        mDatabaseReference.child("my_maps_user").child(auth.getCurrentUser().getUid()).child(session.getLogInUserDisplayName()).child("maps").child(d_time.replace('/', '-') + "@" + mLastUpdateTime).updateChildren(user1.toMapDetails()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "successfully added lat and long in database from jsonuu", Toast.LENGTH_SHORT).show();

                                } else {
                                    Toast.makeText(getApplicationContext(), "failed to add  lat and long in database from jsonuuu", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
                /*loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.insertMapsEntry(Utilities.strUserName, currentLatitude, currentLongitude, d_time, mLastUpdateTime, j_address1,j_city,j_state,j_country);
                loginDataBaseAdapter.close();*/
                        startAlert();
                    } catch (Exception e1) {
                        Log.d("error_message_ingm", e1.getMessage());
                    }
                } else
                    Toast.makeText(getApplicationContext(), "please Login again", Toast.LENGTH_SHORT).show();

            } else
                Toast.makeText(getApplicationContext(), "you dont have internet connection", Toast.LENGTH_SHORT).show();
        }
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




/*
              getLocationInfo(currentLatitude,currentLongitude);
*/

    private void menuOnclickAttach() {
        dialog = new Dialog(GoogleMaps.this);
        dialog.requestWindowFeature(dialog.getWindow().FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_menu_items);
        dialog.setCancelable(true);
        dialog.getWindow().setGravity(Gravity.RIGHT);
        dialog.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
        dialog.getWindow().getAttributes().height = ViewGroup.LayoutParams.MATCH_PARENT;
        dialogInitilization();
        dialog.show();
    }

    private void dialogInitilization() {
        ll_profile = (LinearLayout) findViewById(R.id.ll_profile);
        ll_contacts = (LinearLayout) findViewById(R.id.ll_contacts);
        ll_messages = (LinearLayout) findViewById(R.id.ll_messages);
        ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
        ll_location_history = (LinearLayout) findViewById(R.id.ll_location_history);
        ll_profile.setOnClickListener(this);
        ll_contacts.setOnClickListener(this);
        ll_location_history.setOnClickListener(this);
        ll_messages.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
    }


    protected void sendSMSMessage() {
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        client_details = loginDataBaseAdapter.getAllContactPhonenumber();
        if (client_details != null) {
            if (client_details.getCount() == 0) {
                Toast.makeText(getApplicationContext(), "you didnt add contacts ,please add contacts", Toast.LENGTH_SHORT).show();

            } else {
                Log.d("allcontact phonenumbers", "" + client_details.getCount());
                client_details.moveToFirst();
                do {

                    contact_phonenumber = client_details.getString(client_details.getColumnIndex("contact_phonenumber"));
                    String message = "iam at " + l_address + "," + l_city + "," + l_state + ".";

                    try {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contact_phonenumber, null, message, null, null);
                        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                        loginDataBaseAdapter = loginDataBaseAdapter.open();
                        loginDataBaseAdapter.deleteAllContactsEntry();
                        loginDataBaseAdapter.close();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    Log.d("contact_phonenumber", "" + contact_phonenumber);

                } while (client_details.moveToNext());
                loginDataBaseAdapter.close();
            }

        }

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
        switch (v.getId()) {

            case R.id.ll_profile:
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                break;
            case R.id.ll_contacts:
                acessContacts();
                break;

            case R.id.llv_buddies:
                startActivity(new Intent(GoogleMaps.this, BuddiesActivity.class));
/*
              displayContacts();
*/

                /*AlertDialog.Builder builder1 = new AlertDialog.Builder(GoogleMaps.this);
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
                alert11.show();*/

                break;
            case R.id.ll_location_history:

                startActivity(new Intent(getApplicationContext(), MapDetails.class));
                break;
            case R.id.ll_messages:

                startActivity(new Intent(GoogleMaps.this, TrackMyBuddy.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

                // sendSMSMessage();

                break;
            case R.id.ll_logout:
                onPressed();

               /* if (AccessToken.getCurrentAccessToken() != null) {

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
                finish();*/
                break;
        }
    }

    private void acessContacts() {
        boolean allow_contacts = mayRequestContacts();
        if (allow_contacts) {
            startActivity(new Intent(getApplicationContext(), ContactRegistrationActivity.class));

        }
        //dialog.cancel();
        // This Build is < 6 , you can Access contacts here.
    }


    private void displayContacts() {

        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        startActivityForResult(intent, PICK_CONTACT);
    }

    @Override
    public void onConnected(Bundle bundle) {
        try {
            locationManager = (LocationManager) getApplicationContext()
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
               else if (isGPSEnabled) {
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
                    try {
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                        mMap.getUiSettings().setZoomControlsEnabled(true);
                    } catch (Exception e) {
                        Log.d("gps_message", " " + e.getMessage());
                        // utilities.showAlert("Gps is disabled in your device,please enable it");

                    }

                }
            } else {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingintent);
                pendingintent = PendingIntent.getActivity(this, 0, new Intent(this, MyBroadcastReciever.class), PendingIntent.FLAG_ONE_SHOT);
                try {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                } catch (Exception e) {
                    Log.d("gps_message", " " + e.getMessage());
                    //utilities.showAlert("Gps is disabled in your device,please enable it");
                }

            }
           /* buildGoogleApiClient();
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, pendingintent);
            pendingintent = PendingIntent.getActivity(this, 0,new Intent(this,MyBroadcastReciever.class),PendingIntent.FLAG_ONE_SHOT);//FLAG_ONE_SHOT - only once pIntent can use the myIntent*/

        } else {
            //handleNewLocation(location);

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
        AlertDialog.Builder builder = new AlertDialog.Builder(GoogleMaps.this);
        builder.setMessage(connectionResult.getErrorMessage())
                .setTitle(R.string.login_error_title)
                .setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();
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
        Intent i = new Intent(this, MyBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(), 60 * 60 * 1000, pendingIntent);
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



    /* class MyBroadReceiver extends BroadcastReceiver {
         MediaPlayer mp;
         @Override
         public void onReceive(Context context, Intent intent) {
 //            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, this);
             Log.d("GPS Enabled", "GPS Enabled");
             if(location!=null)
             updateChanged(location);

             Toast.makeText(context, "its time to work ", Toast.LENGTH_SHORT).show();


         }
     }
 */

    public void onPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (AccessToken.getCurrentAccessToken() != null) {
                            LoginManager.getInstance().logOut();
                            session.logoutUser();
                            singleton.setLoginUserDisplayName(null);
                            singleton.setLoginUserEmailId(null);
                            singleton.setLoginActiveStatus(null);
                            Toast.makeText(getApplicationContext(), "you logged out successfully", Toast.LENGTH_SHORT).show();


                        }
                       /* else if (Utilities.mGoogleApiClient.isConnected())
                        {
                            Plus.AccountApi.clearDefaultAccount(Utilities.mGoogleApiClient);
                            Utilities.mGoogleApiClient.disconnect();
                            Utilities.mGoogleApiClient.connect();
                            ProfileActivity.tv_fname.setText("");
                            ProfileActivity.tv_lname.setText("");
                            ProfileActivity.tv_femail.setText("");
                            Toast.makeText(getApplicationContext(), "you logged out successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }*/
                        else {

                            signOut();


                        }
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

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

    public static String getCurrentLocationViaJSON(double lat, double lng, Context con) {

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
                    Log.d("hey_long_name", " " + long_name);
                    Log.d("hey_type_name", " " + Type);


                    if (Type.equalsIgnoreCase("street_number")) {
                        Address1 = long_name + " ";
                        Log.d("street_num_address1"," "+Address1);
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
                //Toast.makeText(con.getApplicationContext(), currentLocation, Toast.LENGTH_SHORT).show();

                Log.d("json_location", "" + currentLocation);
                Log.d("json_address", " " + j_address1 + " " + j_address2 + " " + j_city + "" + j_country + " " + j_state + " " + j_country);

            }
        } catch (Exception e) {
            Log.d("exceptionhaha", "" + e.getMessage());

        }
        return currentLocation;

    }

    private boolean checkInternetConnection() {

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, "wifi_maps Connected ", Toast.LENGTH_LONG).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, "mobile_maps Connected ", Toast.LENGTH_LONG).show();
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
                                ActivityCompat.requestPermissions(GoogleMaps.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                                mMap.getUiSettings().setZoomControlsEnabled(true);
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
                        mMap.setMyLocationEnabled(true);
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

                        fm.getMapAsync(GoogleMaps.this);
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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);


            }
        } else {
            mMap.setMyLocationEnabled(true);


        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class BottomNavigationItemsAdapter extends RecyclerView.Adapter<BottomNavigationItemsAdapter.MyViewHolder> {
        LayoutInflater infilate;
        List<String> data_items;
        ArrayList<String> selections;
        View itemView;
        Context context;
        String bottomNavItems[];
        int[] bottomNavIcons;

        public BottomNavigationItemsAdapter(Context context, int[] bottom_nav_icons, String[] bottom_nav_items) {

            this.selections = new ArrayList<String>();
            this.bottomNavIcons = bottom_nav_icons;
            infilate = LayoutInflater.from(context);
            this.context = context;
            this.bottomNavItems = bottom_nav_items;

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


            itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_menu_items, parent, false);

            return new MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            holder.setIsRecyclable(false);


            try {
                if (selections.get(position).equals("1")) {
                    holder.tv_menu_item.setTextColor(Color.parseColor("#03a9f4"));
                } else {

                    holder.tv_menu_item.setTextColor(Color.parseColor("#8D8D8D"));

                }


            } catch (Exception e) {

                Log.d("catch", "--->" + e);
            }

            holder.iv_menu_icon.setImageResource(bottomNavIcons[position]);
            holder.tv_menu_item.setText(bottomNavItems[position]);


            itemView.setOnClickListener(new View.OnClickListener()

            {
                @Override
                public void onClick(View v) {

                    selections.clear();

                    for (int i = 0; i < bottomNavIcons.length; i++) {
                        if (position == i) {

                            selections.add("1");

                        } else
                            selections.add("0");


                    }
                    String selectedCategoryName = bottomNavigationMenuItems[position];

                    Log.d("selected_category", selectedCategoryName);
                    // Log.d("selectedCategoryId", selectedCategoryId);

                    // displayView(position);


                    notifyDataSetChanged();

                }
            });
        }


        /**
         * Returns the total number of items in the data set held by the adapter.
         *
         * @return The total number of items in this adapter.
         */
        @Override
        public int getItemCount() {

            return bottomNavItems.length;

        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_menu_item;
            ImageView iv_menu_icon;


            public MyViewHolder(View itemView) {
                super(itemView);

                tv_menu_item = (TextView) itemView.findViewById(R.id.tv_menu_name);
                iv_menu_icon = (ImageView) itemView.findViewById(R.id.iv_menu_icon);
            }
        }
    }
}

