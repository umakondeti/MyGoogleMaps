/*
package com.example.umamaheshwari.mygooglemaps;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

class FeedRunnerDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Login.db";
    public static final String Table_name = "SIGNUP";
    public static final String COLUMN_fname = "FNAME";
    public static final String COLUMN_lname = "LNAME";
    public static final String COLUMN_EMAIL = "EMAIL";
    public static final String TABLE_CONTACTS = "CONTATCS";
    public static final String CONTACTS_COLUMN_NAME = "NAME";
    public static final String CONTACTS_COLUMN_AGE = "AGE";
    public static final String CONTACTS_COLUMN_PHONENUMBER = "PHONENUMBER";
    public static final String CONTACTS_COLUMN_EMAIL = "EMAIL";
    public static final String CONTACTS_COLUMN_PROFESSION = "PROFESSION";
    public static final String CONTACTS_COLUMN_BLOODGROUP = "BLOODGROUP";
    public static final String CONTACTS_COLUMN_WORKING = "WORKING";
    public static final String CONTACTS_COLUMN_CITY = "CITY";
    public static final String CONTACTS_COLUMN_STATE = "STATE";
    public static final String CONTACTS_COLUMN_COUNTRY = "COUNTRY";
    public static final String CONTACTS_COLUMN_FAVOURIT = "FAVOURIT";
    public static final String CONTACTS_COLUMN_IMGPATH = "PATH";
    public static final String COLUMN_PASSWORD = "PASSWORD";
    public static final String TEXT_TYPE = "VARCHAR(255)";
    public static final String NUMBER_TYPE = "INT";

    public FeedRunnerDBHelper(Context context) {
//            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        super(context, Table_name, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createSignUpTable = "CREATE TABLE " + Table_name + " ( " + COLUMN_fname + " " + TEXT_TYPE + "," + COLUMN_lname + " " + TEXT_TYPE + "," + COLUMN_EMAIL + " " + TEXT_TYPE + "," + COLUMN_PASSWORD + " " + TEXT_TYPE + ");";
        String createContactTable = "CREATE TABLE "+TABLE_CONTACTS+" ( "+CONTACTS_COLUMN_NAME +" "+TEXT_TYPE+","+CONTACTS_COLUMN_AGE+" "+NUMBER_TYPE+","
                +CONTACTS_COLUMN_PHONENUMBER+" "+NUMBER_TYPE+","+CONTACTS_COLUMN_EMAIL+" "+TEXT_TYPE+","+CONTACTS_COLUMN_PROFESSION+" "+TEXT_TYPE+","
                +CONTACTS_COLUMN_BLOODGROUP+" "+TEXT_TYPE+","+CONTACTS_COLUMN_WORKING+" "+TEXT_TYPE+","+CONTACTS_COLUMN_CITY+" "+TEXT_TYPE+","
                +CONTACTS_COLUMN_STATE+" "+TEXT_TYPE+","+CONTACTS_COLUMN_COUNTRY+" "+TEXT_TYPE+","+CONTACTS_COLUMN_FAVOURIT+" "+TEXT_TYPE+","+CONTACTS_COLUMN_IMGPATH+" "+TEXT_TYPE+");";
        db.execSQL(createSignUpTable);
        db.execSQL(createContactTable);
        Log.d("he","he");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FeedRunnerDBHelper.Table_name);
        db.execSQL("DROP TABLE IF EXISTS " + FeedRunnerDBHelper.TABLE_CONTACTS);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
    }

    public void selectEmail() {
        String login_email = "SELECT EMAIL FROM " + FeedRunnerDBHelper.Table_name + " WHERE " + COLUMN_EMAIL + "='" + MainActivity.edEmail.getText().toString() + "';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(login_email, null);
        if(c.getCount()==1)
        {
            Log.d("oneRecord","one");
        }
        else
        {
            Log.d("oneRecord","more than one");
        }


    }
}*/
