/*
package com.example.umamaheshwari.mygooglemaps;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

*/
/**
 * Created by chandana on 18-01-2016.
 *//*

public class HomeActivity extends Activity {
    ImageView menu,contacts;
    ListView llContactsList;
    FeedRunnerDBHelper feedRunnerDBHelper;
    SQLiteDatabase sqLiteDatabase;

    ArrayList<HashMap<String,String>>rowData=new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String,String>>rowfullData=new ArrayList<HashMap<String, String>>();
    String[] data;
    SwipeRefreshLayout swipeRefreshLayout;
    ContactsListAdapter c;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_home);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.header_custom);
        feedRunnerDBHelper=new FeedRunnerDBHelper(getApplicationContext());
        sqLiteDatabase=feedRunnerDBHelper.getReadableDatabase();
        llContactsList=(ListView)findViewById(R.id.ll_contacts);
        menu=(ImageView)findViewById(R.id.ivH_menu);
        contacts=(ImageView)findViewById(R.id.ivH_contacts);
        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.sw_list_View);
        contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ContactRegistrationActivity.class));
            }
        });
        listviewDisplay();
        c = new ContactsListAdapter(getApplicationContext(),rowData);
        llContactsList.setAdapter(c);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                c.notifyDataSetChanged();
//                llContactsList.deferNotifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                listviewFulldata();

                ContactsListAdapter c1=new ContactsListAdapter(getApplicationContext(),rowfullData);
                llContactsList.setAdapter(c1);
            }
        });

    }

    private void listviewFulldata() {
        String Select_contact_list="SELECT "+FeedRunnerDBHelper.CONTACTS_COLUMN_IMGPATH+","+FeedRunnerDBHelper.CONTACTS_COLUMN_NAME+","+FeedRunnerDBHelper.CONTACTS_COLUMN_PHONENUMBER
                +" FROM "+FeedRunnerDBHelper.TABLE_CONTACTS +" WHERE rowid!="+";";
        Cursor cursor_contactList=sqLiteDatabase.rawQuery(Select_contact_list,null);
        if(cursor_contactList!=null)
        {
            while (cursor_contactList.moveToNext())
            {
                HashMap<String,String>hashMap=new HashMap<>();
                hashMap.put("imagePath",cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_IMGPATH)));
                hashMap.put("Name",cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_NAME)));
                hashMap.put("phonenumber",cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_PHONENUMBER)));
                rowfullData.add(hashMap);
                Log.d("phone", "" + cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_IMGPATH)));
            }
            Log.d("CursorContactList",""+cursor_contactList);
        }
        cursor_contactList.close();
    }

    private void listviewDisplay() {
        String Select_contact_list="SELECT "+FeedRunnerDBHelper.CONTACTS_COLUMN_IMGPATH+","+FeedRunnerDBHelper.CONTACTS_COLUMN_NAME+","+FeedRunnerDBHelper.CONTACTS_COLUMN_PHONENUMBER
                +" FROM "+FeedRunnerDBHelper.TABLE_CONTACTS+" LIMIT 5";
        Cursor cursor_contactList=sqLiteDatabase.rawQuery(Select_contact_list,null);
        if(cursor_contactList!=null)
        {
            while (cursor_contactList.moveToNext())
            {
                HashMap<String,String>hashMap=new HashMap<>();
                hashMap.put("imagePath",cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_IMGPATH)));
                hashMap.put("Name",cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_NAME)));
                hashMap.put("phonenumber",cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_PHONENUMBER)));
                rowData.add(hashMap);
                Log.d("phone", "" + cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_IMGPATH)));
            }
            Log.d("CursorContactList",""+cursor_contactList);
        }
        cursor_contactList.close();
        for(int rowdatalength=0;rowdatalength<rowData.size();rowdatalength++)
        {
            HashMap<String,String> d=rowData.get(rowdatalength);
            Log.d("imagpath,name",""+d.get("imagePath")+d.get("Name")+d.get("phonenumber"));
        }
    }
}
*/
/*data=new String[3];
                data[0]=cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_IMGPATH));
                data[1]=cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_NAME));
                data[2]=cursor_contactList.getString(cursor_contactList.getColumnIndex(FeedRunnerDBHelper.CONTACTS_COLUMN_PHONENUMBER));
                rowData.add(nzmd);
                rowData.add(ph);
                rowData.add(data);*/
