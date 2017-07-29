package com.example.umamaheshwari.mygooglemaps;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.widget.Toast;

import com.example.umamaheshwari.mygooglemaps.adapter.Buddies;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by UmaMaheshwari on 1/21/2016.
 */
public class Utilities {

    public static String strProfielUrl;
    public static GoogleApiClient mGoogleApiClient;
    public static double prevlatitude,prevlongitude;
    public static double currlatitude,currlongitude;
    public static String strUserName,strUserEmail,userPic;
    public  static int prevValueCounts=0;
    public  static int postValueCounts=0;
    public static ArrayList<Contacts> registeredUsersArrayList = new ArrayList<>();
    public static ArrayList<Buddies> registeredUsersBuddiesList = new ArrayList<>();
    public static ArrayList<Contacts> selectedUsersBuddiesList = new ArrayList<>();
    public  ArrayList<String> registeredSampleUsersBuddiesList = new ArrayList<>();

    private ProgressDialog progress;
    Context context;
    public Utilities(Context context) {
        this.context = context;

    }
    public static ProgressDialog  showProgressDialog(Context ctx,String msg) {
         ProgressDialog progress  =   new ProgressDialog(ctx);
        progress.setMessage(msg);
        progress.setIndeterminate(true);
        progress.setProgress(0);
        progress.setCanceledOnTouchOutside(false);
        progress.show();
        return progress ;
    }
    public void cancelProgressDialog() {

        // if ((context instanceof MainActivity) || (context instanceof DisplayItemsActivity) || (context instanceof SubCategoryActivity) || (context instanceof SingleItemActivity))
        progress.dismiss();
    }

    public String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }
    public void DisplayToast(String toast) {

        Toast.makeText(context.getApplicationContext(), toast, Toast.LENGTH_LONG).show();
    }
    public void showAlert(String alertMessage) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setMessage(alertMessage);
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }
}
