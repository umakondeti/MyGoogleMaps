package com.example.umamaheshwari.mygooglemaps;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;

/*
*
 * Created by chandana on 19-01-2016.
*/

public class ContactsListAdapter extends BaseAdapter implements Filterable {
    ArrayList<Contacts> names;
    ArrayList<String> checkedContacts = new ArrayList<String>();
    ArrayList<String> position_checked;
    ArrayList<Contacts> mStringFilterList;
    ValueFilter valueFilter;
    boolean[] itemChecked;
    Contacts contacts;
    LoginDataBaseAdapter loginDataBaseAdapter;
    String checked_name;
    SharedPreferences sharedPreferences;
    String checked_phonenumber;
    int count = 0;
    Context context;
    LayoutInflater inflater;

    public ContactsListAdapter(Context context, ArrayList<Contacts> names) {
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
            holder = new Holder();
            holder.Name = (TextView) row.findViewById(R.id.tv_name);
            holder.PhoneNumber = (TextView) row.findViewById(R.id.tv_number);
            holder.ckbox = (CheckBox) row.findViewById(R.id.cb_select);
            row.setTag(holder);
        } else {

            holder = (Holder) row.getTag();
        }
        count++;
        holder.ckbox.setFocusable(false);
        contacts = (Contacts) names.get(position);

/*
        String contactStringArray[] = names.get(position).split("-~-");
*/
        holder.Name.setText(contacts.getName());
        holder.PhoneNumber.setText(contacts.getPhone_num());
        Log.d("inside_adapter_name", "  " + contacts.getName() + " " + contacts.getPhone_num());
        holder.ckbox.setChecked(false);
        try {
            if (itemChecked[position])
                holder.ckbox.setChecked(true);
            else
                holder.ckbox.setChecked(false);
        } catch (Exception e) {

        }
        if (context instanceof BuddiesActivity) {
            if (names.size() != 0) {
                Log.d("iamin buddies_activity", "  " + "haha");
                BuddiesActivity buddiesActivity = new BuddiesActivity();
                buddiesActivity.progress_buddies.setVisibility(View.GONE);
            }
        }

        final Holder finalHolder1 = holder;
        holder.ckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                try {
                    if (finalHolder1.ckbox.isChecked()) {
                        itemChecked[position] = true;
                        String str1[] = {contacts.getName(), contacts.getPhone_num()};
                        checkedContacts.add(str1[0] + "-~-" + str1[1]);
                        Log.d(" checked name", "--->" + names.get(position));
                    } else {
                        itemChecked[position] = false;
                        String contactunCheckedName[] = {contacts.getName(), contacts.getPhone_num()};
                        for (int i = 0; i < checkedContacts.size(); i++) {
                            String str[] = checkedContacts.get(i).split("-~-");
                            if (contactunCheckedName[0].equals(str[0])) {
                                checkedContacts.remove(i);
                            }
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(context.getApplicationContext(), "You are not checked properly", Toast.LENGTH_SHORT).show();
                /*    Intent in = new Intent(context.getApplicationContext(),GoogleMaps.class);
                    in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(in);*/
                }

            }
        });
        return row;
    }

    @Override
    public Filter getFilter() {

        if (valueFilter == null) {
            valueFilter = new ValueFilter();
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
                ArrayList<Contacts> filterList = new ArrayList<Contacts>();
                for (int i = 0; i < mStringFilterList.size(); i++) {
                    if ((mStringFilterList.get(i).getName().toUpperCase())
                            .contains(constraint.toString().toUpperCase())) {

                        Contacts country = new Contacts(mStringFilterList.get(i)
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
            names = (ArrayList<Contacts>) results.values;
            notifyDataSetChanged();
        }

    }
}
