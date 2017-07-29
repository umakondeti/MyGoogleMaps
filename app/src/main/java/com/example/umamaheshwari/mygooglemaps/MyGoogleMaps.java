package com.example.umamaheshwari.mygooglemaps;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import com.google.firebase.database.FirebaseDatabase;
/**
 * Created by user1 on 24-Jun-17.
 */

public class MyGoogleMaps extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
       // MultiDex.install(this);

    /*    Picasso.Builder builder = new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built = builder.build();
        built.setIndicatorsEnabled(false);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);*/


    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
