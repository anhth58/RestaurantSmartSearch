package com.me.restaurantsmartsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;

/**
 * Created by Laptop88T on 2/16/2017.
 */
public class SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Realm.init(SplashScreen.this);
        Realm realm = Realm.getDefaultInstance();
        if(realm.isEmpty()){
            importDataFromJSONFile();
        }
        else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                }
            }).run();
        }
    }

    public void importDataFromJSONFile(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Realm.init(SplashScreen.this);
                    Realm realm = Realm.getDefaultInstance();
                    JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
                    Log.d("length", jsonArray.length() + "");
                    for(int i = 0; i < jsonArray.length();i++){
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(i);
                        restaurant.setName(jsonArray.getJSONObject(i).optString("name"));
                        restaurant.setAddress(jsonArray.getJSONObject(i).optString("address"));
                        restaurant.setTime(jsonArray.getJSONObject(i).optString("time"));
                        restaurant.setType(jsonArray.getJSONObject(i).optString("type"));
                        restaurant.setDescription(jsonArray.getJSONObject(i).optString("description"));
                        restaurant.setLatitude(jsonArray.getJSONObject(i).optDouble("latitude"));
                        restaurant.setLongitude(jsonArray.getJSONObject(i).optDouble("longitude"));
                        restaurant.setImage(jsonArray.getJSONObject(i).optString("image"));
                        restaurant.setPrice(jsonArray.getJSONObject(i).optString("price"));
                        restaurant.setpCost(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pCost"));
                        restaurant.setpLocation(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pLocation"));
                        restaurant.setpSpace(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pSpace"));
                        restaurant.setpServe(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pServe"));
                        restaurant.setpQuality(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pQuality"));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(restaurant);
                        realm.commitTransaction();
                    }
                    startActivity(new Intent(SplashScreen.this,MainActivity.class));
                } catch (JSONException e) {
                    e.printStackTrace();
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
            }
        });
        t.run();
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
