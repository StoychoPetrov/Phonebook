package com.example.stoycho.phonebook.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.stoycho.phonebook.R;
import com.example.stoycho.phonebook.activities.HomeActivity;
import com.example.stoycho.phonebook.adapters.CountriesAdapter;
import com.example.stoycho.phonebook.models.Country;
import com.example.stoycho.phonebook.database.Database;

import java.util.List;

public class CountriesFragment extends Fragment implements TextWatcher, AdapterView.OnItemClickListener,View.OnClickListener {

    private EditText mSearch;
    private ListView mCountriesList;
    private List<Country> mCountries;
    private CountriesAdapter mAdapter;
    private TextView mAll;
    private View mDivider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_countries, container, false);

        initUI(root);
        setListeners();
        if(getArguments() == null) {
            mAll.setVisibility(View.GONE);
            mDivider.setVisibility(View.GONE);
        }
        loadCountriesList();
        return root;
    }

    private void initUI(View root)
    {
        mSearch = (EditText)root.findViewById(R.id.search);
        mCountriesList = (ListView)root.findViewById(R.id.countriesList);
        mAll = (TextView) root.findViewById(R.id.all);
        mDivider = root.findViewById(R.id.divider);
    }

    private void setListeners()
    {
        mSearch.addTextChangedListener(this);
        mCountriesList.setOnItemClickListener(this);
        mAll.setOnClickListener(this);
    }

    private void loadCountriesList()
    {
        mCountries = new Database(getActivity()).selectAllCountries(null,Database.SELECT_ALL_COUNTRIES);
        mAdapter = new CountriesAdapter(getActivity(),mCountries);
        mCountriesList.setAdapter(mAdapter);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mCountries = new Database(getActivity()).selectAllCountries(s.toString(),Database.SELECT_SEARCH_PLACES);
        mAdapter.setCountries(mCountries);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        List<Fragment> fragments = getFragmentManager().getFragments();
        if(fragments.size() > 1) {
            Fragment fragment = fragments.get(getFragmentManager()
                    .getFragments().size() - 2);
            if (fragment instanceof RegistrationFragment)
                ((RegistrationFragment) fragment).setSelectedCountry(mCountries.get(position));
        }
        else if(fragments.size() == 1)
            ((HomeActivity)getActivity()).setFilterCountry(mCountries.get(position));

        getFragmentManager().popBackStack();
        View viewFocus = getActivity().getCurrentFocus();
        if (viewFocus != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.all:
                selectAll();
                break;
        }
    }

    private void selectAll()
    {
        ((HomeActivity)getActivity()).setFilterCountry(null);
        getFragmentManager().popBackStack();
    }
}
