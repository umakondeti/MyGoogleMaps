package com.example.umamaheshwari.mygooglemaps;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by UmaMaheshwari on 1/24/2016.
 */
public class MyBroadcastReciever extends BroadcastReceiver {
    LocationManager locationmanager;
    Location location;
    List<Address> addresses;
    LoginDataBaseAdapter logindatabaseadapter;
    Context context;
    boolean networkEnabled = false, GPSEnabled = false;
    String address, city, state, country;
    Double currentLatitude = null, currentLongitude = null, previous_lattitude, previous_longitude;
    String d_time, mLastUpdateTime;
    static String j_address1 = null, j_address2 = null, j_state = null, j_city = null, j_country = null, j_pin = null;
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 60; // 1 minute
    private LocationListener locListener = new MyLocationListener();

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.d("GPS Enabled", "GPS Enabled");
        locationmanager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationmanager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        try {
            GPSEnabled = locationmanager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            networkEnabled = locationmanager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        // don't start listeners if no provider is enabled

        if (GPSEnabled) {
            locationmanager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locListener);
        }
        if (networkEnabled) {
            locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locListener);
        }
    }

    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // This needs to stop getting the location data and save the battery power.
                if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context.getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                updateChanged(location);
                locationmanager.removeUpdates(locListener);


            }
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
    }

    public void updateChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        double s_lat = 12.48795698058;
        double s_lon = 69.43128613;

        Log.e("currentLat", "" + currentLatitude);
        Log.e("Currrentlon", "" + currentLongitude);
        Geocoder geocoder;
        geocoder = new Geocoder(context.getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(currentLatitude, currentLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if (addresses != null && (addresses.size() != 0)) {
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                Log.e("currentLat", "" + currentLatitude);
                Log.e("Currrentlon", "" + currentLongitude);
                Log.e("currentaddress", "" + address);
                Log.e("caddress", "" + city);
                Log.e("cstate", "" + state);
                Log.e("country", "" + country);
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                Date date = new Date();
                d_time = dateFormat.format(date);
                long atTime = location.getTime();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));

               /* if ((Utilities.strUserName != null) && (currentLatitude != null) && (currentLongitude != null) && (d_time != null) && (mLastUpdateTime != null) && (address != null) && (city != null) && (state != null) && (country != null))

                {
                    if ((previous_lattitude != null) && (previous_longitude != null) && (previous_lattitude.equals(currentLatitude)) && (previous_longitude.equals(currentLongitude)))
                    {
                        Toast.makeText(context.getApplicationContext(), "you are in same location", Toast.LENGTH_SHORT).show();

                    } else if (((previous_lattitude == null) && (previous_longitude == null)) || ((previous_lattitude != currentLatitude) && ((previous_longitude != currentLongitude))))
                    {
                        Toast.makeText(context.getApplicationContext(), "you changed your location geocoder", Toast.LENGTH_SHORT).show();
*/
                Log.d("previous_lat", "" + previous_lattitude);
                Log.d("previous_long", "" + previous_longitude);
                previous_lattitude = currentLatitude;
                previous_longitude = currentLongitude;
                Log.d("previous_latitude", "" + previous_lattitude);
                Log.d("previous_longitude", "" + previous_longitude);
                Log.d("d_time", "" + d_time);
                Log.d("mLastUpdateTime", "" + mLastUpdateTime);
                       /* logindatabaseadapter = new LoginDataBaseAdapter(context.getApplicationContext());
                        logindatabaseadapter = logindatabaseadapter.open();
                        logindatabaseadapter.insertMapsEntry(Utilities.strUserName, currentLatitude, currentLongitude, d_time, mLastUpdateTime, address, city, state, country);
                        logindatabaseadapter.close();*/
                Toast.makeText(context.getApplicationContext(), "its time to work  from geocoder from recievers", Toast.LENGTH_SHORT).show();
                currentLatitude = null;
                currentLongitude = null;
                d_time = null;
                mLastUpdateTime = null;
                address = null;
                city = null;
                state = null;
                country = null;
                   /* }
                }*/
            } else {
                Toast.makeText(context.getApplicationContext(), "No addresses returned from geocoder(recievers)", Toast.LENGTH_SHORT).show();
                boolean status = checkInternetConnection();
                if (status) {
                    getCurrentLocationViaJSON(currentLatitude, currentLongitude);

                    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

                    Date date = new Date();
                    d_time = dateFormat.format(date);
                    long atTime = location.getTime();
                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
                    Log.d("timer", "" + mLastUpdateTime);
                    Log.d("date and time:", dateFormat.format(date));
                    logindatabaseadapter = new LoginDataBaseAdapter(context.getApplicationContext());
                    logindatabaseadapter = logindatabaseadapter.open();
                    /*if ((Utilities.strUserName != null) && (currentLatitude != null) && (currentLongitude != null) && (d_time != null) && (mLastUpdateTime != null) && (j_address1 != null) && (j_city != null) && (j_state != null) && (j_country != null)) {
                        if ((previous_lattitude != null) && (previous_longitude != null) && (previous_lattitude.equals(currentLatitude)) && (previous_longitude.equals(currentLongitude))) {
                            Toast.makeText(context.getApplicationContext(), "you are in same location", Toast.LENGTH_SHORT).show();

                        } else if (((previous_lattitude == null) && (previous_longitude == null)) || ((previous_lattitude != currentLatitude) && ((previous_longitude != currentLongitude)))) {*/
                    previous_lattitude = currentLatitude;
                    previous_longitude = currentLongitude;
                    Log.d("previous_latitude", "" + previous_lattitude);
                    Log.d("previous_longitude", "" + previous_longitude);
                    Log.d("d_time", "" + d_time);

                    Log.d("mLastUpdateTime", "" + mLastUpdateTime);
                  /*  logindatabaseadapter.insertMapsEntry(Utilities.strUserName, currentLatitude, currentLongitude, d_time, mLastUpdateTime, j_address1, j_city, j_state, j_country);
                    logindatabaseadapter.close();*/
                    currentLatitude = null;
                    currentLongitude = null;
                    d_time = null;
                    mLastUpdateTime = null;
                    j_address1 = null;
                    j_city = null;
                    j_state = null;
                    j_country = null;

                    Toast.makeText(context.getApplicationContext(), "its time to work from Json(recievers)", Toast.LENGTH_SHORT).show();
                        /*}
                    }*/
                } else
                    Toast.makeText(context.getApplicationContext(), "you dont have internet connection", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context.getApplicationContext(), "No addresses returned from geocoder(recievers)", Toast.LENGTH_SHORT).show();
            checkInternetConnection();
            getCurrentLocationViaJSON(currentLatitude, currentLongitude);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
            Date date = new Date();
            d_time = dateFormat.format(date);
            long atTime = location.getTime();
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
            Log.d("timer", "" + mLastUpdateTime);
            Log.d("date and time:", dateFormat.format(date));

           /* if ((Utilities.strUserName != null) && (currentLatitude != null) && (currentLongitude != null) && (d_time != null) && (mLastUpdateTime != null) && (j_address1 != null) && (j_city != null) && (j_state != null) && (j_country != null)) {
                if ((previous_lattitude != null) && (previous_longitude != null) && (previous_lattitude.equals(currentLatitude)) && (previous_longitude.equals(currentLongitude))) {
                    Toast.makeText(context.getApplicationContext(), "you are in same location", Toast.LENGTH_SHORT).show();

                } else if (((previous_lattitude == null) && (previous_longitude == null)) || ((previous_lattitude != currentLatitude) && ((previous_longitude != currentLongitude))))
                {*/
            previous_lattitude = currentLatitude;
            previous_longitude = currentLongitude;
            Log.d("previous_latitude", "" + previous_lattitude);
            Log.d("previous_longitude", "" + previous_longitude);
            /*logindatabaseadapter = new LoginDataBaseAdapter(context.getApplicationContext());
            logindatabaseadapter = logindatabaseadapter.open();

            logindatabaseadapter.insertMapsEntry(Utilities.strUserName, currentLatitude, currentLongitude, d_time, mLastUpdateTime, j_address1, j_city, j_state, j_country);
            logindatabaseadapter.close();*/
            Log.d("d_time", "" + d_time);

            Log.d("mLastUpdateTime", "" + mLastUpdateTime);

            currentLatitude = null;
            currentLongitude = null;
            d_time = null;
            mLastUpdateTime = null;
            j_address1 = null;
            j_city = null;
            j_state = null;
            j_country = null;

            Toast.makeText(context.getApplicationContext(), "its time to work from Json ", Toast.LENGTH_SHORT).show();
               /* }
            }*/

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
        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||

                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            Toast.makeText(context.getApplicationContext(), " Connected ", Toast.LENGTH_LONG).show();
            return true;
        } else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            Toast.makeText(context.getApplicationContext(), "Not Connected,Internet required" + currentLatitude + currentLongitude, Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}
