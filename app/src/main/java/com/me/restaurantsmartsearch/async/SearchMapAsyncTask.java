package com.me.restaurantsmartsearch.async;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;
import com.me.restaurantsmartsearch.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Laptop88T on 11/16/2016.
 */
public class SearchMapAsyncTask extends AsyncTask<Void, Integer, String> {
    HttpClient httpclient;
    HttpGet httpGet;
    OnSearchComplete onSearchComplete;
    LatLng southwest, northeast;

    public SearchMapAsyncTask(LatLng _southwest, LatLng _northeast, OnSearchComplete _onSearchComplete) {
        onSearchComplete = _onSearchComplete;
        this.southwest = _southwest;
        this.northeast = _northeast;
    }

    protected String doInBackground(Void... params) {
        httpclient = new DefaultHttpClient();
        String query = "https://map.coccoc.com/map/search.json?category=1&borders="+southwest.latitude+"%2C"+southwest.longitude+"%2C"+northeast.latitude+"%2C"+northeast.longitude;
        httpGet = new HttpGet(query);
        String responseString = "";
        try {
            HttpResponse httpResponse = httpclient.execute(httpGet);
            HttpEntity r_entity = httpResponse.getEntity();
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            responseString = EntityUtils.toString(r_entity);
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
