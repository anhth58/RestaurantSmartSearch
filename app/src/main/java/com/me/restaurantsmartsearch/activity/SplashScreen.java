package com.me.restaurantsmartsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import io.realm.Realm;

/**
 * Created by Laptop88T on 2/16/2017.
 */
public class SplashScreen extends Activity {
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        doSomethingNecessary();
    }

    public void doSomethingNecessary() {
        Realm.init(SplashScreen.this);
        realm = Realm.getDefaultInstance();
        if (realm.isEmpty()) {
            importDataFromJSONFile();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
                }
            }).run();
        }
    }

    public void importDataFromJSONFile() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Realm.init(SplashScreen.this);
                    Realm realm = Realm.getDefaultInstance();
                    JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(i);
                        restaurant.setName(jsonArray.getJSONObject(i).optString(Constant.NAME));
                        restaurant.setAddress(jsonArray.getJSONObject(i).optString(Constant.ADDRESS));
                        restaurant.setTime(jsonArray.getJSONObject(i).optString(Constant.TIME));
                        restaurant.setType(jsonArray.getJSONObject(i).optString(Constant.TYPE));
                        restaurant.setDescription(jsonArray.getJSONObject(i).optString(Constant.DESCRIPTION));
                        restaurant.setLatitude(jsonArray.getJSONObject(i).optDouble(Constant.LATITUDE));
                        restaurant.setLongitude(jsonArray.getJSONObject(i).optDouble(Constant.LONGITUDE));
                        restaurant.setImage(jsonArray.getJSONObject(i).optString(Constant.IMAGE));
                        restaurant.setPrice(jsonArray.getJSONObject(i).optString(Constant.PRICE));
                        restaurant.setpCost(jsonArray.getJSONObject(i).getJSONObject(Constant.POINT).optDouble(Constant.P_COST));
                        restaurant.setpLocation(jsonArray.getJSONObject(i).getJSONObject(Constant.POINT).optDouble(Constant.P_LOCATION));
                        restaurant.setpSpace(jsonArray.getJSONObject(i).getJSONObject(Constant.POINT).optDouble(Constant.P_SPACE));
                        restaurant.setpServe(jsonArray.getJSONObject(i).getJSONObject(Constant.POINT).optDouble(Constant.P_SERVE));
                        restaurant.setpQuality(jsonArray.getJSONObject(i).getJSONObject(Constant.POINT).optDouble(Constant.P_QUALITY));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(restaurant);
                        realm.commitTransaction();
                    }
                    startActivity(new Intent(SplashScreen.this, MainActivity.class));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
