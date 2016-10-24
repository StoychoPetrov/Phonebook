package com.example.stoycho.phonebook.tasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Stoycho on 10/20/2016.
 */

public class DownloadData extends AsyncTask<Void, Void, String> {

    private String mUrl;

    public DownloadData(String url)
    {
        mUrl = url;
    }

    @Override
    protected String doInBackground(Void... params) {
        String dataGetChanges = "";
        URL urlGetData;
        try {
            urlGetData = new URL(mUrl);
            HttpURLConnection urlConnectionGetChanges = (HttpURLConnection) urlGetData.openConnection();
            BufferedReader bufferedReaderGetChanges = new BufferedReader(new InputStreamReader(urlConnectionGetChanges.getInputStream()));
            String nextGetChanges = bufferedReaderGetChanges.readLine();
            while (nextGetChanges != null) {
                dataGetChanges += nextGetChanges;
                nextGetChanges = bufferedReaderGetChanges.readLine();
            }
            urlConnectionGetChanges.disconnect();
            return dataGetChanges;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
