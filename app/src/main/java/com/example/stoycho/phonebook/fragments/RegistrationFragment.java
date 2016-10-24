package com.example.stoycho.phonebook.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stoycho.phonebook.R;
import com.example.stoycho.phonebook.activities.HomeActivity;
import com.example.stoycho.phonebook.models.Country;
import com.example.stoycho.phonebook.database.Database;
import com.example.stoycho.phonebook.models.User;

import java.util.ArrayList;
import java.util.List;

public class RegistrationFragment extends Fragment implements View.OnClickListener, View.OnFocusChangeListener {

    private EditText mFirstName;
    private EditText mLastName;
    private EditText mCountry;
    private EditText mEmail;
    private EditText mPhoneNumber;
    private TextView mCallingCode;
    private RadioButton mMale;
    private RadioButton mFemale;
    private Button mAdd;
    private int mCountryId;
    private String mPhoneCode;
    private boolean mHasEmailError;
    private boolean mHasPhoneError;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_registration, container, false);
        initUI(root);
        setListeners();
        if(getArguments() != null && getArguments().containsKey("id"))
            setInformations();
        // Inflate the layout for this fragment
        return root;
    }

    private void initUI(View root)
    {
        mFirstName = (EditText)root.findViewById(R.id.first_name);
        mLastName = (EditText)root.findViewById(R.id.last_name);
        mCountry = (EditText)root.findViewById(R.id.country);
        mEmail = (EditText)root.findViewById(R.id.email);
        mPhoneNumber = (EditText)root.findViewById(R.id.phone_number);
        mCallingCode = (TextView) root.findViewById(R.id.callingCode);
        mMale = (RadioButton)root.findViewById(R.id.male);
        mFemale = (RadioButton)root.findViewById(R.id.female);
        mAdd = (Button)root.findViewById(R.id.add);
    }

    private void setListeners()
    {
        mAdd.setOnClickListener(this);
        mCountry.setOnClickListener(this);
        mEmail.setOnFocusChangeListener(this);
        mPhoneNumber.setOnFocusChangeListener(this);
    }

    private void setInformations()
    {
        Bundle bundle = getArguments();
        mFirstName.setText(bundle.getString("firstName"));
        mLastName.setText(bundle.getString("lastName"));
        mEmail.setText(bundle.getString("email"));
        mCountry.setText(bundle.getString("country"));
        mPhoneNumber.setText(bundle.getString("phone"));
        mPhoneCode = getArguments().getString("callingCode");
        String code = getString(R.string.plus) + getArguments().getString("callingCode") + " ";
        mCountryId = getArguments().getInt("countryId");
        mCallingCode.setText(code);
        if(bundle.getString("gender").equals("Male"))
            mMale.setChecked(true);
        else
            mFemale.setChecked(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id)
        {
            case R.id.add:
                mPhoneNumber.clearFocus();
                mEmail.clearFocus();
                if(getArguments() == null)
                    onAdd();
                else
                    onEdit();
                break;
            case R.id.country:
                onCountry();
                break;
        }
    }

    private void onEdit()
    {
        if(!mFirstName.getText().toString().equals("") && !mLastName.getText().toString().equals("") && !mCountry.getText().toString().equals("")
                && !mEmail.getText().toString().equals("") && !mHasEmailError && !mHasPhoneError && (mMale.isChecked() || mFemale.isChecked()))
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.updateInfo)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            updateUser();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                        }
                    });
            builder.create();
            builder.show();
        }
        else
            Toast.makeText(getActivity(),getString(R.string.registrationError),Toast.LENGTH_SHORT).show();
    }

    private void updateUser()
    {
        User user = new User(mFirstName.getText().toString(),mLastName.getText().toString(),mCountryId,mEmail.getText().toString()
                ,mPhoneNumber.getText().toString(),mMale.isChecked() ? getString(R.string.male):getString(R.string.female));
        Country country = new Country(mCountry.getText().toString(),mPhoneCode);
        int userId = getArguments().getInt("id");
        user.setId(userId);
        if(new Database(getActivity()).updateUser(user)) {
            Toast.makeText(getActivity(), R.string.successUpdate, Toast.LENGTH_SHORT).show();
            ((HomeActivity)getActivity()).updateUser(user,country,getArguments().getInt("position"));
            getFragmentManager().popBackStack();
            ((HomeActivity)getActivity()).setmTitle();
        }
        else
            Toast.makeText(getActivity(),R.string.notSuccessEdit,Toast.LENGTH_SHORT).show();
    }

    private void onAdd()
    {
        if(!mFirstName.getText().toString().equals("") && !mLastName.getText().toString().equals("") && !mCountry.getText().toString().equals("")
                && !mEmail.getText().toString().equals("") && !mHasEmailError && !mHasPhoneError && (mMale.isChecked() || mFemale.isChecked()))
        {
            List<User> users = new Database(getActivity()).selectUsersAndTheirCountries(new ArrayList<Country>(),-1,null,mPhoneNumber.getText().toString());
            if(users.size() == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(R.string.message_for_dialog)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                registerUser();
                            }
                        })
                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        });
                builder.create();
                builder.show();
            }
            else
                Toast.makeText(getActivity(),getString(R.string.sameNumber),Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser()
    {
        User user = new User(mFirstName.getText().toString(),mLastName.getText().toString(),mCountryId,mEmail.getText().toString()
                ,mPhoneNumber.getText().toString(),mMale.isChecked() ? getString(R.string.male):getString(R.string.female));
        Country country = new Country(mCountry.getText().toString(),mPhoneCode);
        long id = new Database(getActivity()).insertUser(user);
        if(id != -1) {
            user.setId((int) id);
            Toast.makeText(getActivity(), R.string.message_for_register, Toast.LENGTH_SHORT).show();
            ((HomeActivity)getActivity()).refreshUsers(user,country);
            getFragmentManager().popBackStack();
            ((HomeActivity)getActivity()).setmTitle();
        }
        else
            Toast.makeText(getActivity(),R.string.register_error,Toast.LENGTH_SHORT).show();
    }

    private void onCountry()
    {
        View viewFocus = getActivity().getCurrentFocus();
        if (viewFocus != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewFocus.getWindowToken(), 0);
        }
        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_down,0,0,R.anim.slide_up)
                .add(R.id.replace_layout,new CountriesFragment(),"countryFragment").addToBackStack(null).commit();
    }

    private boolean emailValidation(String email) {
        return email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean phoneValidation()
    {
        return PhoneNumberUtils.isGlobalPhoneNumber(mPhoneNumber.getText().toString());
    }

    public void setSelectedCountry(Country country)
    {
        mCountry.setText(country.getCountryName());
        mPhoneCode = country.getCallingCode();
        String code = getString(R.string.plus) + mPhoneCode + " ";
        mCallingCode.setText(code);
        mCountryId = country.getId();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        switch (id)
        {
            case R.id.email:
                if(!hasFocus)
                    checkEmailValidation();
                break;
            case R.id.phone_number:
                if(!hasFocus)
                    checkPhoneValidation();
                break;
        }
    }

    private void checkPhoneValidation()
    {
        if(!phoneValidation())
        {
            mPhoneNumber.setError(getString(R.string.phone_error));
            mHasPhoneError = true;
        }
        else
            mHasPhoneError = false;
    }

    private void checkEmailValidation()
    {
        if(!emailValidation(mEmail.getText().toString()))
        {
            mEmail.setError(getString(R.string.email_error));
            mHasEmailError = true;
        }
        else
            mHasEmailError = false;
    }
}
