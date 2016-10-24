package com.example.stoycho.phonebook.activities;

import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stoycho.phonebook.R;
import com.example.stoycho.phonebook.adapters.UsersAdapter;
import com.example.stoycho.phonebook.fragments.CountriesFragment;
import com.example.stoycho.phonebook.fragments.RegistrationFragment;
import com.example.stoycho.phonebook.models.Country;
import com.example.stoycho.phonebook.database.Database;
import com.example.stoycho.phonebook.models.GenderDialog;
import com.example.stoycho.phonebook.models.InformationDialog;
import com.example.stoycho.phonebook.models.User;
import com.example.stoycho.phonebook.tasks.DownloadData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemClickListener,DialogInterface.OnDismissListener {

    private TextView mNewContact;
    private ListView mListWithUsers;
    private List<Country> mCountries;
    private List<User> mUsers;
    private UsersAdapter mUserAdapter;
    private RelativeLayout mSelectCountryLayout;
    private RelativeLayout mSelectGenderLayout;
    private TextView mFilterCountry;
    private TextView mFilterGender;
    private TextView mTitle;
    private GenderDialog mGenderDialog;
    private Country mSelectedFilterCountry;
    private LinearLayout mFilterLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initUI();
        setListeners();
        loadCountries();
        loadUsers();
    }

    private void initUI()
    {
        mNewContact = (TextView) findViewById(R.id.add_user);
        mListWithUsers = (ListView)findViewById(R.id.listForUsers);
        mSelectCountryLayout = (RelativeLayout) findViewById(R.id.select_country);
        mSelectGenderLayout = (RelativeLayout) findViewById(R.id.select_gender);
        mFilterCountry = (TextView) findViewById(R.id.filter_country);
        mFilterGender = (TextView) findViewById(R.id.filter_gender);
        mGenderDialog = new GenderDialog(this);
        mFilterLayout = (LinearLayout) findViewById(R.id.search_bar);
        mTitle = (TextView) findViewById(R.id.title);
    }

    private void setListeners()
    {
        mNewContact.setOnClickListener(this);
        mListWithUsers.setOnItemClickListener(this);
        mSelectCountryLayout.setOnClickListener(this);
        mSelectGenderLayout.setOnClickListener(this);
        mGenderDialog.setOnDismissListener(this);
    }

    private void loadCountries()
    {
        String urlForGetCountries = "https://restcountries.eu/rest/v1/all";
        final Database database = new Database(HomeActivity.this);
        DownloadData data = new DownloadData(urlForGetCountries)
        {
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                try {
                    JSONArray countries = new JSONArray(s);
                    for(int i=0; i < countries.length(); i++)
                    {
                        JSONObject countryInformation = countries.getJSONObject(i);
                        JSONArray countryCodes = countryInformation.getJSONArray("callingCodes");
                        Country country = new Country(countryInformation.getString("name"),countryCodes.length() > 0 ? countryCodes.getString(0) : null);
                        database.insertCountry(country);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        if(database.selectAllCountries(null,Database.SELECT_ALL_COUNTRIES).size() == 0)
            data.execute();
    }

    private void loadUsers()
    {
        mCountries = new ArrayList<>();
        mUsers = new Database(this).selectUsersAndTheirCountries(mCountries,-1,null,null);
        mUserAdapter = new UsersAdapter(this,mUsers,mCountries);
        mListWithUsers.setAdapter(mUserAdapter);
        if(mUsers.size() > 0)
            mFilterLayout.setVisibility(View.VISIBLE);
    }

    public void refreshUsers(User user,Country country)
    {
        mCountries.add(country);
        mUsers.add(user);
        mUserAdapter.setUsersAndCountries(mUsers,mCountries);
        mUserAdapter.notifyDataSetChanged();
        if(mFilterLayout.getVisibility() == View.GONE)
            mFilterLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id)
        {
            case R.id.add_user:
                onAddUser();
                mTitle.setText(getString(R.string.registration));
                break;
            case R.id.select_country:
                selectCountry();
                mTitle.setText(getString(R.string.countries));
                break;
            case R.id.select_gender:
                mGenderDialog.show();
                break;
        }
    }

    private void selectCountry()
    {
        CountriesFragment countriesFragment = new CountriesFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("hasAll",1);
        countriesFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_down,0,0,R.anim.slide_up)
                .add(R.id.replace_layout,countriesFragment).addToBackStack(null).commit();
    }

    private void onAddUser()
    {
        getSupportFragmentManager().beginTransaction().replace(R.id.replace_layout,new RegistrationFragment(),"registerFragment")
                .addToBackStack(null).commit();
    }

    public void updateUser(User user,Country country,int position)
    {
        mUsers.set(position,user);
        mCountries.set(position,country);
        mUserAdapter.setUsersAndCountries(mUsers,mCountries);
        mUserAdapter.notifyDataSetChanged();
    }

    public void setFilterCountry(Country countryName)
    {
        mSelectedFilterCountry = countryName;
        mCountries = new ArrayList<>();
        if(countryName == null)
            mFilterCountry.setText(getString(R.string.all));
        else
            mFilterCountry.setText(countryName.getCountryName());
        String gender = mFilterGender.getText().toString();
        mUsers = new Database(this).selectUsersAndTheirCountries(mCountries,countryName != null ? countryName.getId():-1, !gender.equals("All") ? gender : null,null);
        mUserAdapter.setUsersAndCountries(mUsers, mCountries);
        mUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        InformationDialog dialog = new InformationDialog(this,mCountries.get(position),mUsers.get(position));
        dialog.show();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(((GenderDialog)dialog).getmGender() != null) {
            mFilterGender.setText(((GenderDialog) dialog).getmGender());
            mCountries = new ArrayList<>();
            String gender = mFilterGender.getText().toString();
            int countryId ;
            if(mSelectedFilterCountry != null)
                countryId = mSelectedFilterCountry.getId();
            else
                countryId = -1;
            mUsers = new Database(this).selectUsersAndTheirCountries(mCountries,countryId,!gender.equals("All") ? gender : null,null);
            mUserAdapter.setUsersAndCountries(mUsers,mCountries);
            mUserAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if(getSupportFragmentManager().getBackStackEntryCount() == 0)
            mTitle.setText(getString(R.string.contacts));
    }

    public void deleteUserAlert(final int position)
    {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.deleteEntry))
                .setMessage(getString(R.string.deleteInfo))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser(position);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteUser(int position)
    {
        if(new Database(this).deleteUser(mUsers.get(position).getId())) {
            mUsers.remove(position);
            mCountries.remove(position);
            mUserAdapter.setUsersAndCountries(mUsers,mCountries);
            mUserAdapter.notifyDataSetChanged();
            if(mUsers.size() == 0)
                mFilterLayout.setVisibility(View.GONE);
            Toast.makeText(this, getString(R.string.successDelete), Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(this,getString(R.string.notSuccessDelete),Toast.LENGTH_SHORT).show();
    }

    public void onEdit(int position)
    {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        Bundle bundle = new Bundle();
        User user = mUsers.get(position);
        Country country = mCountries.get(position);
        bundle.putInt("id",user.getId());
        bundle.putString("firstName",user.getFirstName());
        bundle.putString("lastName",user.getLastName());
        bundle.putString("country",country.getCountryName());
        bundle.putString("email",user.getEmail());
        bundle.putString("phone",user.getPhoneNumber());
        bundle.putString("gender",user.getGender());
        bundle.putString("callingCode",country.getCallingCode());
        bundle.putInt("countryId",country.getId());
        bundle.putInt("position",position);
        registrationFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.replace_layout,registrationFragment,"registerFragment")
                .addToBackStack(null).commit();
        mTitle.setText(getString(R.string.editContact));
    }

    public void setmTitle()
    {
        mTitle.setText(getString(R.string.contacts));
    }

    @Override
    public void onResume()
    {
        super.onResume();
        mTitle.setText(getString(R.string.contacts));
    }
}
