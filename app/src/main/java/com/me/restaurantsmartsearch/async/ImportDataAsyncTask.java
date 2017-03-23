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

import opennlp.tools.parser.Cons;

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
    String price;

    public ImportDataAsyncTask(int _id, String _name, String _address, String _type, String _description, String _time, double _longitude, double _latitude, String _price) {
        id = _id + 1;
        name = _name;
        address = _address;
        type = _type;
        description = _description;
        time = _time;
        longitude = _longitude;
        latitude = _latitude;
        price = _price;
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
        JSONObject pin = new JSONObject();
        JSONObject location = new JSONObject();
        //"time":"07:00 AM - 10:00 PM "

        String timeInOut[] = time.split("-");
        float timeIn = 0, timeOut = 0;
        int priceMin = 0, priceMax = 0;

        if (timeInOut.length == 2) {
            timeIn = Integer.parseInt(timeInOut[0].substring(0, 2)) + Integer.parseInt(timeInOut[0].substring(3, 5)) / 60;
            if (timeInOut[0].substring(6, 8).equals("PM")) timeIn += 12;
            timeOut = Integer.parseInt(timeInOut[1].substring(0, 2)) + Integer.parseInt(timeInOut[1].substring(3, 5)) / 60;
            if (timeInOut[1].substring(6, 8).equals("PM")) timeOut += 12;
        }

        String priceMinMax[] = AccentRemover.removeAccent(price).split("-");

        if (priceMinMax.length == 2) {
            priceMin = Integer.parseInt(priceMinMax[0].trim().replaceAll("d",""));
            priceMax = Integer.parseInt(priceMinMax[1].trim().replaceAll("d",""));
        }


        try {
            location.put(Constant.LAT, latitude);
            location.put(Constant.LON, longitude);
            pin.put(Constant.LOCATION, location);

            jsonObject.put(Constant.PIN, pin);
            jsonObject.put(Constant.NAME, AccentRemover.removeAccent(name));
            jsonObject.put(Constant.ADDRESS, AccentRemover.removeAccent(address));
            jsonObject.put(Constant.DESCRIPTION, AccentRemover.removeAccent(description));
            jsonObject.put(Constant.TIME, AccentRemover.removeAccent(time));
            jsonObject.put(Constant.TYPE, AccentRemover.removeAccent(type));
            jsonObject.put(Constant.TIME_IN, timeIn);
            jsonObject.put(Constant.TIME_OUT, timeOut);
            jsonObject.put(Constant.MAX_PRICE, priceMax);
            jsonObject.put(Constant.MIN_PRICE, priceMin);
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
