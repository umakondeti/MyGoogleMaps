package com.example.umamaheshwari.mygooglemaps;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

class SearchContacts extends Activity  implements OnClickListener{

    final static String LOG_TAG = "PocketMagic";
    //-
    TextView tv1,tv2;
    EditText et1,et2;
    Button b1,b2;
    //-
    final static int 	idb1 			= Menu.FIRST + 1,
            idb2 			= Menu.FIRST + 2;
    //idEDIT_CONTACT 	= Menu.FIRST + 2;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        //Hide titlebar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        ScrollView vscroll = new ScrollView(this);
        vscroll.setFillViewport(false);
        //panel in scroll: add all controls/ objects to this layout
        LinearLayout m_panel = new LinearLayout (this);
        m_panel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
        m_panel.setOrientation(LinearLayout.VERTICAL);
        m_panel.setPadding(10,10,10,10);
        vscroll.addView(m_panel);

        //Create some controls
        tv1 = new TextView(this);
        tv1.setText("Search sample");
        m_panel.addView(tv1);

        et1 = new EditText(this);
        et1.setHint("Enter Name to search");
        m_panel.addView(et1);

        b1 = new Button(this);
        b1.setId(idb1);
        b1.setOnClickListener((OnClickListener) this);
        b1.setText("Search by name");
        m_panel.addView(b1);

		/*et2 = new EditText(this);
		et2.setHint("Enter ID to search");
		m_panel.addView(et2);*/
        b2 = new Button(this);
        b2.setId(idb2);
        b2.setOnClickListener((OnClickListener) this);
        b2.setText("List all contacts");
        m_panel.addView(b2);

        tv2 = new TextView(this);
        tv2.setText("Results:");
        m_panel.addView(tv2);

        setContentView(vscroll);


    }
    public void onClick(View arg0) {
        // TODO Auto-generated method stub
        int idc = arg0.getId();
        if (idc == idb1) {
            String contname = et1.getText().toString();
            if (contname != null && contname.length()>0) {
                Uri lkup = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_FILTER_URI, contname);
                String results = "";
                Cursor idCursor = getContentResolver().query(lkup, null, null, null, null);
                while (idCursor.moveToNext()) {
                    long lid = idCursor.getLong(idCursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String key = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                    String name = idCursor.getString(idCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    Log.d(LOG_TAG, "search: "+lid + " key: "+key + " name: "+name);
                    results+=("\n"+"Name:"+name+" ID:"+lid+" Key:"+key);
                }
                idCursor.close();
                tv2.setText(results);
            }
            else tv2.setText("Enter a name to search for!");
        }
        if (idc==idb2) {
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            // set projection: aka the fields to be retrieved
            String[] projection = new String[] {
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.LOOKUP_KEY
            };
            // set additional parameters:
            boolean mShowInvisible = false;
            String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" +
                    (mShowInvisible ? "0" : "1") + "'";
            // sort the results
            String[] selectionArgs = null;
            String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
            // run the SQL query
            String results = "";
            Cursor cur =  managedQuery(uri, projection, selection, selectionArgs, sortOrder);
            while (cur.moveToNext()) {
                long lid = cur.getLong(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String key = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                Log.d(LOG_TAG, "VISIBLE Contact: "+lid +  " name: "+name +" key:"+key);
                results+=("\n"+"Name:"+name+" ID:"+lid+" Key:"+key);
            }
            cur.close();
            tv2.setText(results);
        }

    }
}