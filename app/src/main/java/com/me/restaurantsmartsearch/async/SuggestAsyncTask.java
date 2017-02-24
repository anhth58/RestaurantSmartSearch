package com.me.restaurantsmartsearch.async;

import android.os.AsyncTask;

import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
public class SuggestAsyncTask extends AsyncTask<Void, Integer, String> {
    HttpClient httpclient;
    HttpPost httpGet;
    String querryString;
    OnSuggestComplete onSuggestComplete;

    public SuggestAsyncTask(String querry, OnSuggestComplete _onSuggestComplete) {
        querryString = querry;
        onSuggestComplete = _onSuggestComplete;
    }

    protected String doInBackground(Void... params) {
        httpclient = new DefaultHttpClient();
        String arr[] = querryString.trim().split(" ");
        String s = "";
        for (int i = 0; i < arr.length; i++) {
            if (i < arr.length - 1) s += arr[i] + "+";
            else s += arr[i];
        }
        String querry = Constant.IP_SERVER_HTTP + "/" + Constant.INDEX_NAME + "/_suggest?pretty";
        httpGet = new HttpPost(querry);
        httpGet.setHeader("Authorization", Constant.AUTHORIZATION);
        httpGet.setHeader("Content-type", "application/json");

        JSONObject jsonCompletion = new JSONObject();
        JSONObject jsonSuggest = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonCompletion.put(Constant.FIELD, Constant.SUGGEST);
            jsonSuggest.put(Constant.PREFIX, AccentRemover.removeAccent(querryString));
            jsonSuggest.put(Constant.COMPLETION, jsonCompletion);
            jsonObject.put(Constant.RESTAURANT_SUGGEST, jsonSuggest);
            StringEntity entity = new StringEntity(jsonObject.toString(), "UTF-8");
            httpGet.setEntity(entity);
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
        }
        String responseString = "";
        try {
            HttpResponse httpResponse = httpclient.execute(httpGet);
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
    protected void onPostExecute(String response) {
        onSuggestComplete.onSuggestComplete(response);
        super.onPostExecute(response);
    }

    public interface OnSuggestComplete {
        void onSuggestComplete(String s);
    }
}
