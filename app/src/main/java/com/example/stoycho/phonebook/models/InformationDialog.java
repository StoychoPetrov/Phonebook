package com.example.stoycho.phonebook.models;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.example.stoycho.phonebook.R;

/**
 * Created by Stoycho on 10/22/2016.
 */

public class InformationDialog extends Dialog {

    private Context context;
    private Country mCountry;
    private User mUser;

    protected InformationDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public InformationDialog(Context context,Country country, User user) {
        super(context);
        this.context = context;
        this.mCountry = country;
        this.mUser = user;
    }

    public InformationDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.information_dialog);
        TextView firstName = (TextView) findViewById(R.id.first_name);
        TextView lastName = (TextView) findViewById(R.id.last_name);
        TextView country = (TextView) findViewById(R.id.country);
        TextView email = (TextView) findViewById(R.id.email);
        TextView phoneNumber = (TextView) findViewById(R.id.phone_number);
        TextView gender = (TextView) findViewById(R.id.gender);

        String phone = context.getString(R.string.plus) + mCountry.getCallingCode() + mUser.getPhoneNumber();
        firstName.setText(mUser.getFirstName());
        lastName.setText(mUser.getLastName());
        country.setText(mCountry.getCountryName());
        email.setText(mUser.getEmail());
        phoneNumber.setText(phone);
        gender.setText(mUser.getGender());

    }
}
