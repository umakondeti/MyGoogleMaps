package com.example.umamaheshwari.mygooglemaps;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.inputmethodservice.ExtractEditText;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.app.SearchManager;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * Created by chandana on 19-01-2016.
 */
public class ContactRegistrationActivity extends Activity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {

    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;
    ListView lstContacts;
    ContactsListAdapter contacts_adapter;
    ArrayList<String> names, phone_numbers;
    ArrayList<String> position_checked;
    ArrayList<Contacts> contactsData;
    LoginDataBaseAdapter loginDataBaseAdapter;
    TextView Name, PhoneNumber, result;
    CheckBox ckbox;
    Cursor contacts, search, allcontacts;
    Button savedcontacts;
    ImageView searched;
    DataBaseHelper mydb;
    EditText search_contacts;
    SearchView search_view;
Vector<Contacts> allContacts=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        init();
        // Getting reference to listview
/*
        searched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchContacts();

            }

        });*/
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        loginDataBaseAdapter.deleteAllContactsEntry();
        mydb = new DataBaseHelper(this);


/*
        loginDataBaseAdapter.delete_All_ContactsEntry();
*/
        mydb.deleteContact();
        loginDataBaseAdapter.close();
        contactsData.clear();
        phone_numbers.clear();
        position_checked.clear();
        readContacts();
        Log.d("size of contacts", " " + contactsData.size());
        contacts_adapter = new ContactsListAdapter(getApplicationContext(), contactsData);
        lstContacts.setAdapter(contacts_adapter);
        search_view.setOnQueryTextListener(this);

        lstContacts.setOnItemClickListener(this);
        savedcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int contacts_size = contacts_adapter.checkedContacts.size();
                if (contacts_size != 0) {
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

                } else {
                    Toast.makeText(getApplicationContext(), "selected contacts is empty", Toast.LENGTH_SHORT).show();
                }
                loginDataBaseAdapter.close();
                finish();

            }
        });

/*
        // The contacts from the contacts content provider is stored in this
        // cursor
        mMatrixCursor = new MatrixCursor(new String[] {displayName , mobilePhone, photoPath,
                 });

        // Adapter to set data in the listview
        // Creating an AsyncTask object to retrieve and load listview with
        // contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

        // Starting the AsyncTask process to retrieve and load listview with
        // contacts
        listViewContactsLoader.execute();


        // Getting reference to listview
         lstContacts = (ListView)findViewById(R.id.lst_contacts);
        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.contact_items, null, new String[] {displayName, mobilePhone,
               photoPath }, new int[] { R.id.tv_name,
                R.id.tv_details ,R.id.iv_photo }, 0);
        // Setting the adapter to listview
        lstContacts.setAdapter(mAdapter);*/


    }

    private void init() {

        lstContacts = (ListView)findViewById(R.id.lst_contacts);
        names = new ArrayList<>();
        phone_numbers = new ArrayList<>();
        contactsData = new ArrayList<Contacts>();
        position_checked = new ArrayList<String>(names.size());
        savedcontacts = (Button) findViewById(R.id.save_contacts);
        search_view = (SearchView) findViewById(R.id.search_view);


    }

    private void searchContacts() {

        String contname = search_contacts.getText().toString();
        allcontacts = mydb.getData(contname);
        if (allcontacts != null) {
            allcontacts.moveToFirst();
            do {
                String name = allcontacts.getString(allcontacts.getColumnIndex("name"));
                String phonenumber = allcontacts.getString(allcontacts.getColumnIndex("phonenumber"));
                result.setText(name + " " + phonenumber);

            } while(allcontacts.moveToNext());
        }
        loginDataBaseAdapter.close();
       /* String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        Uri lkup = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, contname);
        Cursor idCursor = getContentResolver().query(lkup,null,contname,null,sortOrder);
        int search_contact= 0;
        assert idCursor != null;
        search_contact = idCursor.getCount();
        if ((search_contact!=0) && (contname.length() > 0)) {
        do {
            idCursor.moveToFirst();
            String search_name = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String search_phonenumber = idCursor.getString(idCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
*//*
            result.setText("\n"+search_name +"\n"+search_phonenumber);
*//*

            Log.d("contact_search", "search: "+search_name + " key: "+search_phonenumber );
        }while (idCursor.moveToNext());
        idCursor.close();
    }*/

    }

    /**
     * An AsyncTask class to retrieve and load listview with contacts
     */
  /*  private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... params) {
            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            // Querying the table ContactsContract.Contacts to retrieve all the
            // contacts
            Cursor contactsCursor = getContentResolver().query(contactsUri,
                    null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

            if (contactsCursor.moveToFirst()) {
                do {
                    long contactId = contactsCursor.getLong(contactsCursor
                            .getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    // Querying the table ContactsContract.Data to retrieve
                    // individual items like
                    // home phone, mobile phone, work email etc corresponding to
                    // each contact
                    Cursor dataCursor = getContentResolver().query(dataUri,
                            null,
                            ContactsContract.Data.CONTACT_ID + "=" + contactId,
                            null, null);



                    if (dataCursor.moveToFirst()) {
                        // Getting Display Name
                        displayName = dataCursor
                                .getString(dataCursor
                                        .getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                        do {

                            // Getting NickName
                            if (dataCursor
                                    .getString(
                                            dataCursor
                                                    .getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
                                nickName = dataCursor.getString(dataCursor
                                        .getColumnIndex("data1"));

                            // Getting Phone numbers
                            if (dataCursor
                                    .getString(
                                            dataCursor
                                                    .getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)) {
                                switch (dataCursor.getInt(dataCursor
                                        .getColumnIndex("data2"))) {
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                                        homePhone = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                                        mobilePhone = dataCursor
                                                .getString(dataCursor
                                                        .getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                                        workPhone = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"));
                                        break;
                                }
                            }

                            // Getting EMails
                            if (dataCursor
                                    .getString(
                                            dataCursor
                                                    .getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                                switch (dataCursor.getInt(dataCursor
                                        .getColumnIndex("data2"))) {
                                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                                        homeEmail = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                                        workEmail = dataCursor.getString(dataCursor
                                                .getColumnIndex("data1"));
                                        break;
                                }
                            }

                            // Getting Organization details
                            if (dataCursor
                                    .getString(
                                            dataCursor
                                                    .getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)) {
                                companyName = dataCursor.getString(dataCursor
                                        .getColumnIndex("data1"));
                                title = dataCursor.getString(dataCursor
                                        .getColumnIndex("data4"));
                            }

                            // Getting Photo
                            if (dataCursor
                                    .getString(
                                            dataCursor
                                                    .getColumnIndex("mimetype"))
                                    .equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)) {
                                photoByte = dataCursor.getBlob(dataCursor
                                        .getColumnIndex("data15"));

                                if (photoByte != null) {
                                    Bitmap bitmap = BitmapFactory
                                            .decodeByteArray(photoByte, 0,
                                                    photoByte.length);

                                    // Getting Caching directory
                                    File cacheDirectory = getBaseContext()
                                            .getCacheDir();

                                    // Temporary file to store the contact image
                                    File tmpFile = new File(
                                            cacheDirectory.getPath() + "/wpta_"
                                                    + contactId + ".png");

                                    // The FileOutputStream to the temporary
                                    // file
                                    try {
                                        FileOutputStream fOutStream = new FileOutputStream(
                                                tmpFile);

                                        // Writing the bitmap to the temporary
                                        // file as png file
                                        bitmap.compress(
                                                Bitmap.CompressFormat.PNG, 100,
                                                fOutStream);

                                        // Flush the FileOutputStream
                                        fOutStream.flush();

                                        // Close the FileOutputStream
                                        fOutStream.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    photoPath = tmpFile.getPath();
                                }

                            }

                        } while (dataCursor.moveToNext());

                        String details = "";

                        // Concatenating various information to single string
                        if (homePhone != null && !homePhone.equals(""))
                            details = "HomePhone : " + homePhone + "\n";
                        if (mobilePhone != null && !mobilePhone.equals(""))
                            details += "MobilePhone : " + mobilePhone + "\n";
                        if (workPhone != null && !workPhone.equals(""))
                            details += "WorkPhone : " + workPhone + "\n";
                        if (nickName != null && !nickName.equals(""))
                            details += "NickName : " + nickName + "\n";
                        if (homeEmail != null && !homeEmail.equals(""))
                            details += "HomeEmail : " + homeEmail + "\n";
                        if (workEmail != null && !workEmail.equals(""))
                            details += "WorkEmail : " + workEmail + "\n";
                        if (companyName != null && !companyName.equals(""))
                            details += "CompanyName : " + companyName + "\n";
                        if (title != null && !title.equals(""))
                            details += "Title : " + title + "\n";

                        // Adding id, display name, path to photo and other
                        // details to cursor
                        mMatrixCursor.addRow(new Object[] {
                                Long.toString(contactId), displayName,
                                photoPath, details });
                    }

                } while (contactsCursor.moveToNext());
            }
            return mMatrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview
            mAdapter.swapCursor(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_contacts, menu);
        return true;
    }*/
    private void readContacts() {
        // TODO Auto-generated method stub
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";

        ContentResolver cr = getContentResolver();
        Cursor contacts_cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, sortOrder);
        int count = contacts_cursor.getCount();
        boolean addNewContact;

        if (count != 0) {
            contacts_cursor.moveToFirst();
            for (int i = 0; i < count; i++)
                do {
                    String name = contacts_cursor.getString(contacts_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    String thumb = contacts_cursor.getString(contacts_cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));
                    String id = contacts_cursor.getString(contacts_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                    String phoneNumber = contacts_cursor.getString(contacts_cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    if (contactsData==null)
                        contactsData = new ArrayList<Contacts>();


                    addNewContact = true;
                    for (Contacts addedC:contactsData){
                        if (addedC.id.equals(id))
                            addNewContact = false;
                    }

                    if (addNewContact) {
                        Contacts country = new Contacts(name, phoneNumber, id);
                        contactsData.add(country);
                      /*  if (thumb != null)
                            c.setThumb(cr, thumb);
                        allContacts.add(c);
                    }  */
                    }

                    if (i == 0)
                        Toast.makeText(getApplicationContext(), name + "your contacts saved successfully" + phoneNumber, Toast.LENGTH_SHORT).show();
                    if (i == names.size())
                        Toast.makeText(getApplicationContext(), name + "your contacts saved successfully" + phoneNumber, Toast.LENGTH_SHORT).show();

                    i++;
                } while (contacts_cursor.moveToNext());

        }

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Name = (TextView) view.findViewById(R.id.tv_name);
        PhoneNumber = (TextView) view.findViewById(R.id.tv_number);
        ckbox = (CheckBox) view.findViewById(R.id.cb_select);
        ckbox.performClick();
        if (ckbox.isChecked()) {
            Toast.makeText(view.getContext(), "checked", Toast.LENGTH_LONG).show();

            position_checked.add(PhoneNumber.getText().toString());

           /* loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.insertContactsEntry(Name.getText().toString(), PhoneNumber.getText().toString());
            loginDataBaseAdapter.close();*/
            Toast.makeText(view.getContext(), Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + PhoneNumber, Toast.LENGTH_LONG).show();


        } else if (!ckbox.isChecked()) {
            Toast.makeText(view.getContext(), "unchecked", Toast.LENGTH_LONG).show();

          /*  position_checked.remove(PhoneNumber.getText().toString());
            loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.deleteContactsEntry(PhoneNumber.getText().toString());
            loginDataBaseAdapter.close();
*/
        }
        Toast.makeText(view.getContext(), Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + PhoneNumber, Toast.LENGTH_LONG).show();


    }

    private String isCheckedOrNot(CheckBox checkbox) {
        if (checkbox.isChecked())

            return "is checked";

        else
            return "is not checked";
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        contacts_adapter.getFilter().filter(newText);
        return false;
    }

}

