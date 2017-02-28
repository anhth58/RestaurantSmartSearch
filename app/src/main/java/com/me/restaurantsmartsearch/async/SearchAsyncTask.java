package com.me.restaurantsmartsearch.async;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.me.restaurantsmartsearch.utils.Constant;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.parser.Cons;

/**
 * Created by Laptop88T on 11/16/2016.
 */
public class SearchAsyncTask extends AsyncTask<Void, Integer, String> {
    HttpClient httpclient;
    HttpGet httpGet;
    String querryString;
    OnSearchComplete onSearchComplete;
    double latitude;
    double longitude;
    String fields[];

    public SearchAsyncTask(String querry, double lon, double lat, String _fields[], OnSearchComplete _onSearchComplete) {
        querryString = querry;
        latitude = lat;
        longitude = lon;
        onSearchComplete = _onSearchComplete;
        fields = _fields;
    }

    protected String doInBackground(Void... params) {
        httpclient = new DefaultHttpClient();

        JSONObject source = new JSONObject();
        JSONObject query = new JSONObject();
        JSONObject bool = new JSONObject();
        JSONObject must = new JSONObject();
        JSONObject multiMatch = new JSONObject();
        JSONObject filter = new JSONObject();
        JSONObject geoDistance = new JSONObject();
        JSONObject pinLocation = new JSONObject();

        JSONArray jsonArray = new JSONArray();

        for(int i = 0; i <fields.length; i++){
            jsonArray.put(fields[i]);
        }

        try {
            if (latitude != 0 || longitude != 0) {
                pinLocation.put(Constant.LAT, latitude);
                pinLocation.put(Constant.LON, longitude);
                geoDistance.put(Constant.PIN_LOCATION, pinLocation);
                geoDistance.put(Constant.DISTANCE, Constant.DISTANCE_RADIUS);
                filter.put(Constant.GEO_DISTANCE, geoDistance);
                bool.put(Constant.FILTER, filter);
            }

            multiMatch.put(Constant.QUERY, querryString);
            multiMatch.put(Constant.TYPE, Constant.CROSS_FIELDS);
            multiMatch.put(Constant.FIELDS, jsonArray);
            multiMatch.put(Constant.OPERATOR, Constant.OR);
            must.put(Constant.MULTI_MATCH, multiMatch);

            bool.put(Constant.MUST, must);

            query.put(Constant.BOOL, bool);

            source.put(Constant.QUERY, query);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*
        * Query format
        *
        * {
              "query": {
                "bool": {
                  "must": {
                    "multi_match": {
                      "query": "bun cha dong da",
                      "type": "cross_fields",
                      "fields": [
                        "name",
                        "address"
                      ],
                      "operator": "or"
                    }
                  },
                  "filter": {
                    "geo_distance": {
                      "distance": "10km",
                      "pin.location": {
                        "lat": 20.999906919559,
                        "lon": 105.8187568187713
                      }
                    }
                  }
                }
              }
        }
        * */

        List<NameValuePair> paramsURI = new LinkedList<>();
        paramsURI.add((new BasicNameValuePair("source", source.toString())));

        String querry = Constant.IP_SERVER_HTTP + "/" + Constant.INDEX_NAME + "/_search?" + URLEncodedUtils.format(paramsURI, "utf-8");
        httpGet = new HttpGet(querry);
        httpGet.setHeader("Authorization", Constant.AUTHORIZATION);
        httpGet.setHeader("Content-type", "application/json");
        Log.d("#requestString", source.toString());
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
