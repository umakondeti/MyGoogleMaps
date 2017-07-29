package com.example.umamaheshwari.mygooglemaps;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUpActivity extends Activity {
    private static final int RESULT_LOAD_IMAGE = 1;
    EditText editTextEmail, editTextDisplayName, editTextLastName, editTextPassword, editTextConfirmPassword, editTextGender, editTextPhoneNumber;
    Button btnCreateAccount;
    String DisplayName, lastName, EmailId, password, confirmPassword, Gender, phonenumber;
    static CircularImageView iv_pic;
    static String picturePath;
    LinearLayout llv_signup;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUsers;
    private DatabaseReference mDatabaseReferences;
    private String mUserId;
    SessionManager session;
    Singleton singleton;
    Firebase ref;
    ProgressDialog myDialog;
    LoginDataBaseAdapter loginDataBaseAdapter;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String passwordPattern = "^[A-Za-z0-9]{4,20}$";

    String namePattern = "[a-zA-z]*";
    static byte[] blob;
    private final int REQUEST_PERMISSION_PHONE_STATE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 96;
    boolean allow_status = false, deny_status = false, allowAccessImages = false;
    Bitmap imageBitmap;
    Bundle bundle;
    Utilities utilities;
    String firstname, lastname, username, pic, Email, gender;
    String userProfilePicUrl;
    String came_from;
    boolean cameFromFb = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        // Initialize FirebaseAuth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUsers = mFirebaseAuth.getCurrentUser();
        // Initialize Database Reference
        mDatabaseReferences = FirebaseDatabase.getInstance().getReference();
        Firebase.setAndroidContext(getApplicationContext());
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        session = new SessionManager(getApplicationContext());
        singleton = Singleton.getInstance();
        // get Instance  of Database Adapter
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        iv_pic = (CircularImageView) findViewById(R.id.iv_pic_circle);
        llv_signup = (LinearLayout) findViewById(R.id.llv_signup);
        utilities = new Utilities(this);
        iv_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("onclick_allow_status", " " + allow_status);
                if (Build.VERSION.SDK_INT >= 23) {
                    checkAccessMediaPermission();

                } else {
                    Intent i = new Intent(
                            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
            }
        });

        // Get References of Views
        editTextDisplayName = (EditText) findViewById(R.id.et_display_name);
        editTextEmail = (EditText) findViewById(R.id.et_email);
        editTextPassword = (EditText) findViewById(R.id.et_password);
        editTextConfirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        editTextGender = (EditText) findViewById(R.id.et_gender);
        editTextPhoneNumber = (EditText) findViewById(R.id.et_phone_number);
        //Get the Values

        btnCreateAccount = (Button) findViewById(R.id.btn_sign_up);
        bundle = getIntent().getExtras();
        came_from = bundle.getString("came_from", "");
        if (came_from.equals("MainActivity")) {
            firstname = bundle.getString("first_name", "");
            lastname = bundle.getString("last_name", "");
            username = bundle.getString("name", "");
            Email = bundle.getString("email", "");
            pic = bundle.getString("pic", "");
            Log.d("retrieve_pic", "" + pic);
            gender = bundle.getString("gender", gender);
            populateFbDetails();
        }
    }

    public void signUp(View V) {

        Log.d("clicked", "signup got clicked");
        // TODO Auto-generated method stub

        DisplayName = editTextDisplayName.getText().toString();
        EmailId = editTextEmail.getText().toString();
        password = editTextPassword.getText().toString();
        confirmPassword = editTextConfirmPassword.getText().toString();
        Gender = editTextGender.getText().toString();
        phonenumber = editTextPhoneNumber.getText().toString();

              /*  loginDataBaseAdapter = loginDataBaseAdapter.open();
                String uname = loginDataBaseAdapter.getSinlgeEntry(EmailId);
                loginDataBaseAdapter.close();*/

        if ((DisplayName.equals("")) || (Gender.equals("")) || (EmailId.equals("")) || (password.equals("")) || (confirmPassword.equals(""))||(phonenumber.equals(""))) {
            if (DisplayName.equals("")) {
                Toast.makeText(getApplicationContext(), "Display Name is empty",
                        Toast.LENGTH_LONG).show();
                editTextDisplayName.setFocusable(true);
            } else if (EmailId.equals("")) {
                Toast.makeText(getApplicationContext(), "Email is empty",
                        Toast.LENGTH_LONG).show();
                editTextEmail.setFocusable(true);
            } else if (Gender.equals("")) {
                Toast.makeText(getApplicationContext(), "Gender is empty",
                        Toast.LENGTH_LONG).show();
                editTextGender.setFocusable(true);
            }
            else if (phonenumber.equals("")) {
                Toast.makeText(getApplicationContext(), "Phone Number is empty",
                        Toast.LENGTH_LONG).show();
                editTextPhoneNumber.setFocusable(true);
            }else if (password.equals("")) {
                Toast.makeText(getApplicationContext(), "Password is empty",
                        Toast.LENGTH_LONG).show();
                editTextPassword.setFocusable(true);
            }
            if (confirmPassword.equals("")) {
                Toast.makeText(getApplicationContext(), "Confirm password is empty",
                        Toast.LENGTH_LONG).show();
                editTextConfirmPassword.setFocusable(true);
            }
        } else if (!DisplayName.matches(namePattern)) {
            Toast.makeText(getApplicationContext(), "Enter valid Display Name",
                    Toast.LENGTH_LONG).show();
            if (!editTextDisplayName.hasFocus()) {
                editTextDisplayName.setFocusable(true);
            }
        } else if (!EmailId.matches(emailPattern)) {
            Toast.makeText(getApplicationContext(), "Enter valid Email",
                    Toast.LENGTH_LONG).show();
            editTextEmail.setFocusable(true);
        } /*else if (EmailId.equals(uname)) {
                    Toast.makeText(getApplicationContext(), "EMAIL ALREADY EXITS",
                            Toast.LENGTH_LONG).show();
                }*/ else if (!password.matches(passwordPattern)) {
            Toast.makeText(getApplicationContext(), "Enter valid Password",
                    Toast.LENGTH_LONG).show();
            editTextPassword.setFocusable(true);
        } else if (!(password.length() >= 6) && (password.length() <= 10)) {
            Toast.makeText(getApplicationContext(), "Password should have atleast 6 Characters",
                    Toast.LENGTH_LONG).show();
            editTextPassword.setFocusable(true);
        } else if (!confirmPassword.equals(password)) {
            Toast.makeText(getApplicationContext(), "Password does not match",
                    Toast.LENGTH_LONG).show();
            editTextConfirmPassword.setFocusable(true);
        } /*else if (picturePath == null) {
                    Toast.makeText(getApplicationContext(), "PLEASE SELECT YOUR PICTURE",
                            Toast.LENGTH_LONG).show();
                    iv_pic.setFocusable(true);
                }*/ else {
            myDialog = Utilities.showProgressDialog(SignUpActivity.this, "creating your account......");


            //loginDataBaseAdapter = loginDataBaseAdapter.open();
                   /* ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    if (picturePath != null) {
                        Bitmap bit = (BitmapFactory.decodeFile(picturePath));
                        bit.compress(Bitmap.CompressFormat.PNG, 0, stream);
                        blob = stream.toByteArray();
                    }*/
            //loginDataBaseAdapter.insertEntry(picturePath, firstName, lastName, EmailId, password, confirmPassword);
            //loginDataBaseAdapter.close();
            Utilities.strUserName = DisplayName;
            if (cameFromFb) {
                userProfilePicUrl = pic;
            }
            signUpWithUserData(DisplayName,EmailId, userProfilePicUrl, Gender,phonenumber,password);

        }
    }


    private void signUpWithUserData(final String DisplayName, final String emailId, final String userProfilePic ,final String Gender, final String phone_no, String pwd) {
        mFirebaseAuth.createUserWithEmailAndPassword(emailId, pwd)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_LONG).show();
                            // Get Firebase auth instance
                            mFirebaseAuth = FirebaseAuth.getInstance();
                            mFirebaseUsers = mFirebaseAuth.getCurrentUser();
                            String uid = mFirebaseAuth.getCurrentUser().getUid();
                            Log.d(" Uid", mFirebaseUsers.getUid());
                            // Initialize Database Reference
                            mDatabaseReferences = FirebaseDatabase.getInstance().getReference();
                            Firebase.setAndroidContext(getApplicationContext());
                            ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
                            Log.d("userProfilePicUrl", "" + userProfilePic);
                            MyMapsUser user = new MyMapsUser(DisplayName,emailId,userProfilePicUrl,Gender,phone_no);
                            user.setDisplayName(DisplayName);
                            /*user.setFirstName(firstName);
                            user.setLastName(lastName);
                            user.setEmailId(emailId);
                            user.setUserProfilePic(userProfilePic);*/
                            String email = emailId.replace('.', ',');

                            mDatabaseReferences.child("my_maps_user").child("emailToUid").updateChildren(user.setEmailToUId(email, uid, DisplayName,picturePath,phone_no));
                            if (mFirebaseUsers != null) {

                                mUserId = mFirebaseUsers.getUid();
                                Log.d("after_loginmuserId", mUserId);
                            }


                            mDatabaseReferences.child("my_maps_user").child(mFirebaseAuth.getCurrentUser().getUid()).child(DisplayName).child("UserProfileInfo").setValue(user.toUserDetails()).
                                    addOnCompleteListener(SignUpActivity.this,
                                            new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d("success", "added fields");
                                                        myDialog.dismiss();
                                                        // utilities.cancelProgressDialog();
                                                        Intent intent = new Intent(SignUpActivity.this, SignInMainActivity.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                                   /* singleton.setLoginUserDisplayName(firstName + "" + lastName);
                                                                    singleton.setLoginUserEmailId(EmailId);
                                                                    singleton.setLoginImage(userProfilePicUrl);*/
                                                        Toast.makeText(SignUpActivity.this, "successfully added fields." + task.getException(), Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        myDialog.dismiss();
                                                        Toast.makeText(SignUpActivity.this, "not added fields." + task.getException(), Toast.LENGTH_SHORT).show();

                                                        Log.d("failure", "" + task.getException());
                                                    }

                                                }
                                            });

                        } else {
                            myDialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
                            builder.setMessage(task.getException().getMessage())
                                    .setTitle(R.string.login_error_title)
                                    .setPositiveButton(android.R.string.ok, null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        }
                    }
                });
    }

    private void populateFbDetails() {
        editTextDisplayName.setText(firstname + " "+lastname);
        //editTextLastName.setText(lastname);
        editTextEmail.setText(Email);
        editTextGender.setText(gender);
        editTextPassword.setText("");
        editTextConfirmPassword.setText("");
        editTextPhoneNumber.setText("");
        if (pic != null) {
            // new FbNumberImage().
            //       execute();
            Log.d("profile_img", "" + pic);
            Picasso.with(SignUpActivity.this).load(pic).resize(100, 150).into(iv_pic);
            cameFromFb = true;
        }
    }

    private void checkAccessMediaPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(SignUpActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(SignUpActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar.make(llv_signup, R.string.alert_allow_gallery, Snackbar.LENGTH_INDEFINITE)
                        .setAction(android.R.string.ok, new View.OnClickListener() {
                            @Override
                            @TargetApi(Build.VERSION_CODES.M)
                            public void onClick(View v) {

                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(SignUpActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                       /*     Intent i = new Intent(
                                                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                            startActivityForResult(i, RESULT_LOAD_IMAGE);
                                            allow_status = true;*/
                                Log.d("permission_one", "requested" + allow_status);
                            }
                        });

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(SignUpActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                         /*   Intent i = new Intent(
                                    Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, RESULT_LOAD_IMAGE);
                            allow_status = true;*/

                Log.d("permission_second_one", "requested" + allow_status);
                // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            ActivityCompat.requestPermissions(SignUpActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
               /*         Intent i = new Intent(
                                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                        allow_status = true;*/

            Log.d("permiision_last", "requested" + "" + allow_status);
        }
    }

    public String encodeBitmapAndSaveToFirebase(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        return imageEncoded;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //region imageview display
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Bundle extras = data.getExtras();

            Uri selectedImage = data.getData();
            Log.d("imagpath", "" + selectedImage);
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            Log.d("path", "" + picturePath);
            cursor.close();
            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
            iv_pic.setImageBitmap(imageBitmap);
            userProfilePicUrl = encodeBitmapAndSaveToFirebase(imageBitmap);
            Log.d("yanto(pic)"," " +userProfilePicUrl);
            //userProfilePicUrl=picturePath;

        }
        //endregion
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(
                                Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    } else {
//Displaying another toast if permission is not granted
                        Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show();
                    }
                }
            }


            return;

        }
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();

    }

    class FbNumberImage extends AsyncTask<Bitmap, Bitmap, Bitmap> {

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            Bitmap bitmap = null;
            try {
                URL url2 = new URL(pic);
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
            iv_pic.setImageBitmap(bitmap);
        }
    }


}