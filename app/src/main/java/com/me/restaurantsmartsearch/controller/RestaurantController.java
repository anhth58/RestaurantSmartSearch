package com.me.restaurantsmartsearch.controller;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import com.me.restaurantsmartsearch.model.Restaurant;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Laptop88T on 11/15/2016.
 */
public class RestaurantController {
    private static RestaurantController instance;
    private final Realm realm;

    public RestaurantController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static RestaurantController with(Fragment fragment) {

        if (instance == null) {
            instance = new RestaurantController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static RestaurantController with(Activity activity) {

        if (instance == null) {
            instance = new RestaurantController(activity.getApplication());
        }
        return instance;
    }

    public static RestaurantController with(Application application) {

        if (instance == null) {
            instance = new RestaurantController(application);
        }
        return instance;
    }

    public static RestaurantController getInstance() {

        return instance;
    }

    public Realm getRealm() {

        return realm;
    }


    //clear all objects from Book.class
    public void clearAll() {

        realm.beginTransaction();
        realm.delete(Restaurant.class);
        realm.commitTransaction();
    }

    //find all objects in the Book.class
    public RealmResults<Restaurant> getAllRestaurant() {

        return realm.where(Restaurant.class).findAll();
    }

    //query a single item with the given id
    public Restaurant getRestaurantById(String id) {

        return realm.where(Restaurant.class).equalTo("id", id).findFirst();
    }
}
