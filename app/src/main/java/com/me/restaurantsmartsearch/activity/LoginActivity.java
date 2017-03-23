package com.me.restaurantsmartsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;

import com.me.restaurantsmartsearch.R;

/**
 * Created by anhth_58 on 3/23/2017.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private View btnContinue;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    public void initView(){
        btnContinue = findViewById(R.id.btn_continue);
    }

    public void initListener(){
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue:
                showProgressDialog("ĐĂNG NHẬP");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    }
                },1000);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }
}
