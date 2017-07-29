package com.example.umamaheshwari.mygooglemaps.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.example.umamaheshwari.mygooglemaps.BuddiesActivity;
import com.example.umamaheshwari.mygooglemaps.Contacts;
import com.example.umamaheshwari.mygooglemaps.LoginDataBaseAdapter;
import com.example.umamaheshwari.mygooglemaps.R;

import java.util.ArrayList;

/**
 * Created by user1 on 03-Jun-17.
 */

public class UserBuddiesAdapter extends BaseAdapter implements Filterable {
    ArrayList<Buddies> names;
    public ArrayList<String> selected_contacts = new ArrayList<String>();
    ArrayList<String> position_checked;
    ArrayList<Buddies> mStringFilterList;
    ValueFilter valueFilter;
    boolean[] itemChecked;
    Buddies buddies;
    ArrayList<String> sampleUsersBuddiesList;
    LoginDataBaseAdapter loginDataBaseAdapter;
    String checked_name;
    SharedPreferences sharedPreferences;
    String checked_phonenumber;
    int count = 0;
    Context context;
    LayoutInflater inflater;
    String Section;

    public UserBuddiesAdapter(Context context, ArrayList<Buddies> names) {
        this.context = context;
        this.names = names;
        this.mStringFilterList = names;
        this.position_checked = position_checked;
        itemChecked = new boolean[names.size()];
       /* position_checked = new ArrayList<Boolean>(names.size());
        for (int i = 0; i < names.size(); i++) {
            position_checked.add(false);
*//*
        this.images=image_paths;
*//*
            inflater = LayoutInflater.from(this.context);
        }*/
        loginDataBaseAdapter = new LoginDataBaseAdapter(this.context);
        inflater = LayoutInflater.from(this.context);

    }

    public UserBuddiesAdapter(Context context, ArrayList<Buddies> names,ArrayList<String> sample_user_buddiesList) {
        this.context = context;
        this.names = names;
        this.mStringFilterList = names;
        this.position_checked = position_checked;
        itemChecked = new boolean[names.size()];
        selected_contacts = new ArrayList<String>();
        this.sampleUsersBuddiesList = sample_user_buddiesList;

       /* position_checked = new ArrayList<Boolean>(names.size());
        for (int i = 0; i < names.size(); i++) {
            position_checked.add(false);
*//*
        this.images=image_paths;
*//*
            inflater = LayoutInflater.from(this.context);
        }*/
        loginDataBaseAdapter = new LoginDataBaseAdapter(this.context);
        inflater = LayoutInflater.from(this.context);

    }

    @Override
    public int getCount() {
        return names.size();
    }

    @Override
    public Object getItem(int position) {
        return names.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        if (row == null) {
            Integer selected_position = -1;
            row = inflater.inflate(R.layout.contact_items, parent, false);
            holder = new UserBuddiesAdapter.Holder();
            holder.Name = (TextView) row.findViewById(R.id.tv_name);
            holder.PhoneNumber = (TextView) row.findViewById(R.id.tv_number);
            holder.ckbox = (CheckBox) row.findViewById(R.id.cb_select);
            row.setTag(holder);
        } else {

            holder = (Holder) row.getTag();
        }
        count++;
        holder.ckbox.setFocusable(false);
        buddies = (Buddies)names.get(position);
        Log.d("sample_bud_list_size"," "+sampleUsersBuddiesList.size());

/*
        String contactStringArray[] = names.get(position).split("-~-");
*/
        holder.Name.setText(buddies.getName());
        holder.PhoneNumber.setText(buddies.getPhone_num());
        Log.d("inside_adapter_name", "  " + buddies.getName() + " " + buddies.getPhone_num());
        holder.ckbox.setChecked(false);
        try {
            if (itemChecked[position])
                holder.ckbox.setChecked(true);
            else
                holder.ckbox.setChecked(false);
        } catch (Exception e) {

        }


        final Holder finalHolder1 = holder;
        holder.ckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    String cheppali=sampleUsersBuddiesList.get(position);

                    if (finalHolder1.ckbox.isChecked()) {
                        itemChecked[position] = true;
                        String str1[] = {buddies.getName(), buddies.getPhone_num()};
                        Log.d("cheppali","---->"+cheppali);

                            //selected_contacts.clear();
                            selected_contacts.add(cheppali);
                            Log.d("checked_name", "--->" + cheppali);
                            Log.d("after_added_size",""+selected_contacts.size());

                    } else {
                        itemChecked[position] = false;
                        String contactunCheckedName[] = sampleUsersBuddiesList.get(position).split("-~-");
                        for (int i = 0; i < selected_contacts.size(); i++) {
                            String str[] = selected_contacts.get(i).split("-~-");
                            if (contactunCheckedName[0].equals(str[0])) {
                                selected_contacts.remove(cheppali);
                                Log.d("after_remove_size",""+selected_contacts.size());

                            }
                        }

                    }
                    Log.d("inside_adapter_size",""+selected_contacts.size());

                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), "You are not checked properly", Toast.LENGTH_SHORT).show();
                /*    Intent in = new Intent(context.getApplicationContext(),GoogleMaps.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in);*/
                }
                Log.d("inside_adapter_size",""+selected_contacts.size());


            }
        });
        return row;
    }

    @Override
    public Filter getFilter() {

        if (valueFilter == null) {
            valueFilter = new UserBuddiesAdapter.ValueFilter();
        }
        return valueFilter;
    }

    static class Holder {
        TextView Name, PhoneNumber;
        CheckBox ckbox;
        Button savedcontacts;

    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                ArrayList<Buddies> filterList = new ArrayList<Buddies>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        Buddies country = new Buddies(mStringFilterList.get(i)
                                .getName(), mStringFilterList.get(i)
                                .getPhone_num());

                        filterList.add(country);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = mStringFilterList.size();
                results.values = mStringFilterList;
            }

            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            names = (ArrayList<Buddies>) results.values;
            notifyDataSetChanged();
        }

    }
}

