package com.example.umamaheshwari.mygooglemaps;

/**
 * Created by UmaMaheshwari on 1/20/2016.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.util.Log;

import java.sql.Blob;
import java.util.List;

public class LoginDataBaseAdapter

{

    static final String DATABASE_NAME = "login.db";

    static final int DATABASE_VERSION = 1;

    public static final int NAME_COLUMN = 1;

    public int rowId;
    /** Field 1 of the table locations, which is the primary key *//*
    public static final String FIELD_ROW_ID = "_id";

    *//** Field 2 of the table locations, stores the latitude *//*
    public static final String FIELD_LAT = "lat";

    *//** Field 3 of the table locations, stores the longitude*//*
    public static final String FIELD_LNG = "lng";

    *//** Field 4 of the table locations, stores the zoom level of map*//*
    public static final String FIELD_ZOOM = "zom";
*/
    /** A constant, stores the the table name */

    // TODO: Create public field for each column in your table.

    static final String DATABASE_CREATE = "create table "+"FBLOGIN"+

            "( " +"ID"+" integer primary key autoincrement,"+ "FIRSTNAME  text," +

            "USERNAME text,LINK text,PASSWORD text,CONFIRMPASSWORD text); ";

    static final String DATABASE_GOOGLEPLUS = "create table "+"GOOGLELOGIN"+

            "( " +"ID"+" integer primary key autoincrement,"+ "FIRSTNAME  text," +

            "USERNAME text,ABOUTME text,PASSWORD text,CONFIRMPASSWORD text); ";

    static final String DATABASE_TABLE = "create table "+"CURRENT_LOCATIONS"+

            "( " +"ID"+" integer primary key autoincrement,"+"UserName  text," + "lat  double," +

            "lon double,RECORDED_DATE String,time String,address text,City text, state text,country text); ";

    static final String DATABASE_CONTACT = "create table "+"CONTACTS_LIST"+

            "( " +"ID"+" integer primary key autoincrement,"+ "contact_name  text," +

            "contact_phonenumber text); ";

    static final String DATABASE_MAIN = "create table "+"LOGIN_MAIN"+

            "( " +"ID"+" integer primary key autoincrement,"+ "IMAGE  text NOT NULL," + "FIRSTNAME  text," + "LASTNAME  text," +

            "USERNAME text,PASSWORD text,CONFIRMPASSWORD text); ";

    static final String DATABASE_INSERT = "create table "+"PHONE_BOOK"+

            "( " +"ID"+" integer primary key autoincrement,"+ "NAME  text," +

            "PHONENUMBER text); ";
    static final String DATABASE_REGISTERED_USERS = "create table "+"REGISTERED_USERS"+

            "( " + "DISPLAYNAME  text primary key," +

            "PHONENUMBER text); ";

  /* static final String DATABASE_TABLE = "create table "+"CURRENT_LOCATIONS"+

           "( " +"ID"+" integer primary key  autoincrement,"+ "lat  double NOTNULL," + "lon  double NOT NULL," +

           "RECORDED_DATE String NOTNULL); ";*/


    // Variable to hold the database instance
    public  SQLiteDatabase db;

    // Context of the application using the database.
    private Context context;

    // Database open/upgrade helper
    private DataBaseHelper dbHelper;

    public  LoginDataBaseAdapter(Context _context)
    {

        context = _context;

        dbHelper = new DataBaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public  LoginDataBaseAdapter open() throws SQLException

    {
        db = dbHelper.getWritableDatabase();

        return this;
    }

    public void close()
    {
        db.close();
    }

    public  SQLiteDatabase getDatabaseInstance()
    {
        return db;
    }


    public void insertRegisteredUsersEntry(String displayName,String phone_number)
    {
        db = dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();

        // Assign values for each row.

        newValues.put("DISPLAYNAME", displayName);

        newValues.put("PHONENUMBER", phone_number);

        // Insert the row into your table
        db.insert("REGISTERED_USERS", null, newValues);

        db.close();

        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM REGISTERED_USERS";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count(buddyact)is ", "" + cursor.getCount());

        db.close();


        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }
    public void insertFbEntry(String first_Name,String email,String link,String password,String confirm_password)
    {
        db = dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();

        // Assign values for each row.

        newValues.put("FIRSTNAME", first_Name);

        newValues.put("USERNAME", email);

        newValues.put("LINK", link);

        newValues.put("PASSWORD", password);

        newValues.put("CONFIRMPASSWORD", confirm_password);

        // Insert the row into your table
        db.insert("FBLOGIN", null, newValues);

        db.close();

        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM FBLOGIN";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count is ", "" + cursor.getCount());

        db.close();


        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public void insertMapsEntry(String username,double lat,double lon,String recorded_date,String time,String address,String city,String state,String country ) {
        db = dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();

        // Assign values for each row.
        newValues.put("UserName", username);

        newValues.put("lat", lat);

        newValues.put("lon", lon);

        newValues.put("RECORDED_DATE", recorded_date);

        newValues.put("time", time);

        newValues.put("address",address);

        newValues.put("City", city);

        newValues.put("state", state);

        newValues.put("country", country);

        // Insert the row into your table
        db.insert("CURRENT_LOCATIONS", null, newValues);
        MapDetails map = new MapDetails();
        map.gotDetails=true;

        db.close();

        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM CURRENT_LOCATIONS";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count(map_Details) is ", "" + cursor.getCount()+"--->"+time+"!!!!"+recorded_date);


        db.close();
    }

    public void insertContactsEntry(String contact_name,String contact_phonenumber)
    {
        db = dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();

        // Assign values for each row.

        newValues.put("contact_name", contact_name);

        newValues.put("contact_phonenumber", contact_phonenumber);

        // Insert the row into your table
        db.insert("CONTACTS_LIST", null, newValues);

        db.close();

        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM CONTACTS_LIST";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count for contacts is ", "" + cursor.getCount());


        db.close();


        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }
    public void insertAllContactsEntry(String contact_name,String contact_phonenumber)
    {
        db = dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();

        // Assign values for each row.

        newValues.put("NAME", contact_name);

        newValues.put("PHONENUMBER", contact_phonenumber);

        // Insert the row into your table
        db.insert("PHONE_BOOK", null, newValues);

        db.close();

        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM PHONE_BOOK";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count allcontacts is ", "" + cursor.getCount());


        db.close();


        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }

    public void insertGoogleEntry(String first_Name,String email,String aboutme,String password,String confirm_password)
    {
        db=dbHelper.getWritableDatabase();
        ContentValues newValues = new ContentValues();

        // Assign values for each row.

        newValues.put("FIRSTNAME", first_Name);

        newValues.put("USERNAME", email);

        newValues.put("ABOUTME", aboutme);

        newValues.put("PASSWORD", password);

        newValues.put("CONFIRMPASSWORD", confirm_password);

        // Insert the row into your table

        db.insert("GOOGLELOGIN", null, newValues);

        db.close();

        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }
    public int deleteFbEntry(String UserName)

    {
        //String id=String.valueOf(ID);
        String where="USERNAME=?";

        int numberOFEntriesDeleted= db.delete("FBLOGIN", where, new String[]{UserName}) ;

        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }

    public int deleteContactsEntry(String phonenumber)

    {
        //String id=String.valueOf(ID);
        String where="contact_phonenumber=?";

        int numberOFEntriesDeleted= db.delete("CONTACTS_LIST", where, new String[]{phonenumber}) ;
        Log.e("deleted"," "+numberOFEntriesDeleted);

        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }
    public int deleteAllRegisteredUsersEntry()

    {
        //String id=String.valueOf(ID);
        int numberOFEntriesDeleted= db.delete("REGISTERED_USERS", null, null) ;
        Log.d("numberOFEntriesDeleted",""+numberOFEntriesDeleted);

        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }
    public int deleteAllContactsEntry()

    {
        //String id=String.valueOf(ID);
        int numberOFEntriesDeleted= db.delete("CONTACTS_LIST", null, null) ;

        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }
    public int deleteAllLocationsEntry()

    {
        //String id=String.valueOf(ID);
        int numberOFEntriesDeleted= db.delete("CURRENT_LOCATIONS", null, null) ;
        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM CURRENT_LOCATIONS";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count(maps_del_ent) is ", "" + cursor.getCount());


        db.close();
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }
    public int delete_All_ContactsEntry()
    {
        //String id=String.valueOf(ID);
        int numberOFEntriesDeleted= db.delete("INSERT", null, null) ;

        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();

        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;

    }

    public Cursor getAllContactDetails()
    {
        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM CONTACTS_LIST";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count is ", "" + cursor.getCount());


        db.close();
        return cursor;
    }

    public Cursor getAllRegisteredUsersDetails()
    {
        db = dbHelper.getReadableDatabase();

        String query = "Select * FROM REGISTERED_USERS";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count is ", "" + cursor.getCount());


        db.close();
        return cursor;
    }

    public Cursor getAllContactPhonenumber()
    {
        db = dbHelper.getReadableDatabase();

        String query = "Select contact_phonenumber FROM CONTACTS_LIST";

        Cursor cursor = db.rawQuery(query, null);

        Log.e("count is ", "" + cursor.getCount());


        db.close();
        return cursor;
    }
    public Cursor getAllContacts(String name)
    {
        db = dbHelper.getReadableDatabase();

        Cursor cursor=db.query("PHONE_BOOK", null, " NAME=?", new String[]{name}, null, null, null);

        if (cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();

        }


        return cursor;
    }

    public int deleteGoogleEntry(String UserName)

    {
        //String id=String.valueOf(ID);

        String where="USERNAME=?";

        int numberOFEntriesDeleted= db.delete("GOOGLELOGIN", where, new String[]{UserName}) ;

        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();

        return numberOFEntriesDeleted;
    }
    public String getFbSinlgeEntry(String userName)
    {

        Cursor cursor=db.query("FBLOGIN", null, " USERNAME=?", new String[]{userName}, null, null, null);

        if (cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();

            return "NOT EXIST";
        }

        cursor.moveToFirst();

        String password= cursor.getString(cursor.getColumnIndex("PASSWORD"));

        rowId=cursor.getInt(cursor.getColumnIndex("ID"));

        Log.d("fbgb", String.valueOf(rowId));

        //  Log.d("password",password);

        cursor.close();

        return password;


    }

    public String getMapsSinlgeEntry(String record_date)
    {

        Cursor cursor=db.query("CURRENT_LOCATIONS", null, " RECORDED_DATE=?", new String[]{record_date}, null, null, null);

        if (cursor.getCount()<1) // UserName Not Exist
        {
            cursor.close();

            return "NOT EXIST";
        }

        cursor.moveToFirst();

        String password= cursor.getString(cursor.getColumnIndex("lat"));

        rowId=cursor.getInt(cursor.getColumnIndex("ID"));

        Log.d("fbgb", String.valueOf(rowId));

        //  Log.d("password",password);

        cursor.close();

        return password;


    }


    public String getGoogleSinlgeEntry(String userName)

    {
        Cursor cursor=db.query("GOOGLELOGIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
        if (cursor.getCount()<1)
        // UserName Not Exist

        {
            cursor.close();

            return "NOT EXIST";
        }

        cursor.moveToFirst();

        String password= cursor.getString(cursor.getColumnIndex("PASSWORD"));

        rowId=cursor.getInt(cursor.getColumnIndex("ID"));

        Log.d("fbgb", String.valueOf(rowId));

        //  Log.d("password",password);


        cursor.close();

        return password;

    }

    public void  updateFbEntry(String firstName,String userName,String link)

    {

        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("FIRSTNAME", firstName);

        updatedValues.put("USERNAME", userName);

        updatedValues.put("LINK", link);

        String where="USERNAME = ?";

        db.update("FBLOGIN", updatedValues, where, new String[]{userName});
    }

    public void  updateMapsEntry( String username,double lat,double lon,String recorded_date,String time,String address,String city,String state,String country)

    {

        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.


        updatedValues.put("address",address);

        updatedValues.put("City", city);

        updatedValues.put("state", state);

        updatedValues.put("country", country);

/*
        String where="RECORDED_DATE = ?";
*/

    db.update("CURRENT_LOCATIONS", updatedValues,"UserName=? AND lat=? AND lon=? AND RECORDED_DATE = ? AND time=?",new String[] {username,""+lat,""+lon, recorded_date,time} );
    }

    public void  updateGoogleEntry(String firstName,String lastName,String userName)

    {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();

        // Assign values for each row.
        updatedValues.put("FIRSTNAME", firstName);

        updatedValues.put("LASTNAME", lastName);

        updatedValues.put("USERNAME", userName);

        String where="USERNAME = ?";

        db.update("GOOGLELOGIN", updatedValues, where, new String[]{userName});

    }
    public  Cursor getFbClientDetails(String username)

    {

        Cursor cursor=db.query("FBLOGIN", null, " USERNAME=?", new String[]{username}, null, null, null);

        if(cursor==null)
        {


            Log.d("cursor values", "cursor is empty");
        }

        return cursor;


    }

    public  Cursor getMapsClientDetails(String record_date)

    {
        db=dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * FROM CURRENT_LOCATIONS", null);
        Log.d("cursor_values",""+cursor.getCount());

       /* Cursor cursor=db.query("CURRENT_LOCATIONS", null, " RECORDED_DATE=?", new String[]{record_date}, null, null, null);
        Log.d("cursor values",""+cursor.getCount());

        if(cursor==null)
        {


            Log.d("cursor values", "cursor is empty");
        }

        return cursor;
*/
       return cursor;

    }

    public  Cursor getContactsClientDetails(String record_date)

    {

        Cursor cursor=db.query("CURRENT_LOCATIONS", null, " RECORDED_DATE=?", new String[]{record_date}, null, null, null);

        if(cursor==null)
        {


            Log.d("cursor values", "cursor is empty");
        }

        return cursor;


    }


    public  Cursor getGoogleClientDetails(String username) {

        db=dbHelper.getReadableDatabase();
        Cursor cursor= db.rawQuery("SELECT * FROM GOOGLELOGIN WHERE USERNAME = " + username, null);

//        Cursor cursor=db.query("GOOGLELOGIN", null, " USERNAME=?", new String[]{username}, null, null, null);

        if(cursor==null)

        {

            Log.d("cursor values","cursor is empty");

        }
/*
        db.close();
*/

        return cursor;


    }

    public void  updatePassword(String new_password,String confirm_password,String username)

    {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("PASSWORD",new_password);

        updatedValues.put("CONFIRMPASSWORD", confirm_password);

        String where="USERNAME = ?";

        db.update("FBLOGIN", updatedValues, where, new String[]{username});
    }
    public void insertEntry(String image,String first_Name,String last_Name,String email,String password,String confirm_password)
    {        db = dbHelper.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        // Assign values for each row.
        newValues.put("IMAGE", String.valueOf(image));
        newValues.put("FIRSTNAME", first_Name);
        newValues.put("LASTNAME", last_Name);
        newValues.put("USERNAME", email);
        newValues.put("PASSWORD", password);
        newValues.put("CONFIRMPASSWORD", confirm_password);

        // Insert the row into your table
        db.insert("LOGIN_MAIN", null, newValues);
        ///Toast.makeText(context, "Reminder Is Successfully Saved", Toast.LENGTH_LONG).show();
    }
    public int deleteEntry(String UserName)
    {
        //String id=String.valueOf(ID);
        String where="USERNAME=?";
        int numberOFEntriesDeleted= db.delete("LOGIN_MAIN", where, new String[]{UserName}) ;
        // Toast.makeText(context, "Number fo Entry Deleted Successfully : "+numberOFEntriesDeleted, Toast.LENGTH_LONG).show();
        return numberOFEntriesDeleted;
    }
    public String getSinlgeEntry(String userName) {
        db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query("LOGIN_MAIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
        if (cursor.getCount() < 1) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        } else {
            cursor.moveToFirst();
            String usernam = cursor.getString(cursor.getColumnIndex("USERNAME"));
            //  Log.d("password",password);
            cursor.close();
            return usernam;
        }
    }
    public String getSinlgeMatch(String userName)
    {        db = dbHelper.getReadableDatabase();

        Cursor cursor=db.query("LOGIN_MAIN", null, " USERNAME=?", new String[]{userName}, null, null, null);
        if ((cursor.getCount()<1)) // UserName Not Exist
        {
            cursor.close();
            return "NOT EXIST";
        }else
        {
            cursor.moveToFirst();
            String Password= cursor.getString(cursor.getColumnIndex("PASSWORD"));
            Log.d("password",Password);
            cursor.close();
            return Password;
        }


    }
    public void  updateEntry(String firstName,String lastName,String userName)
    {
        // Define the updated row content.
        ContentValues updatedValues = new ContentValues();
        // Assign values for each row.
        updatedValues.put("FIRSTNAME", firstName);
        updatedValues.put("LASTNAME", lastName);
        updatedValues.put("USERNAME", userName);

        String where="USERNAME = ?";
        db.update("LOGIN_MAIN", updatedValues, where, new String[]{userName});
    }
    public  Cursor getClientDetails(String username)
    {

        Cursor cursor=db.query("LOGIN_MAIN", null, " USERNAME=?", new String[]{username}, null, null, null);

        if(cursor==null)
        {
            Log.d("cursor values","cursor is empty");
        }

        return cursor;


    }

}
