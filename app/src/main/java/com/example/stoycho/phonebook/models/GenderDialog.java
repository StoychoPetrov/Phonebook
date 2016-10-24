package com.example.stoycho.phonebook.models;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.stoycho.phonebook.R;

/**
 * Created by Stoycho on 10/22/2016.
 */

public class GenderDialog extends Dialog implements View.OnClickListener{

    private Context mContext;
    private String mGender;
    private TextView all;
    private TextView male;
    private TextView female;

    public GenderDialog(Context context) {
        super(context);
        mContext = context;
    }

    public GenderDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected GenderDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.gender_dialog);

        all = (TextView) findViewById(R.id.all);
        male = (TextView) findViewById(R.id.male);
        female = (TextView) findViewById(R.id.female);
        RelativeLayout allLayout = (RelativeLayout) findViewById(R.id.allLayout);
        RelativeLayout maleLayout = (RelativeLayout) findViewById(R.id.maleLayout);
        RelativeLayout femaleLayout = (RelativeLayout) findViewById(R.id.femaleLayout);
        allLayout.setOnClickListener(this);
        maleLayout.setOnClickListener(this);
        femaleLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id)
        {
            case R.id.allLayout:
                mGender = all.getText().toString();
                break;
            case R.id.maleLayout:
                mGender = male.getText().toString();
                break;
            case R.id.femaleLayout:
                mGender = female.getText().toString();
                break;
        }
        dismiss();
    }

    public String getmGender()
    {
        return mGender;
    }
}
