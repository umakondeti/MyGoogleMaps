package com.example.umamaheshwari.mygooglemaps;

/**
 * Created by UmaMaheshwari on 1/22/2016.
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.ui.IconGenerator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MapDetails extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000 * 60 * 1; //1 minute
    private static final long FASTEST_INTERVAL = 1000 * 60 * 1; // 1 minute
    public static final long MIN_TIME_BETWEEN_UPDATES = 1000 * 60 * 5; // 5 minutes
    public static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    private Marker myMarker;
    Button btnFusedLocation;
    TextView tvLocation;
    static String j_address1, j_address2, j_city, j_state, j_country, j_pin;
    String l_address, l_city, l_state, l_country, contact_phonenumber;

    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String mLastUpdateTime, db;
    GoogleMap googleMap;
    ImageView iv_date, iv_arrow;
    DatePicker datePicker;
    SharedPreferences sharedPreferences;
    Geocoder geocoder;
    List<Address> addresses;
    Cursor client_details;
    PolylineOptions polylineOptions;
    int coun = 0;
    int c = 0;
    private ArrayList<LatLng> arrayPoints = null;

    /*firebase database*/
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUsers;
    private DatabaseReference mRootRef;
    private String mUserId;
    SessionManager session;
    String UserName,Date;
    Singleton singleton;
    Firebase ref;
    int value_count = 0;
    DatabaseReference mMapsRef, mMapsRefs;
    LoginDataBaseAdapter loginDataBaseAdapter;
    private DatabaseReference mDatabaseReference;
    String date, userName, time, address, state, country, city;
    double lat, lon;
    SupportMapFragment fm;
    public boolean gotDetails = false;
    String selected_date;
    int selected_date_child_count;
    int all_date_child_count;
    String userDisplayName;

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_googlemap);
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            startActivity(new Intent(MapDetails.this, GoogleMaps.class));
        }
/*
       createLocationRequest();
*/
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUsers = mFirebaseAuth.getCurrentUser();
        // Initialize Database Reference
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mRootRef.keepSynced(true);

        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/" + mUserId);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);

        session = new SessionManager(getApplicationContext());
        singleton = Singleton.getInstance();
        if (singleton.getLoginUserDisplayName() != null) {
            userDisplayName = singleton.getLoginUserDisplayName();
            Log.d("check_display_name", "" + userDisplayName);

        }
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        iv_date = (ImageView) findViewById(R.id.iv_date);
        iv_arrow = (ImageView) findViewById(R.id.iv_arrow);
        datePicker = (DatePicker) findViewById(R.id.datePicker1);
        arrayPoints = new ArrayList<LatLng>();
        polylineOptions = new PolylineOptions();

        sharedPreferences = getSharedPreferences("uma", Context.MODE_PRIVATE);

        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
     /*   if (mUserId != null) {
            mRootRef = FirebaseDatabase.getInstance().getReference();
            userDisplayName = singleton.getLoginUserDisplayName();
            Log.d("getting_username", "" + userDisplayName);
            mMapsRef = mRootRef.child("my_maps_user").child(mUserId);

            //    selected_date = db.replace('/', '-');
            //  Query query = mMapsRef.orderByChild("Date").equalTo(db);

            Log.d("mMapsRef", "" + mMapsRef);
        }*/
        getAllDateDetails();

        iv_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (datePicker.getVisibility() == View.GONE)
                    selectDate();
                else
                    datePicker.setVisibility(View.GONE);

            }
        });
        iv_arrow.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MapDetails.this, GoogleMaps.class));
                    }
                });
        fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        fm.getMapAsync(MapDetails.this);

        // googleMap = fm.getMap();

        //googleMap.getUiSettings().setZoomControlsEnabled(true);
        final Calendar c = Calendar.getInstance();

        int maxYear = c.get(Calendar.YEAR); // this year ( 2011 ) - 20 = 1991
        int maxMonth = c.get(Calendar.MONTH);
        int maxDay = c.get(Calendar.DAY_OF_MONTH);

        int minYear = 1960;
        int minMonth = 0; // january
        int minDay = 25;

        datePicker.init(maxYear, maxMonth, maxDay, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                Log.d("changeday", "" + dayOfMonth);
                Log.d("chamonth", "" + monthOfYear + 1);
                Log.d("chayear", "" + year);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                date = year + "/0" + (monthOfYear + 1) + "/" + dayOfMonth;
               /* if (coun!=0) {
                    coun++;
                    googleMap.clear();
                }*/
                //if(googleMap!=null)
                // googleMap = fm.getMap();

                googleMap.clear();
                arrayPoints.clear();
                all_date_child_count = 0;
                selected_date_child_count = 0;
                loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.deleteAllLocationsEntry();
                try {
                    Date d1 = dateFormat.parse(date);
                    db = dateFormat.format(d1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Toast.makeText(MapDetails.this, " getting results..... " + db, Toast.LENGTH_LONG).show();

                Log.d("date", "" + date);
                RequestData(new getSelectedDateListener() {


                    @Override
                    public void getSelectedDateResult(Cursor client_details) {
                        int after_client_count = client_details.getCount();
                        Log.d("after_client_count", "" + after_client_count);
                        Log.d("selected_date", "" + selected_date);

                        // client_details = loginDataBaseAdapter.getMapsClientDetails(db);
                        // Log.d("check_client_count",""+clientdetails.getCount());
                        if (client_details.getCount() != 0) {
                            Log.d("gbhb", "" + client_details.getCount());
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
                                                    getAddress(lat, lon);
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
                                Toast.makeText(MapDetails.this, "you didnt use google maps on that date " + db, Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MapDetails.this, "you didnt use google maps on that date " + db, Toast.LENGTH_SHORT).show();
                        }
                        loginDataBaseAdapter.close();
                    }

                    @Override
                    public void getSelDateErrorResult(Cursor String) {

                    }


                });
                //   new ClientDetails().execute();

            }
        });
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
        LatLng latLng = new LatLng(lat, lon);
        if (counts == 1) {
            googleMap.clear();
            arrayPoints.clear();

            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(userName+"\n"+time + "\n" + db)
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            c++;

        } else if (counts == (client_details.getCount() - 1)) {
            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(userName+"\n"+time + "\n" + db)
                    .snippet(address)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            c++;

        } else {
            myMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, lon))
                    .title(userName+"\n"+time + "\n" + db)
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

                LinearLayout info = new LinearLayout(MapDetails.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MapDetails.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MapDetails.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });
        /*    move to current location*/
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
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

    private void selectDate() {
        datePicker.setVisibility(View.VISIBLE);

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth() + 1;
        int year = datePicker.getYear();
        Log.d("day", "" + day);
        Log.d("month", "" + month);
        Log.d("year", "" + year);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
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

    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
    }

//    protected void startLocationUpdates() {
//        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
//                mGoogleApiClient, mLocationRequest, this);
//        Log.d(TAG, "Location update started ..............: ");
//    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
/*
        addMarker();
*/
    }

    private void addMarker() {
        MarkerOptions options = new MarkerOptions();

        // following four lines requires 'Google Maps Android API Utility Library'
        // https://developers.google.com/maps/documentation/android/utility/
        // I have used this to display the time as title for location markers
        // you can safely comment the following four lines but for this info
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(db)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        LatLng currentLatLng = new LatLng(lat, lon);
        options.position(currentLatLng);
        long atTime = mCurrentLocation.getTime();
        Marker mapMarker = googleMap.addMarker(options);
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
        mapMarker.setTitle(mLastUpdateTime);
        Log.d(TAG, "Marker added.............................");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                13));
        Log.d(TAG, "Zoom done.............................");
    }

    @Override
    protected void onPause() {
        super.onPause();
/*
        stopLocationUpdates();
*/
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
/*
            startLocationUpdates();
*/
            Log.d(TAG, "Location update resumed .....................");
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.equals(myMarker)) {
            MarkerOptions options = new MarkerOptions();
            IconGenerator iconFactory = new IconGenerator(this);
            iconFactory.setStyle(IconGenerator.STYLE_PURPLE);
            options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(db)));
            options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
            myMarker = googleMap.addMarker(options);
            myMarker.setTitle(GoogleMaps.address);


        }
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMaps) {
        googleMap = googleMaps;

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
/*
                buildGoogleApiClient();
*/
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

            }
        } else {
/*
            buildGoogleApiClient();
*/
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);

        }
    }

    public void getAllDateDetails() {

        mFirebaseUsers = mFirebaseAuth.getCurrentUser();
        mFirebaseAuth.getCurrentUser();
        // Initialize Database Reference

        //this mRootRef contains the root of the json tree

        if (mFirebaseUsers != null) {

            mUserId = mFirebaseUsers.getUid();
            Log.d("mapdetails_loginmuserId", mUserId);
        }
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/" + mUserId);

        //Log.d("getDate", "" + db);
        if (mUserId != null) {
            mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.keepSynced(true);

            userDisplayName = session.getLogInUserDisplayName();
            Log.d("getting_username", "" + userDisplayName);
            mMapsRef = mRootRef.child("my_maps_user").child(mUserId).child(session.getLogInUserDisplayName()).child("maps");
            mMapsRef.keepSynced(true);

            //    selected_date = db.replace('/', '-');
            //  Query query = mMapsRef.orderByChild("Date").equalTo(db);

            Log.d("mMapsRef", "" + mMapsRef);
        }

        //   DatabaseReference mUsersRef = mConditionRef.child(mUserId);
        //  DatabaseReference   mMapsRef = mUsersRef.child(String.valueOf(mUsersRef.startAt(selected_date))).child("maps").child("date");
        //Log.d("getting key",""+mMapsRef.push().getKey());
        //Log.d("mMapsRef",""+mMapsRef);
        //Query query3=query2.

        mMapsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("hellooo", dataSnapshot.toString());
                value_count = (int) dataSnapshot.getChildrenCount();
                String UserName = dataSnapshot.child("UserName").getValue(String.class);
                String dates =dataSnapshot.child("Date").getValue(String.class);
                Log.d("adithya",""+UserName+ " "+dates);
                Log.d("value_count", "" + value_count);
                if (value_count != 0)
                    singleton.setAllDateValues("" + value_count);
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                          /*  if(issue.child("Date").getValue().toString().equals(db))
                            {
                                break;
                            }*/
                        MyMapsUser userData = null;
                        userData = issue.getValue(MyMapsUser.class);
                        String date = userData.getDate();
                        //            Log.d("compare_date", "" + date + "-->" + db);

              /*          if (date != null)
                            if (date.equals(db)) {
                                String UserName = userData.getUsername();
                                String latitude = userData.getLatitude();
                                String longitude = userData.getLongitude();
                                String time = userData.getTime();
                                Log.d("retrieve_usersdata", time + " " + latitude + "" + longitude + "" + date + "" + UserName);
                            }
              */          // do something with the individual "issues"
                        // Log.d("getdatafromfirebase",""+issue.child("date").getValue(MyMapsUser.class));
                    }
                }
               /* String checkalldatevalues = singleton.getAllDateValues();
                Log.d("checkinalldatevalues", checkalldatevalues);*/
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());


            }
        });
    }

    public void getUserMapData(DataSnapshot dataSnapshot,final getSelectedDateListener listener) {
        long retrieve_count = dataSnapshot.getChildrenCount();
        Log.d("retrieve_count", "" + retrieve_count);
        all_date_child_count++;
        Log.d("all_date_child_count", "" + all_date_child_count + "~!~" + value_count);
        String all_date_values = singleton.getAllDateValues();
        Log.d("singleton_all_date", "" + all_date_values + "!~!" + all_date_child_count);
        // A new comment has been added, add it to the displayed list
                   /* MyMapsUser comment = dataSnapshot.getValue(MyMapsUser.class);
                    String retrieve_date = comment.getDate();
                    Log.d("retrieve_date", "" + retrieve_date);
                    Log.d("comment", "" + comment);
                    Log.d("datasnapshot", "" + dataSnapshot.toString());
                    Log.d("selected_datesss", "" + selected_dates);
                    Log.d("meaning", "" + s);
                    boolean isSelected = false;*/
        Log.d("testdate", "" + db);


                 /*  mMapsRef.child(dataSnapshot.getKey()).child("maps").child("date").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshots) {
                           Log.d("selected_date_result",dataSnapshots.toString());
                          *//* String yanti=dataSnapshots.getValue().toString();
                           Log.d("yanti",yanti);*//*
                          if(dataSnapshots.getValue().equals(db))
                          {
                              Log.d("equal_dates are",dataSnapshots.getValue() +""+db);

                          }
                           for(DataSnapshot post: dataSnapshots.getChildren())
                           {
                               MyMapsUser Date = post.getValue(MyMapsUser.class);
                               String my_date=Date.getDate();
                               Log.d("my_date",my_date);
                               if(my_date.equals(db))
                               {
                                   Log.d("equal_dates are",my_date +""+db);
                               }
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });*/
        Log.d("RETRIEVE_datasnapshots", dataSnapshot.getKey() + "--~!!!--" + selected_date);
        String[] compareKeysWithDate = dataSnapshot.getKey().split("@");
        if (compareKeysWithDate[0].equals(selected_date)) {
            loginDataBaseAdapter = loginDataBaseAdapter.open();
            // loginDataBaseAdapter.deleteAllLocationsEntry();
            // for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
            selected_date_child_count++;
            Log.d("postsnapshot_count", "" + selected_date_child_count);
            Log.d("selected_date_res", "" + dataSnapshot);
            Log.d("know_childrens",""+dataSnapshot.getChildren());
            String key = dataSnapshot.getKey();
            // Map<String, Object> map = (HashMap<String, Object>) dataSnapshot.getValue();

            MyMapsUser selectedDateUserDetails = dataSnapshot.getValue(MyMapsUser.class);
            String date = selectedDateUserDetails.getDate();
            String userName = selectedDateUserDetails.getUserName();
            Double latitude = Double.parseDouble(selectedDateUserDetails.getLatitude());
            Double longitude = Double.parseDouble(selectedDateUserDetails.getLongitude());
            String time = selectedDateUserDetails.getTime();
            String address = selectedDateUserDetails.getAddress();
            String city = selectedDateUserDetails.getCity();
            String state = selectedDateUserDetails.getState();
            String country = selectedDateUserDetails.getCountry();
            loginDataBaseAdapter.insertMapsEntry(session.getLogInUserDisplayName(), latitude, longitude, db, time, address, city, state, country);
            loginDataBaseAdapter.close();
            Log.d("date+db", "" + date + "~~!!~~" + db + "~~!!~~" + selected_date);
            if (selected_date_child_count == 1)
                Log.d("selectedDateUserDetails", "" + key + "~" + date + "~" + userName + "~" + latitude + "~" + longitude + "~" + time + "~" + address + "~" + city + "~" + state + "~" + country);
            Log.d("selected_datasnapshots", key + "--->" + selected_date);
            Log.d("userDetails", "" + selectedDateUserDetails);
            Log.d("-------------->", "finished" + "---------------->");

            // }


        }
        Log.d("got_details", "" + gotDetails);
        Log.d("check_alldate_sel_date", "" + all_date_child_count + "" + value_count);
//if((all_date_child_count!=0)&& (value_count!=null)) {
        if (all_date_child_count == value_count) {

            loginDataBaseAdapter = loginDataBaseAdapter.open();
            client_details = loginDataBaseAdapter.getMapsClientDetails(db);

            Log.d("acidity_chal....", "" + client_details.getCount());
            //}
            listener.getSelectedDateResult(client_details);

            Log.d("before", "" + client_details);
        }
//}
/*else
{
    Toast.makeText(getApplicationContext(),"Please wait,while getting the results...",Toast.LENGTH_LONG).show();
}*/

        // ...
        //adapter.add((String) dataSnapshot.child("title").getValue());

    }

    public interface getSelectedDateListener {
        public void getSelectedDateResult(Cursor String);
        //  public Cursor getSelectedDateResult();

        public void getSelDateErrorResult(Cursor String);

    }

    public void RequestData(final getSelectedDateListener listener){

        //publishProgress("Sleeping..."); // Calls onProgressUpdate()

        mFirebaseUsers = mFirebaseAuth.getCurrentUser();
        mFirebaseAuth.getCurrentUser();
        // Initialize Database Reference

        //this mRootRef contains the root of the json tree

        if (mFirebaseUsers != null) {

            mUserId = mFirebaseUsers.getUid();
            Log.d("mapdetails_loginmuserId", mUserId);
        }
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/" + mUserId);

        Log.d("getDate", "" + db);
        if (mUserId != null) {
            mRootRef = FirebaseDatabase.getInstance().getReference();
            mRootRef.keepSynced(true);
            mMapsRefs = mRootRef.child("my_maps_user").child(mUserId).child(session.getLogInUserDisplayName()).child("maps");
            mMapsRefs.keepSynced(true);

            selected_date = db.replace('/', '-');
            Query query = mMapsRefs.orderByChild("Date").equalTo(db);

            Log.d("mMapsRef", "" + mMapsRefs);
        }
        if (singleton.getLoginUserDisplayName() != null) {
            userDisplayName = singleton.getLoginUserDisplayName();
            Log.d("check_display_name", "" + userDisplayName);

        }

        if (mFirebaseUsers != null) {

            mUserId = mFirebaseUsers.getUid();
            Log.d("Mapdetails_userid", mUserId);
        }
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/" + mUserId + "/" + userDisplayName + "/maps");
        final String selected_dates = db.replace('/', '-');

        // com.firebase.client.Query queryRef = ref.orderByChild("Date").equalTo(selected_date);
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);

        Log.d("selec_daes", "" + selected_dates);
        mMapsRefs = mRootRef.child("my_maps_user").child(mUserId).child(session.getLogInUserDisplayName()).child("maps");
        mMapsRefs.keepSynced(true);
        Log.d("inside_click", "" + session.getLogInUserDisplayName());
        Log.d("inside_maps", "" + mMapsRef);
        mMapsRefs.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildAdded_maps:" + dataSnapshot.getKey());
                getUserMapData(dataSnapshot,listener);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
               /* MyMapsUser newComment = dataSnapshot.getValue(MyMapsUser.class);
                String commentKey = dataSnapshot.getKey();*/
                getUserMapData(dataSnapshot,listener);

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                // adapter.remove((String) dataSnapshot.child("title").getValue());
                Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());
                getUserMapData(dataSnapshot,listener);

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so remove it.
              //  String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                MyMapsUser movedComment = dataSnapshot.getValue(MyMapsUser.class);
                String commentKey = dataSnapshot.getKey();
                getUserMapData(dataSnapshot,listener);

                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "postComments:onCancelled", databaseError.toException());

                Toast.makeText(getApplicationContext(), "Failed to load comments.",
                        Toast.LENGTH_SHORT).show();
            }
        });
                 /*{
                @Override
                public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    MyMapsUser comment = dataSnapshot.getValue(MyMapsUser.class);
                    Log.d("comment", "" + comment);
                    Log.d("datasnapshot", "" + dataSnapshot.toString());
                    Log.d("selected_datesss", "" + selected_date);
                    Log.d("meaning", "" + s);
                    if (dataSnapshot.getKey().contains(selected_date)) {
                        String key = dataSnapshot.getKey();

                        Log.d("selected_datasnapshots", key + "--->" + selected_date);
                    }

                    // ...
                    //adapter.add((String) dataSnapshot.child("title").getValue());
                }

                @Override
                public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    MyMapsUser newComment = dataSnapshot.getValue(MyMapsUser.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {
// adapter.remove((String) dataSnapshot.child("title").getValue());
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    MyMapsUser movedComment = dataSnapshot.getValue(MyMapsUser.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.w(TAG, "postComments:onCancelled", firebaseError.toException());
                    Toast.makeText(getApplicationContext(), "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }

            };
            queryRef.addChildEventListener(listener);
*/


        // loginDataBaseAdapter.close();

        //if (gotDetails) {


    }

    private class ClientDetails extends AsyncTask<String, String, Cursor> {


        @Override
        protected Cursor doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()

            mFirebaseUsers = mFirebaseAuth.getCurrentUser();
            mFirebaseAuth.getCurrentUser();
            // Initialize Database Reference

            //this mRootRef contains the root of the json tree

            if (mFirebaseUsers != null) {

                mUserId = mFirebaseUsers.getUid();
                Log.d("mapdetails_loginmuserId", mUserId);
            }
            Firebase.setAndroidContext(getApplicationContext());
            ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/" + mUserId);

            Log.d("getDate", "" + db);
            if (mUserId != null) {
                mRootRef = FirebaseDatabase.getInstance().getReference();
                mRootRef.keepSynced(true);
                mMapsRef = mRootRef.child("my_maps_user").child(mUserId).child(session.getLogInUserDisplayName()).child("maps");
                mMapsRef.keepSynced(true);
                selected_date = db.replace('/', '-');
                Query query = mMapsRef.orderByChild("Date").equalTo(db);

                Log.d("mMapsRef", "" + mMapsRef);
            }

            //   DatabaseReference mUsersRef = mConditionRef.child(mUserId);
            //  DatabaseReference   mMapsRef = mUsersRef.child(String.valueOf(mUsersRef.startAt(selected_date))).child("maps").child("date");
            //Log.d("getting key",""+mMapsRef.push().getKey());
            //Log.d("mMapsRef",""+mMapsRef);
            //Query query3=query2.

            mMapsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("hellooo", dataSnapshot.toString());
                    if (dataSnapshot.exists()) {
                        // dataSnapshot is the "issue" node with all children with id 0
                        for (DataSnapshot issue : dataSnapshot.getChildren()) {
                          /*  if(issue.child("Date").getValue().toString().equals(db))
                            {
                                break;
                            }*/
                            MyMapsUser userData = null;
                            userData = issue.getValue(MyMapsUser.class);
                            String date = userData.getDate();
                            Log.d("compare_date", "" + date + "-->" + db);

                            if (date != null)
                                if (date.equals(db)) {
                                    String UserName = userData.getUserName();
                                    String latitude = userData.getLatitude();
                                    String longitude = userData.getLongitude();
                                    String time = userData.getTime();
                                    Log.d("retrieve_usersdata", time + " " + latitude + "" + longitude + "" + date + "" + UserName);
                                }
                            // do something with the individual "issues"
                            // Log.d("getdatafromfirebase",""+issue.child("date").getValue(MyMapsUser.class));
                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "loadPost:onCancelled", databaseError.toException());


                }
            });


            if (mFirebaseUsers != null) {

                mUserId = mFirebaseUsers.getUid();
                Log.d("Mapdetails_userid", mUserId);
            }
            Firebase.setAndroidContext(getApplicationContext());
            ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/" + mUserId);
            final String selected_dates = db.replace('/', '-');

            // com.firebase.client.Query queryRef = ref.orderByChild("Date").equalTo(selected_date);
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mDatabaseReference.keepSynced(true);

            Log.d("selec_daes", "" + selected_dates);

            mMapsRef.addChildEventListener(new com.google.firebase.database.ChildEventListener() {
                @Override
                public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                   /* MyMapsUser comment = dataSnapshot.getValue(MyMapsUser.class);
                    String retrieve_date = comment.getDate();
                    Log.d("retrieve_date", "" + retrieve_date);
                    Log.d("comment", "" + comment);
                    Log.d("datasnapshot", "" + dataSnapshot.toString());
                    Log.d("selected_datesss", "" + selected_dates);
                    Log.d("meaning", "" + s);
                    boolean isSelected = false;*/
                    Log.d("testdate", "" + db);

                 /*  mMapsRef.child(dataSnapshot.getKey()).child("maps").child("date").addValueEventListener(new ValueEventListener() {
                       @Override
                       public void onDataChange(DataSnapshot dataSnapshots) {
                           Log.d("selected_date_result",dataSnapshots.toString());
                          *//* String yanti=dataSnapshots.getValue().toString();
                           Log.d("yanti",yanti);*//*
                          if(dataSnapshots.getValue().equals(db))
                          {
                              Log.d("equal_dates are",dataSnapshots.getValue() +""+db);

                          }
                           for(DataSnapshot post: dataSnapshots.getChildren())
                           {
                               MyMapsUser Date = post.getValue(MyMapsUser.class);
                               String my_date=Date.getDate();
                               Log.d("my_date",my_date);
                               if(my_date.equals(db))
                               {
                                   Log.d("equal_dates are",my_date +""+db);
                               }
                           }
                       }

                       @Override
                       public void onCancelled(DatabaseError databaseError) {

                       }
                   });*/
                    Log.d("RETRIEVE_datasnapshots", dataSnapshot.getKey() + "--~!!!--" + selected_date);
                    String[] compareKeysWithDate = dataSnapshot.getKey().split("@");
                    if (compareKeysWithDate[0].equals(selected_date)) {
                        loginDataBaseAdapter = loginDataBaseAdapter.open();
                        // loginDataBaseAdapter.deleteAllLocationsEntry();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            selected_date_child_count++;
                            Log.d("postsnapshot_count", "" + selected_date_child_count);
                            Log.d("selected_date_res", "" + postSnapshot);
                            String key = dataSnapshot.getKey();
                            MyMapsUser selectedDateUserDetails = postSnapshot.getValue(MyMapsUser.class);
                            String date = selectedDateUserDetails.getDate();
                            String userName = selectedDateUserDetails.getUserName();
                            Double latitude = Double.parseDouble(selectedDateUserDetails.getLatitude());
                            Double longitude = Double.parseDouble(selectedDateUserDetails.getLongitude());
                            String time = selectedDateUserDetails.getTime();
                            String address = selectedDateUserDetails.getAddress();
                            String city = selectedDateUserDetails.getCity();
                            String state = selectedDateUserDetails.getState();
                            String country = selectedDateUserDetails.getCountry();
                            loginDataBaseAdapter.insertMapsEntry(session.getLogInUserDisplayName(), latitude, longitude, date, time, address, city, state, country);
                            // loginDataBaseAdapter.close();
                            Log.d("date+db", "" + date + "~~!!~~" + db + "~~!!~~" + selected_date);
                            if (selected_date_child_count == 1)
                                Log.d("selectedDateUserDetails", "" + key + "~" + date + "~" + userName + "~" + latitude + "~" + longitude + "~" + time + "~" + address + "~" + city + "~" + state + "~" + country);
                            Log.d("selected_datasnapshots", key + "--->" + selected_date);
                            Log.d("userDetails", "" + selectedDateUserDetails);
                            Log.d("-------------->", "finished" + "---------------->");

                        }


                    }
                    Log.d("got_details", "" + gotDetails);


                    // ...
                    //adapter.add((String) dataSnapshot.child("title").getValue());
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    MyMapsUser newComment = dataSnapshot.getValue(MyMapsUser.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    // adapter.remove((String) dataSnapshot.child("title").getValue());
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    MyMapsUser movedComment = dataSnapshot.getValue(MyMapsUser.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(getApplicationContext(), "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            });
                 /*{
                @Override
                public void onChildAdded(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    MyMapsUser comment = dataSnapshot.getValue(MyMapsUser.class);
                    Log.d("comment", "" + comment);
                    Log.d("datasnapshot", "" + dataSnapshot.toString());
                    Log.d("selected_datesss", "" + selected_date);
                    Log.d("meaning", "" + s);
                    if (dataSnapshot.getKey().contains(selected_date)) {
                        String key = dataSnapshot.getKey();

                        Log.d("selected_datasnapshots", key + "--->" + selected_date);
                    }

                    // ...
                    //adapter.add((String) dataSnapshot.child("title").getValue());
                }

                @Override
                public void onChildChanged(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    MyMapsUser newComment = dataSnapshot.getValue(MyMapsUser.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildRemoved(com.firebase.client.DataSnapshot dataSnapshot) {
// adapter.remove((String) dataSnapshot.child("title").getValue());
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onChildMoved(com.firebase.client.DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    MyMapsUser movedComment = dataSnapshot.getValue(MyMapsUser.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.w(TAG, "postComments:onCancelled", firebaseError.toException());
                    Toast.makeText(getApplicationContext(), "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }

            };
            queryRef.addChildEventListener(listener);
*/


            // loginDataBaseAdapter.close();

            if (gotDetails) {
                loginDataBaseAdapter = loginDataBaseAdapter.open();
                client_details = loginDataBaseAdapter.getMapsClientDetails(db);

                Log.d("acidity....", "" + client_details.getCount());
            }
            Log.d("before", "" + client_details);
            return client_details;
        }


        @Override
        protected void onPostExecute(Cursor clientdetails) {

            Log.d("got(details)", "" + gotDetails);
            // Log.d("check_client_count",""+clientdetails.getCount());
            Log.d("after", "" + clientdetails);
            if (gotDetails) {
                loginDataBaseAdapter = loginDataBaseAdapter.open();
                client_details = loginDataBaseAdapter.getMapsClientDetails(db);
                Log.d("acidity()....", "" + client_details.getCount());
            }
            // execution of result of Long time consuming operation
//loginDataBaseAdapter = loginDataBaseAdapter.open();
            //  Log.d("check_client_count",""+client_details.getCount());
            Log.d("selected_date", "" + selected_date);

            // client_details = loginDataBaseAdapter.getMapsClientDetails(db);
            // Log.d("check_client_count",""+clientdetails.getCount());
            if (client_details.getCount() != 0) {
                Log.d("gbhb", "" + client_details.getCount());
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
                                        getAddress(lat, lon);
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
                    Toast.makeText(MapDetails.this, "you didnt use google maps on that date " + db, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MapDetails.this, "you didnt use google maps on that date " + db, Toast.LENGTH_SHORT).show();
            }
            loginDataBaseAdapter.close();
        }


        @Override
        protected void onPreExecute() {
           /* progressDialog = ProgressDialog.show(MapDetails.this,
                    "ProgressDialog",
                    "getting results");*/
        }


        @Override
        protected void onProgressUpdate(String... text) {

        }
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
        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
/*
            Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
*/
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

/*
            Toast.makeText(this, " Not Connected,Internet connection required ", Toast.LENGTH_LONG).show();
*/
            return false;
        }
        return false;
    }

    public void getAddress(double lat, double lon) {
        try {
            geocoder = new Geocoder(MapDetails.this, Locale.getDefault());
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses != null && (addresses.size()!=0)) {
                l_address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                l_city = addresses.get(0).getLocality();
                l_state = addresses.get(0).getAdminArea();
                l_country = addresses.get(0).getCountryName();
                address = l_address;
                state = l_state;
                city = l_city;
                country = l_country;
                Log.e("address", "" + l_address);
                Log.e("address", "" + l_city);
                Log.e("address", "" + l_state);
                Log.e("address", "" + l_country);
                loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.updateMapsEntry(Utilities.strUserName, lat, lon, date, time, address, city, state, country);
                loginDataBaseAdapter.close();

            } else {
                Toast.makeText(getApplicationContext(), "No addresses returned from geocoder", Toast.LENGTH_SHORT).show();
                getCurrentLocationViaJSON(lat, lon);
                address = j_address1 + j_address2;
                state = j_state;
                city = j_city;
                country = j_country;

                loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.updateMapsEntry(Utilities.strUserName, lat, lon, date, time, address, city, state, country);
                loginDataBaseAdapter.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "No addresses returned from geocoder", Toast.LENGTH_SHORT).show();
            boolean status = checkInternetConnection();
            if (status) {
                getCurrentLocationViaJSON(lat, lon);
                address = j_address1 + j_address2;
                state = j_state;
                city = j_city;
                country = j_country;

                loginDataBaseAdapter = loginDataBaseAdapter.open();
                loginDataBaseAdapter.updateMapsEntry(Utilities.strUserName, lat, lon, date, time, address, city, state, country);
                loginDataBaseAdapter.close();

            } else
                Toast.makeText(getApplicationContext(), "you dont have internet connection", Toast.LENGTH_SHORT).show();
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

                        fm.getMapAsync(MapDetails.this);
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


}
