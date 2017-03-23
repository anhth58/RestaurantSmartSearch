package com.me.restaurantsmartsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.model.Pin;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.utils.Constant;
import com.me.restaurantsmartsearch.utils.RealmBackupRestore;

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
            //importDataFromJSONFile();
            RealmBackupRestore realmBackupRestore = new RealmBackupRestore(SplashScreen.this);
            realmBackupRestore.restore();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
            },1500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                }
            },1500);
        }
    }

    public void importDataFromJSONFile() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Realm.init(SplashScreen.this);
                    Realm realm = Realm.getDefaultInstance();
                    for(int k = 1;  k <7; k++){
                        JSONArray jsonArray = new JSONArray(loadJSONFromAsset(k));
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Restaurant restaurant = new Restaurant();
                            restaurant.setId(getNextKey(realm));
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
                            Log.d("imported", restaurant.getId() + " ");
                            realm.commitTransaction();
                        }
                    }

                    JSONArray arrayLocation = new JSONArray(loadJSONLocationFromAsset());
                    Log.d("Size Location",arrayLocation.length()+"");
                    for (int i = 0; i < arrayLocation.length(); i++) {
                        Pin pin = new Pin();
                        pin.setId(i);
                        pin.setLocation(arrayLocation.getJSONObject(i).optString(Constant.LOCATION));
                        pin.setLatitude(arrayLocation.getJSONObject(i).optDouble(Constant.LATITUDE));
                        pin.setLongitude(arrayLocation.getJSONObject(i).optDouble(Constant.LONGITUDE));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(pin);
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

    public String loadJSONFromAsset(int index) {
        String json = null;
        try {
            InputStream is = getAssets().open("restaurant_realm_"+index+".json");
            Log.d("file","restaurant_realm_"+index+".json");
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

    public String loadJSONLocationFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data_location.json");
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
    public int getNextKey(Realm realm) {
        if(realm.isEmpty()) return 0;
        try {
            return realm.where(Restaurant.class).max("id").intValue() + 1;
        } catch (Exception e) {
            return 0;
        }
    }
}
