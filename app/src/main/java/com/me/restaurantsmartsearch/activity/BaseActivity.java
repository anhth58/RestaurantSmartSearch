package com.me.restaurantsmartsearch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.me.restaurantsmartsearch.fragment.ProgressDialogFragment;
import com.me.restaurantsmartsearch.utils.Constant;

/**
 * Created by anhth_58 on 3/23/2017.
 */

public abstract class BaseActivity extends FragmentActivity {
    private ProgressDialogFragment progressDialogFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showProgressDialog(int id) {
        String text = getString(id);
        showProgressDialog(text,true);
    }

    public void showProgressDialog(String text) {
        showProgressDialog(text,true);
    }

    protected void showProgressDialog(String str, boolean haveCancel) {
        try {
            progressDialogFragment = ProgressDialogFragment.newInstance(null, str, haveCancel);
            progressDialogFragment.show(getSupportFragmentManager(), Constant.SERVICE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dismissProgressDialog() {
        try {
            if (progressDialogFragment != null)
                progressDialogFragment.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
