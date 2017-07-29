package com.example.umamaheshwari.mygooglemaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.firebase.client.Firebase;
import com.firebase.client.utilities.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SignInMainActivity extends Activity {
    Button btnSignIn;
    EditText editTextUserName, editTextPassword;
    LoginDataBaseAdapter loginDataBaseAdapter;
    Button loginButton;
    Button signupButton;
    Cursor login_details;
    String storedPassword;
    SessionManager session;
    Singleton singleton;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference mDatabaseReference;
    private String mUserId;
    Firebase ref;
    Utilities utilities;
    boolean gotUserInfo = false;
    ProgressDialog signInDialog;
    public static String userName, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.sign_in);
        editTextUserName = (EditText) findViewById(R.id.et_username);
        editTextPassword = (EditText) findViewById(R.id.et_password_sign_in);
        btnSignIn = (Button) findViewById(R.id.btn_sign_in);
        loginButton = (Button) findViewById(R.id.login_button);
        signupButton = (Button) findViewById(R.id.btn_sign_ups);
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize Database Reference
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        utilities= new Utilities(this);
        // create a instance of SQLite Database
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        session = new SessionManager(getApplicationContext());
        singleton = Singleton.getInstance();
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra("came_from", getLocalClassName());

                startActivity(intent);
            }
        });

    }

    public void setAccountInfo() {

        Log.d("retrieve_usersigin_data", "clicked");

        auth = FirebaseAuth.getInstance();
        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();
        // Initialize Database Reference
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        if (user != null) {

            mUserId = user.getUid();
            Log.d("mtestuserId", mUserId);
        }
        if (!gotUserInfo) {
            getUserInfoFromFirebase();
        }

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

    // Methods to handleClick Event of Sign In Button
    public void signIn(View V) {
        // Set On ClickListener
        // get The User name and Password
        userName = editTextUserName.getText().toString().trim();
        password = editTextPassword.getText().toString().trim();
        if (userName.length() <= 0) {
            Toast.makeText(SignInMainActivity.this, "email should not be empty", Toast.LENGTH_SHORT).show();
        } else if (password.length() <= 0) {
            Toast.makeText(SignInMainActivity.this, "password should not be empty", Toast.LENGTH_SHORT).show();

        } else {
            signInDialog=Utilities.showProgressDialog(SignInMainActivity.this,"Signing In....");
            //utilities.showProgressDialog();

            //authenticate user
            auth.signInWithEmailAndPassword(userName, password)
                    .addOnCompleteListener(SignInMainActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            // progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                signInDialog.dismiss();
                                // there was an error
                               // utilities.cancelProgressDialog();
                                AlertDialog.Builder builder = new AlertDialog.Builder(SignInMainActivity.this);
                                builder.setMessage(task.getException().getMessage())
                                        .setTitle(R.string.login_error_title)
                                        .setPositiveButton(android.R.string.ok, null);
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                Log.d("signin_error", "" + task.getException());

                            } else if (task.isSuccessful()) {

                                setAccountInfo();

                            }
                        }
                    });
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close The Database
        loginDataBaseAdapter.close();
    }

    public void getUserInfoFromFirebase() {
        mDatabaseReference.child("my_maps_user").child(mUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Log.d("my_app_user_child", "" + dataSnapshot);
                    Log.d("y so",dataSnapshot.getKey()+""+dataSnapshot.getChildrenCount());
                    gotUserInfo = true;
                    MyMapsUser userData = postSnapshot.getValue(MyMapsUser.class);
                   // String usernames = userData.getDisplayName();
                    String usernames = dataSnapshot.getKey();
                    String userProfilePic = userData.getUserProfilePic();
                    String emailId = userData.getEmailId() ;
                    String gender=userData.getGender();
                    String phone_number=userData.getPhoneNumber();
                    Log.d("retrieve_sign_usernam(c", "" + usernames);
                    Log.d("ret_sign_userProfile(c)", "" + userProfilePic);
                    Log.d("retrieve_emailId(c)", "" + emailId);
                    if (usernames != null) {
                        singleton.setLoginUserDisplayName("" + usernames);
                        singleton.setLoginUserEmailId("" + emailId);
                        singleton.setLoginImage("" + userProfilePic);
                        singleton.setFirstName(usernames);
                        Utilities.strUserName = usernames;
                        Log.d("userName(c)", "" + userName);
                        Log.d("Password(c)", "" + password);
                        Log.d("DisplayName(c)", " " + singleton.getLoginUserDisplayName());
                        Log.d("LoginUserEmailId(c)", " " + singleton.getLoginUserEmailId());
                        Log.d("LoginImage(c)", " " + singleton.getLoginImage());
                        session.createLoginSession(usernames, emailId, userProfilePic,gender,phone_number);
                        signInDialog.dismiss();
                        //utilities.cancelProgressDialog();
                        Toast.makeText(SignInMainActivity.this, "Congrats: Login Successful", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), IntroPage.class);
                        startActivity(intent);
                    }else
                    {
                        auth.signOut();
                        session.logoutUser();
                        singleton.setLoginUserDisplayName(null);
                        singleton.setLoginUserEmailId(null);
                        singleton.setLoginActiveStatus(null);
                        Toast.makeText(getApplicationContext(),"please Login again",Toast.LENGTH_LONG).show();
                    }
                    break;
           }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d("onChildChanged:", "" + dataSnapshot.getKey());

                // A comment has changed, use the key to determine if we are displaying this
                // comment and if so displayed the changed comment.
                MyMapsUser newComment = dataSnapshot.getValue(MyMapsUser.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d("onChildMoved:", "" + dataSnapshot.getKey());

                // A comment has changed position, use the key to determine if we are
                // displaying this comment and if so move it.
                MyMapsUser movedComment = dataSnapshot.getValue(MyMapsUser.class);
                String commentKey = dataSnapshot.getKey();

                // ...
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                signInDialog.dismiss();
                Log.d("SignInMainActivity", "postComments:onCancelled", databaseError.toException());
                Toast.makeText(getApplicationContext(), "Failed to retrieve the data",
                        Toast.LENGTH_SHORT).show();
            }
        });


    }
}