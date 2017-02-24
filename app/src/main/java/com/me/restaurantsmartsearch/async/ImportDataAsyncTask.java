package com.me.restaurantsmartsearch.async;

import android.os.AsyncTask;
import android.util.Log;

import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Laptop88T on 11/16/2016.
 */
public class ImportDataAsyncTask extends AsyncTask<Void, Integer, String> {
    HttpClient httpclient;
    HttpPut httpPut;
    int id;
    String name;
    String address;
    String type;
    String description;
    String time;
    double longitude;
    double latitude;

    public ImportDataAsyncTask(int _id, String _name, String _address, String _type, String _description, String _time, double _longitude, double _latitude) {
        id = _id + 1;
        name = _name;
        address = _address;
        type = _type;
        description = _description;
        time = _time;
        longitude = _longitude;
        latitude = _latitude;
    }

    protected String doInBackground(Void... params) {
        httpclient = new DefaultHttpClient();
        httpPut = new HttpPut(Constant.API_IMPORT_DATA_HTTP + id);
        httpPut.setHeader("Authorization", Constant.AUTHORIZATION);
        httpPut.setHeader("Content-type", "application/json");
        JSONObject jsonObject = new JSONObject();
        JSONObject jsonSuggest = new JSONObject();
        JSONObject jsonSuggest1 = new JSONObject();
        JSONArray suggest = new JSONArray();
        try {
            jsonObject.put(Constant.NAME, AccentRemover.removeAccent(name));
            jsonObject.put(Constant.ADDRESS, AccentRemover.removeAccent(address));
            jsonObject.put(Constant.DESCRIPTION, AccentRemover.removeAccent(description));
            jsonObject.put(Constant.TIME, AccentRemover.removeAccent(time));
            jsonObject.put(Constant.TYPE, AccentRemover.removeAccent(type));
            jsonObject.put(Constant.LONGITUDE, longitude);
            jsonObject.put(Constant.LATITUDE, latitude);
            jsonSuggest.put(Constant.INPUT, name);
            jsonSuggest.put(Constant.WEIGHT, id + 1000);
            jsonSuggest1.put(Constant.INPUT, AccentRemover.removeAccent(name));
            jsonSuggest1.put(Constant.WEIGHT, id + 10);
            suggest.put(jsonSuggest);
            suggest.put(jsonSuggest1);
            jsonObject.put(Constant.SUGGEST, suggest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            Log.d("#requestString", jsonObject.toString());
            httpPut.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String responseString = "";
        try {
            HttpResponse httpResponse = httpclient.execute(httpPut);
            HttpEntity r_entity = httpResponse.getEntity();

            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                // Server response
                responseString = EntityUtils.toString(r_entity);
            } else {
                responseString = "Http Status Code: " + statusCode;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
