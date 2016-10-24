package com.example.stoycho.phonebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.stoycho.phonebook.R;
import com.example.stoycho.phonebook.activities.HomeActivity;
import com.example.stoycho.phonebook.models.Country;
import com.example.stoycho.phonebook.models.User;

import java.util.List;

/**
 * Created by Stoycho on 10/22/2016.
 */

public class UsersAdapter extends BaseAdapter {

    private Context context;
    private List<User> users;
    private List<Country> countries;
    private LayoutInflater inflater;

    public UsersAdapter(Context context, List<User> users, List<Country> countries)
    {
        this.context = context;
        this.users = users;
        this.countries = countries;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = inflater.inflate(R.layout.user_item, parent, false);

        User user = users.get(position);
        Country country = countries.get(position);
        TextView name = (TextView) convertView.findViewById(R.id.user_name);
        TextView phone = (TextView) convertView.findViewById(R.id.user_phone);
        ImageView delete = (ImageView) convertView.findViewById(R.id.delete);
        ImageView edit = (ImageView) convertView.findViewById(R.id.edit);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).deleteUserAlert(position);
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity)context).onEdit(position);
            }
        });

        name.setText(user.getFirstName());
        String phoneNumber = context.getString(R.string.plus) + country.getCallingCode() + user.getPhoneNumber();
        phone.setText(phoneNumber);

        return convertView;
    }

    public void setUsersAndCountries(List<User> users, List<Country> countries)
    {
        this.users = users;
        this.countries = countries;
    }
}
