package com.me.restaurantsmartsearch.async;

import android.os.AsyncTask;
import android.util.Log;

import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
            jsonObject.put("name", AccentRemover.removeAccent(name));
            jsonObject.put("address", AccentRemover.removeAccent(address));
            jsonObject.put("description", AccentRemover.removeAccent(description));
            jsonObject.put("time", AccentRemover.removeAccent(time));
            jsonObject.put("type", AccentRemover.removeAccent(type));
            jsonObject.put("longitude", longitude);
            jsonObject.put("latitude", latitude);
            jsonSuggest.put("input",name);
            jsonSuggest.put("weight",id+1000);
            jsonSuggest1.put("input",AccentRemover.removeAccent(name));
            jsonSuggest1.put("weight",id + 10);
            suggest.put(jsonSuggest);
            suggest.put(jsonSuggest1);
            jsonObject.put("suggest", suggest);
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
                responseString = "Error occurred! Http Status Code: " + statusCode;
            }
            Log.d("UploadFileToServer", "Response: " + responseString);
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
