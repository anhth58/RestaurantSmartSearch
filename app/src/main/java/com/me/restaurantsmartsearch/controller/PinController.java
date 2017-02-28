package com.me.restaurantsmartsearch.controller;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import com.me.restaurantsmartsearch.model.Pin;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Laptop88T on 11/15/2016.
 */
public class PinController {
    private static PinController instance;
    private final Realm realm;

    public PinController(Application application) {
        realm = Realm.getDefaultInstance();
    }

    public static PinController with(Fragment fragment) {

        if (instance == null) {
            instance = new PinController(fragment.getActivity().getApplication());
        }
        return instance;
    }

    public static PinController with(Activity activity) {

        if (instance == null) {
            instance = new PinController(activity.getApplication());
        }
        return instance;
    }

    public static PinController with(Application application) {

        if (instance == null) {
            instance = new PinController(application);
        }
        return instance;
    }

    public static PinController getInstance() {

        return instance;
    }

    public Realm getRealm() {
        return realm;
    }


    public void clearAll() {
        realm.beginTransaction();
        realm.delete(Pin.class);
        realm.commitTransaction();
    }

    public RealmResults<Pin> getAllPin() {
        return realm.where(Pin.class).findAll();
    }

    public Pin getPinById(String id) {
        return realm.where(Pin.class).equalTo("id", id).findFirst();
    }

    public Pin getPinByLocation(String location){
        return realm.where(Pin.class).contains("location",location).findFirst();
    }
}
