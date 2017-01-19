package com.me.restaurantsmartsearch.async;

import android.os.AsyncTask;
import android.util.Log;

import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Laptop88T on 11/16/2016.
 */
public class SearchAsyncTask extends AsyncTask<Void, Integer, String> {
    HttpClient httpclient;
    HttpGet httpGet;
    String querryString;
    OnSearchComplete onSearchComplete;

    public SearchAsyncTask(String querry, OnSearchComplete _onSearchComplete) {
        querryString = querry;
        onSearchComplete = _onSearchComplete;
    }

    protected String doInBackground(Void... params) {
        httpclient = new DefaultHttpClient();
        String arr[] = querryString.trim().split(" ");
        String s = "";
        for (int i = 0; i < arr.length; i++) {
            if (i < arr.length - 1) s += arr[i] + "+";
            else s += arr[i];
        }
        String querry = Constant.IP_SERVER_HTTP + "/mydb4/_search?q=name%3A(" + s + ")";
        httpGet = new HttpGet(querry);
        httpGet.setHeader("Authorization", Constant.AUTHORIZATION);
        httpGet.setHeader("Content-type", "application/json");

        String responseString = "";
        try {
            HttpResponse httpResponse = httpclient.execute(httpGet);
            HttpEntity r_entity = httpResponse.getEntity();

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Error occurred! Http Status Code: " + statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String response) {
        onSearchComplete.onSearchComplete(response);
        super.onPostExecute(response);
    }

    public interface OnSearchComplete {
        public void onSearchComplete(String s);
    }
}
