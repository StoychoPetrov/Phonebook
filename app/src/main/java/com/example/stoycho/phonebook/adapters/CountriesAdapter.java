package com.example.stoycho.phonebook.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.stoycho.phonebook.R;
import com.example.stoycho.phonebook.models.Country;

import java.util.List;

/**
 * Created by Stoycho on 10/21/2016.
 */

public class CountriesAdapter extends BaseAdapter {

    private Context context;
    private List<Country> countries;
    private LayoutInflater inflater;

    public CountriesAdapter(Context context,List<Country> countries)
    {
        this.context = context;
        this.countries = countries;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return countries.size();
    }

    @Override
    public Object getItem(int position) {
        return countries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null)
            convertView = inflater.inflate(R.layout.country_item, parent, false);
        Country country = countries.get(position);
        TextView countryName = (TextView) convertView.findViewById(R.id.countryName);
        TextView callingCode = (TextView) convertView.findViewById(R.id.callingCode);

        String code = null;
        if(country.getCallingCode() != null)
            code = "(+" + country.getCallingCode() + ")";
        countryName.setText(country.getCountryName());
        callingCode.setText(code);

        return convertView;
    }

    public void setCountries(List<Country> countries)
    {
        this.countries = countries;
    }
}
