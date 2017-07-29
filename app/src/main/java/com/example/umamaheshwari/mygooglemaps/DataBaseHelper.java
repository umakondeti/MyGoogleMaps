package com.example.umamaheshwari.mygooglemaps;

/**
 * Created by UmaMaheshwari on 1/20/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper
{
    public static final String DATABASE_NAME = "MyDBName.db";
    public static final String CONTACTS_TABLE_NAME = "PHONE_BOOKS";
    public static final String CONTACTS_COLUMN_ID = "id";
    public static final String CONTACTS_COLUMN_NAME = "name";
    public static final String CONTACTS_COLUMN_EMAIL = "email";
    public static final String CONTACTS_COLUMN_STREET = "street";
    public static final String CONTACTS_COLUMN_CITY = "place";
    public static final String CONTACTS_COLUMN_PHONE = "phone";
    private HashMap hp;
    public DataBaseHelper(Context context, String name,CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }
    public DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    // Called when no database exists in disk and the helper class needs
    // to create a new one.
    @Override
    public void onCreate(SQLiteDatabase _db)
    {
        _db.execSQL("CREATE TABLE IF NOT EXISTS PHONE_BOOKS(name VARCHAR,phonenumber VARCHAR);");

        _db.execSQL(LoginDataBaseAdapter.DATABASE_CREATE);
        Log.d("data base", "DATABASE_CREATE" + "created");
        _db.execSQL(LoginDataBaseAdapter.DATABASE_GOOGLEPLUS);
        Log.d("data base", "DATABASE_GOOGLEPLUS" + "created");

        _db.execSQL(LoginDataBaseAdapter.DATABASE_TABLE);
        Log.d("data base", "DATABASE_TABLE" + "created");

        _db.execSQL(LoginDataBaseAdapter.DATABASE_CONTACT);
        Log.d("data base", "DATABASE_CONTACT" + "created");

        _db.execSQL(LoginDataBaseAdapter.DATABASE_MAIN);
        Log.d("data base", "DATABASE_MAIN" + "created");
        _db.execSQL(LoginDataBaseAdapter.DATABASE_INSERT);
        Log.d("data base", "DATABASE_ALL_CONTACTS" + "created");
        _db.execSQL(LoginDataBaseAdapter.DATABASE_REGISTERED_USERS);
        Log.d("data base", "DATABASE_REGISTERED_USERS" + "created");

    }

    // Called when there is a database version mismatch meaning that the version
    // of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion)
    {
        // Log the version upgrade.
        Log.w("TaskDBAdapter", "Upgrading from version " +_oldVersion + " to " +_newVersion + ", which will destroy all old data");

        // Upgrade the existing database to conform to the new version. Multiple
        // previous versions can be handled by comparing _oldVersion and _newVersion
        // values.
        // The simplest case is to drop the old table and create a new one.
        _db.execSQL("DROP TABLE IF EXISTS " + "TEMPLATE");
        // Create a new one.
        onCreate(_db);
    }
    public boolean insertContact(String name, String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phonenumber", phone);
        db.insert("PHONE_BOOKS", null, contentValues);
        return true;
    }

    public Cursor getData(String name){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from PHONE_BOOKS where name="+name+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);

        db.update("PHONE_BOOKS", contentValues, "name = ? ", new String[] {new String(name)} );
        return true;
    }

    public Integer deleteContact ()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("PHONE_BOOKS",
                null,
                null);

    }

    public ArrayList<String> getAllCotacts()
    {
        ArrayList<String> array_list = new ArrayList<String>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from PHONE_BOOKS", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(CONTACTS_COLUMN_NAME)));
            res.moveToNext();
        }
        return array_list;
    }

}
