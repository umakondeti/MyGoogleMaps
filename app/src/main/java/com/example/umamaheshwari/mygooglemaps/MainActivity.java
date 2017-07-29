package com.example.umamaheshwari.mygooglemaps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "MainActivity";
    CallbackManager callbackManager;
    Button login;
    Profile u_profile = null;
    // related to google plus
    private static final int ERROR_DIALOG_REQUEST_CODE = 11;
    private static final int SIGN_IN_FB_REQUEST_CODE = 10;
    private static final int SIGN_IN_GOOGLE_REQUEST_CODE = 9;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    // For communicating with Google APIs
    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    // contains all possible error codes for when a client fails to connect to
    // Google Play services
    private ConnectionResult mConnectionResult;
    private SignInButton signInButton;
    Singleton singleton;
    // getting fb details
    static String firstname, lastname, email, text, link, url,gender;
    public static Uri profilepic;
    static String profile_image;
    Boolean ishout;
    Profile profile;
    /*   getting googleplus details  */
    public String aboutMe, birthday;
    static String first_name;
    ProgressDialog progress;

/*           related to database               */

    LoginDataBaseAdapter loginDataBaseAdapter;
    Cursor client_details;
    TextView signin;
    SessionManager session;
    private String mUserId;
    private DatabaseReference mDatabaseReferences;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        printHashKey();
        setContentView(R.layout.activity_main);
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        session = new SessionManager(getApplicationContext());
        singleton =  Singleton.getInstance();
        Log.d("localo",getLocalClassName());
        //related   to google plus
/*
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
*/
        // Initializing google plus api client

        Utilities.mGoogleApiClient = buildGoogleAPIClient();
        signin = (TextView) findViewById(R.id.tv_sign_in);
        signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth = FirebaseAuth.getInstance();
                mFirebaseUser = mFirebaseAuth.getCurrentUser();
                // Initialize Database Reference
                mDatabaseReferences = FirebaseDatabase.getInstance().getReference();
                mDatabaseReferences.keepSynced(true);

                Firebase.setAndroidContext(getApplicationContext());
                ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
                session = new SessionManager(getApplicationContext());
                singleton = new Singleton();
                //  startActivity(new Intent(MainActivity.this, SignInMainActivity.class));

                if ((mFirebaseUser == null) && (!session.isLoggedIn())) {
                    startActivity(new Intent(MainActivity.this, SignInMainActivity.class));
                } else {
                    startActivity(new Intent(MainActivity.this, IntroPage.class));

                }
            }
        });
        //  related to facebook
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                if (AccessToken.getCurrentAccessToken() != null) {
                    RequestData();
                }
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });


        login = (Button) findViewById(R.id.login_button);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean status = (checkInternetConnection());
                if (status) {
                    facebook();
                } else
                    Toast.makeText(getApplicationContext(), "you dont have internet connection", Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.ngagroupinc.movers",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
    //   oncreate methd completed

    private void facebook() {
        progressbarshow();
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_friends", "email", "user_about_me"));

    }

    public void RequestData() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("fb_response", response.toString());
                JSONObject json = response.getJSONObject();
                try {
                    if (json != null) {
                        text = "<b>Name :</b> " + json.getString("name") + "<br><br><b>Email :</b> " + json.getString("email") + "<br><br><b>Profile link :</b> " + json.getString("link");
                        Utilities.strUserName = json.getString("name");
                        email = json.getString("email");
                        link = json.getString("link");
                        firstname=json.getString("first_name");
                        lastname=json.getString("last_name");
                        gender=json.getString("gender");
                        JSONObject pic = json.getJSONObject("picture");
                        JSONObject data = pic.getJSONObject("data");
                        ishout = data.getBoolean("is_silhouette");
                        url = data.getString("url");
                        Log.d("response", json.toString());

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                u_profile = Profile.getCurrentProfile();

                if ((MainActivity.email == null) && (u_profile != null)) {
                    Utilities.strUserName = u_profile.getFirstName().toString();
                    lastname = u_profile.getLastName().toString();
                    u_profile.getName();
                    profile_image = u_profile.getProfilePictureUri(100, 150).toString();
                }

                progress.dismiss();

                checkFbUserAlreadyRegOrNot();
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                intent.putExtra("first_name",firstname);
                intent.putExtra("last_name",lastname);
                intent.putExtra("name", Utilities.strUserName);
                intent.putExtra("email",email);
                intent.putExtra("pic",url);
                intent.putExtra("gender",gender);
                intent.putExtra("came_from",getLocalClassName());
                startActivity(intent);
            }
        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,first_name,last_name,link,email,gender,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }

    private void checkFbUserAlreadyRegOrNot() {
        if (mFirebaseUser != null) {

            mUserId = mFirebaseUser.getUid();
            Log.d("mapdetails_loginmuserId", mUserId);
        }
        mDatabaseReferences = FirebaseDatabase.getInstance().getReference();
        mDatabaseReferences.keepSynced(true);
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        mDatabaseReferences.child("my_maps_user").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("checkFbEmailResponse", dataSnapshot.toString());

                if (dataSnapshot.exists()) {
                    for(DataSnapshot issue : dataSnapshot.getChildren()) {

                          /*  if(issue.child("Date").getValue().toString().equals(db))
                            {
                                break;
                            }*/
                    /*    MyMapsUser userData = null;
                        userData = issue.getValue(MyMapsUser.class);
                        String date = userData.getDate();
                        Log.d("compare_date", "" + date + "-->" + db);

                        if (date != null)
                            if (date.equals(db)) {
                                String UserName = userData.getUsername();
                                String latitude = userData.getLatitude();
                                String longitude = userData.getLongitude();
                                String time = userData.getTime();
                                Log.d("retrieve_usersdata", time + " " + latitude + "" + longitude + "" + date + "" + UserName);
                            }*/
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


    }

/*
    private void createAccountWithMaps() {
        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
                            // Get Firebase auth instance
                            mFirebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseUser = mFirebaseAuth.getCurrentUser();
                            // Initialize Database Reference
                            mDatabaseReferences = FirebaseDatabase.getInstance().getReference();
                            Firebase.setAndroidContext(getApplicationContext());
                            ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
                            Log.d("userProfilePicUrl", link);
                            MyMapsUser user = new MyMapsUser();
                            user.setFirstName(firstname);
                            user.setLastName(lastname);
                            user.setEmailId(email);
                            user.setUserProfilePic(link);
                            if (mFirebaseUser != null) {

                                mUserId = mFirebaseUser.getUid();
                                Log.d("after_loginmuserId", mUserId);
                            }


                            mDatabaseReferences.child("my_maps_user").child(mUserId).push().setValue(user).
                                    addOnCompleteListener(MainActivity.this,
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("success", "added fields");
  singleton.setLoginUserDisplayName(firstname + "" + lastname);
                                                                    singleton.setLoginUserEmailId(email);
                                                                    singleton.setLoginImage(link);

                                                        Toast.makeText(MainActivity.this, "successfully added fields." + task.getException(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "not added fields." + task.getException(), Toast.LENGTH_SHORT).show();

                                                        Log.d("failure", "" + task.getException());
                                                    }

                                                }
                                            });
 Intent intent = new Intent(MainActivity.this, SignInMainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_GOOGLE_REQUEST_CODE) {
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }

            mIntentInProgress = false;

            if (!Utilities.mGoogleApiClient.isConnecting()) {
                Utilities.mGoogleApiClient.connect();
            }
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);


        }


    }


    @Override
    public void onConnected(Bundle bundle) {
        if (mSignInClicked) {
            mSignInClicked = false;
            Log.d("connected", "int");
            processUserInfoAndUpdateUI();
            Toast.makeText(getApplicationContext(), "Signed In Successfully", Toast.LENGTH_LONG).show();
            Plus.AccountApi.clearDefaultAccount(Utilities.mGoogleApiClient);
            Utilities.mGoogleApiClient.disconnect();
            Utilities.mGoogleApiClient.connect();
            progress.dismiss();
            Intent in = new Intent(MainActivity.this, IntroPage.class);
            startActivity(in);
        }
       /* loginDataBaseAdapter = loginDataBaseAdapter.open();
        loginDataBaseAdapter.insertGoogleEntry(userName, userEmail, aboutMe, null, null);
        loginDataBaseAdapter.close();*/
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Utilities.mGoogleApiClient.connect();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.sign_in_button) {
            boolean status = (checkInternetConnection());
            if (status) {
                processSignIn();
            } else
                Toast.makeText(getApplicationContext(), "you dont have internet connection", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
                    ERROR_DIALOG_REQUEST_CODE).show();
            return;
        }
        if (!mIntentInProgress) {
            mConnectionResult = result;

            if (mSignInClicked) {
                processSignInError();
            }
        }


    }

    // related to google plus
    private GoogleApiClient buildGoogleAPIClient() {
        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN).build();
    }

    protected void onStart() {
        super.onStart();
        // make sure to initiate connection
        Utilities.mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // disconnect api if it is connected
        if (Utilities.mGoogleApiClient.isConnected())
            Utilities.mGoogleApiClient.disconnect();
    }

    /**
     * API to handler sign in of user If error occurs while connecting process
     * it in processSignInError() api
     */
    private void processSignIn() {
        progressbarshow();
        Log.d("msg", "process sign in");
        if (!Utilities.mGoogleApiClient.isConnecting()) {
            processSignInError();
            mSignInClicked = true;
        }

    }


    /**
     * API to process sign in error Handle error based on ConnectionResult
     */
    private void processSignInError() {
        if (mConnectionResult != null && mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this,
                        SIGN_IN_GOOGLE_REQUEST_CODE);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                Utilities.mGoogleApiClient.connect();
            }
        }
    }

    private void processUserInfoAndUpdateUI() {
        Person signedInUser = Plus.PeopleApi.getCurrentPerson(Utilities.mGoogleApiClient);
        if (signedInUser != null) {

            if (signedInUser.hasDisplayName()) {
                Utilities.strUserName = signedInUser.getDisplayName();
            }

            if (signedInUser.hasTagline()) {
                String tagLine = signedInUser.getTagline();

            }

            if (signedInUser.hasAboutMe()) {
                aboutMe = signedInUser.getAboutMe();

            }

            if (signedInUser.hasBirthday()) {
                birthday = signedInUser.getBirthday();

            }

            if (signedInUser.hasCurrentLocation()) {
                String userLocation = signedInUser.getCurrentLocation();
            }

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Utilities.strUserEmail = Plus.AccountApi.getAccountName(Utilities.mGoogleApiClient);

            if (signedInUser.hasImage()) {
                // default size is 50x50 in pixels.changes it to desired size
                int profilePicRequestSize = 250;
                Utilities.strProfielUrl = signedInUser.getImage().getUrl();

            }

        }
    }

    private class UpdateProfilePicTask extends AsyncTask<String, Void, Bitmap> {

        WeakReference profileView;

        public UpdateProfilePicTask(ImageView img) {
            profileView = new WeakReference(img);
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap profilePic = null;
            try {
                URL downloadURL = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) downloadURL
                        .openConnection();
                int responseCode = conn.getResponseCode();
                if (responseCode != 200)
                    throw new Exception("Error in connection");
                InputStream is = conn.getInputStream();
                profilePic = BitmapFactory.decodeStream(is);

            } catch (Exception e) {
                e.printStackTrace();


            }
            return profilePic;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // TODO Auto-generated method stub
            if (result != null && profileView != null) {
                ImageView view = (ImageView) profileView.get();
                if (view != null)
                    view.setImageBitmap(result);
            }
        }

    }

    private void progressbarshow() {

        progress = new ProgressDialog(MainActivity.this);
        progress.setMessage("Please wait...");
        progress.setIndeterminate(true);
        progress.setCancelable(true);
        progress.show();

    }

    private boolean checkInternetConnection() {

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
                return true;
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                Toast.makeText(this, " Connected ", Toast.LENGTH_LONG).show();
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
}



