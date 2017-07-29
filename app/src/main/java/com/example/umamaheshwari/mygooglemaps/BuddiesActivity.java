package com.example.umamaheshwari.mygooglemaps;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user1 on 09-May-17.
 */

public class BuddiesActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SearchView.OnQueryTextListener {
    LinearLayout llv_add_your_buddies;
    SearchView buddies_search_view;
    private FirebaseAuth auth;
    private FirebaseUser mFirebaseuser;
    public DatabaseReference mDatabaseReference;
    public DatabaseReference mRootRef;
    private String mUserId;
    ContactsListAdapter buddies_adapter;
    long added_buddies = 0;
    long total_buddies = 0;
    ListView lv_buddies;
    Firebase ref;
    LoginDataBaseAdapter loginDataBaseAdapter;
    //ArrayList<String> selected_buddies;
    Button btn_add_buddies;
    String registered_users;
    TextView Name, user_key;
    CheckBox ckbox;
    MyMapsUser user;
    public ProgressBar progress_buddies;
    SessionManager session;
    String selected_buddy_name;
    String selected_user_key;
    Map<String, Object> adding_user_buddies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buddies);
        llv_add_your_buddies = (LinearLayout) findViewById(R.id.llv_add_your_buddies);
        buddies_search_view = (SearchView) findViewById(R.id.buddies_search_view);
        btn_add_buddies = (Button) findViewById(R.id.btn_add_buddies);
        session = new SessionManager(this);
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        // sel
        //get firebase auth instance
        auth = FirebaseAuth.getInstance();
        //get current user
        mFirebaseuser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.keepSynced(true);
     /*   loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();
        loginDataBaseAdapter.deleteAllRegisteredUsersEntry();
        loginDataBaseAdapter.close();*/

        Firebase.setAndroidContext(BuddiesActivity.this);
        ref = new Firebase("https://maps-eb9bd.firebaseio.com/");
        user = new MyMapsUser();
        user.selected_buddies.clear();
        if (auth != null) {
            mUserId = mFirebaseuser.getUid();
        }
        adding_user_buddies = new HashMap<>();
        // selected_buddies = new ArrayList<String>();
        progress_buddies = (ProgressBar) findViewById(R.id.pb_buddies);
        lv_buddies = (ListView) findViewById(R.id.lv_buddies);
        lv_buddies.setOnItemClickListener(this);
        if (Utilities.registeredUsersArrayList.size() == 0) {
            progress_buddies.setVisibility(View.VISIBLE);
        } else {
            progress_buddies.setVisibility(View.GONE);
        }
        buddies_adapter = new ContactsListAdapter(getApplicationContext(), Utilities.registeredUsersArrayList);
        lv_buddies.setAdapter(buddies_adapter);

        buddies_search_view.setOnQueryTextListener(this);
        // getAllRegisteredUsers();
        llv_add_your_buddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BuddiesActivity.this, ContactRegistrationActivity.class));
            }
        });
        btn_add_buddies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_buddies.setVisibility(View.VISIBLE);
                Log.d("user_sel_buddies", "" + user.selected_buddies.size());
                Log.d("user_display_name", "" + session.getLogInUserDisplayName());

                if (user.selected_buddies.size() != 0)
                    addBuddiesInFireBase();
                else {
                    Toast.makeText(getApplicationContext(), "you didn't select your buddies", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void addBuddiesInFireBase() {
        loginDataBaseAdapter = new LoginDataBaseAdapter(this);
        loginDataBaseAdapter = loginDataBaseAdapter.open();

        int contacts_size = buddies_adapter.checkedContacts.size();
        if (contacts_size != 0) {
            for (int i = 0; i < contacts_size; i++) {
                String selected[] = buddies_adapter.checkedContacts.get(i).split("-~-");
                String user_buddy_name = selected[0];
                String user_buddy_key = selected[1];
                Contacts users = new Contacts("" + user_buddy_name, user_buddy_key);
                Utilities.selectedUsersBuddiesList.add(users);
                loginDataBaseAdapter.insertContactsEntry(user_buddy_name, user_buddy_key);
                loginDataBaseAdapter.close();
                Log.d(" saved data", "--->" + buddies_adapter.checkedContacts.get(i));
            }
            Toast.makeText(getApplicationContext(), "successfully added into contacts", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getApplicationContext(), "selected contacts is empty", Toast.LENGTH_SHORT).show();
        }
        loginDataBaseAdapter.close();
        //  finish();


        if (user.selected_buddies.size() != 0) {
            for (int j = 0; j < user.selected_buddies.size(); j++) {
                String select_buddies_statement = user.selected_buddies.get(j);
                String[] selected_Buddies = select_buddies_statement.split("~");
                String selectedBuddiesUserName = selected_Buddies[0];
                String selectedBuddyKey = selected_Buddies[1];

                Log.d("modifiedha?", " " + selectedBuddiesUserName + "---->" + selectedBuddyKey);
                MyMapsUser users = new MyMapsUser(selectedBuddiesUserName, selectedBuddyKey);

                Log.d("userIdinBuddies", " " + mUserId);
                Log.d("Buddy_name", " " + session.getLogInUserDisplayName());
                if (j + 1 == user.selected_buddies.size()) {
                    Log.d("buddies_size", " " + users.getSelectedBuddies(user.selected_buddies).size());

                    adding_user_buddies = users.getSelectedBuddies(user.selected_buddies);
                    Log.d("rady to_add_bud_size", " " + user.getSelectedBuddies(user.selected_buddies).size());

                }
            }

              /*  for (int i = 0; i < user.getRegisteredUsers().size(); i++) {
                    Log.d("buddy", " " + user.getRegisteredUsers().get(i));
                }*/
            mDatabaseReference = FirebaseDatabase.getInstance().getReference();
            mDatabaseReference.keepSynced(true);
            // if ((auth.getCurrentUser().getUid()!= null) && (auth.getCurrentUser().getDisplayName() != null)&&(user.selected_buddies.size()!=0))
            mDatabaseReference.child("my_maps_user").child(mUserId).child(session.getLogInUserDisplayName()).child("Buddies").updateChildren(adding_user_buddies).
                    addOnCompleteListener(BuddiesActivity.this,
                            new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("success", "added fields" + task.getResult());
                                        progress_buddies.setVisibility(View.GONE);
                                        // utilities.cancelProgressDialog();
                                        Intent intent = new Intent(BuddiesActivity.this, GoogleMaps.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                                                   /* singleton.setLoginUserDisplayName(firstName + "" + lastName);
                                                                    singleton.setLoginUserEmailId(EmailId);
                                                                    singleton.setLoginImage(userProfilePicUrl);*/
                                        Toast.makeText(BuddiesActivity.this, "successfully added into your buddies." + task.getResult(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        progress_buddies.setVisibility(View.GONE);
                                        AlertDialog.Builder builder = new AlertDialog.Builder(BuddiesActivity.this);
                                        builder.setMessage(task.getException().getMessage())
                                                .setTitle(R.string.login_error_title)
                                                .setPositiveButton(android.R.string.ok, null);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                        Toast.makeText(BuddiesActivity.this, "not added fields." + task.getException(), Toast.LENGTH_SHORT).show();

                                        Log.d("failure", "" + task.getException());
                                    }

                                }
                            });
        } else
            Toast.makeText(getApplicationContext(), "your selection is wrong", Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Name = (TextView) view.findViewById(R.id.tv_name);
        user_key = (TextView) view.findViewById(R.id.tv_number);
        // PhoneNumber = (TextView) view.findViewById(R.id.tv_number);
        ckbox = (CheckBox) view.findViewById(R.id.cb_select);
        ckbox.performClick();
        if (ckbox.isChecked()) {
            Toast.makeText(view.getContext(), "checked", Toast.LENGTH_LONG).show();

            selected_buddy_name = Name.getText().toString().trim();
            selected_user_key = user_key.getText().toString().trim();
            user.selected_buddies.add(selected_buddy_name + "~" + selected_user_key);

           /* loginDataBaseAdapter = loginDataBaseAdapter.open();
            loginDataBaseAdapter.insertContactsEntry(Name.getText().toString(), PhoneNumber.getText().toString());
            loginDataBaseAdapter.close();*/
            Toast.makeText(view.getContext(), Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + Name, Toast.LENGTH_LONG).show();


        } else if (!ckbox.isChecked()) {
            //user.selected_buddies.remove(Name.getText().toString()+ "~" +user_key);

            Toast.makeText(view.getContext(), "unchecked", Toast.LENGTH_LONG).show();
        }
        Toast.makeText(view.getContext(), Name.getText().toString() + " " + isCheckedOrNot(ckbox) + " " + Name, Toast.LENGTH_LONG).show();
    }


    /* private void getAllRegisteredUsers() {
         loginDataBaseAdapter = loginDataBaseAdapter.open();
         Firebase.setAndroidContext(BuddiesActivity.this);
         ref = new Firebase("https://maps-eb9bd.firebaseio.com/my_maps_user/");
         if (mUserId != null) {
             mRootRef = FirebaseDatabase.getInstance().getReference();
            *//* userDisplayName = session.getLogInUserDisplayName();
            Log.d("getting_username", "" + userDisplayName);*//*
            Query mDatabaseReference_query = mRootRef.child("my_maps_user").child("emailToUid");
            Log.d("url", "" + mDatabaseReference_query);
            mDatabaseReference_query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {


                    Log.d("query_shots", "" + dataSnapshot.getChildrenCount());
                    total_buddies = dataSnapshot.getChildrenCount();
                    Log.d("queries_value", "" + dataSnapshot.getValue());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mDatabaseReference_query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d("query_shot", "" + dataSnapshot.getKey());
                    Log.d("query_svalue", "" + dataSnapshot.getValue());
                    added_buddies++;


                    Contacts users = new Contacts("" + dataSnapshot.getKey());
                    Utilities.registered_users.add(users);
                    // loginDataBaseAdapter.insertRegisteredUsersEntry();
                    Log.d("total_buddies", "" + total_buddies);
                    Log.d("added_buddies", "" + added_buddies);
                    String users_details = "" + dataSnapshot.getValue();
                    String[] allUsersData = users_details.split("#");
                    String username = dataSnapshot.getKey();
                    String username_key = allUsersData[0];
                    String user_email = allUsersData[1];
                    String user_phone_num = allUsersData[2];
                    Log.d("geting_data",""+username +"_--~--"+username_key+"--~--"+user_email+"--~--"+ user_phone_num);
                    loginDataBaseAdapter.insertRegisteredUsersEntry(username, user_phone_num);
                    Log.d("user_details", users_details);
                    // if (total_buddies == added_buddies) {
                    Log.d("inside", "" + added_buddies);
                    Log.d("size", "" + Utilities.registered_users.size());
                    buddies_adapter = new ContactsListAdapter(getApplicationContext(), Utilities.registered_users);
                    lv_buddies.setAdapter(buddies_adapter);
                    // }


                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        loginDataBaseAdapter.close();
    }*/
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

        buddies_adapter.getFilter().filter(newText);

        return false;
    }


}