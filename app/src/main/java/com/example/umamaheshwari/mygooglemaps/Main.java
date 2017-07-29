/*
package com.example.umamaheshwari.mygooglemaps;

import android.app.Activity;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class Main extends Activity implements
        SearchView.OnQueryTextListener,AdapterView.OnItemClickListener {

    ListView lv;
    SearchView search_view;
    String[] country_names, iso_codes;
    TypedArray country_flags;
    ArrayList<Contacts> countrylist;
    ContactsListAdapter adapter;
    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;
    ListView lstContacts;
    ContactsListAdapter contacts_adapter;
    ArrayList<String>names,phone_numbers;
    ArrayList<String> position_checked;
    ArrayList<Contacts> contactsData;
    LoginDataBaseAdapter loginDataBaseAdapter;
    TextView Name,PhoneNumber,result;
    CheckBox ckbox;
    Cursor contacts,search,allcontacts;
    Button savedcontacts;
    ImageView searched;
    DataBaseHelper mydb;
    EditText search_contacts;
    */
/**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     *//*

    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        init();
        // Getting reference to listview
*/
/*
        searched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchContacts();

            }

        });*//*

        loginDataBaseAdapter= new LoginDataBaseAdapter(this);
        loginDataBaseAdapter=loginDataBaseAdapter.open();
        loginDataBaseAdapter.deleteAllContactsEntry();
        mydb= new DataBaseHelper(this);


*/
/*
        loginDataBaseAdapter.delete_All_ContactsEntry();
*//*

        mydb.deleteContact();
        loginDataBaseAdapter.close();
        contactsData.clear();
        phone_numbers.clear();
        position_checked.clear();
        readContacts();
        lv = (ListView) findViewById(R.id.lst_contacts);
        search_view = (SearchView) findViewById(R.id.search_view);
        lstContacts.setOnItemClickListener(this);
        savedcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int contacts_size= contacts_adapter.checkedContacts.size();
                if(contacts_size!=0) {
                    for (int i = 0; i < contacts_size; i++) {
                        String selected[] = contacts_adapter.checkedContacts.get(i).split("-~-");
                        String name = selected[0];
                        String phone_num = selected[1];
                        loginDataBaseAdapter = loginDataBaseAdapter.open();
                        loginDataBaseAdapter.insertContactsEntry(name, phone_num);
                        loginDataBaseAdapter.close();
                        Log.d(" saved data", "--->" + contacts_adapter.checkedContacts.get(i));
                    }
                    Toast.makeText(getApplicationContext(), "successfully added into contacts", Toast.LENGTH_SHORT).show();

                }else
                {
                    Toast.makeText(getApplicationContext(),"selected contacts is empty",Toast.LENGTH_SHORT).show();

                }
                loginDataBaseAdapter.close();

                finish();

            }
        });

        }

        adapter = new ContactsListAdapter(getApplicationContext(), countrylist);
        lv.setAdapter(adapter);

        search_view.setOnQueryTextListener(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        adapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    */
/**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     *//*

    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a uk_fg for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Name = (TextView)view.findViewById(R.id.tv_name);
        PhoneNumber = (TextView)view.findViewById(R.id.tv_number);
        ckbox =(CheckBox)view.findViewById(R.id.cb_select);
        ckbox.performClick();
        if (ckbox.isChecked()) {
            Toast.makeText(view.getContext(), "checked", Toast.LENGTH_LONG).show();

            position_checked.add(PhoneNumber.getText().toString());

           */
/* loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.insertContactsEntry(Name.getText().toString(), PhoneNumber.getText().toString());
            loginDataBaseAdapter.close();*//*

            Toast.makeText(view.getContext(), Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + PhoneNumber, Toast.LENGTH_LONG).show();


        } else if (!ckbox.isChecked()) {
            Toast.makeText(view.getContext(), "unchecked", Toast.LENGTH_LONG).show();

          */
/*  position_checked.remove(PhoneNumber.getText().toString());
            loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.deleteContactsEntry(PhoneNumber.getText().toString());
            loginDataBaseAdapter.close();
*//*

        }
        Toast.makeText(view.getContext(), Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + PhoneNumber, Toast.LENGTH_LONG).show();


    }
}*/
