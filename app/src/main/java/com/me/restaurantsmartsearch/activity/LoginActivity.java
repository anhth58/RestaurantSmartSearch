package com.me.restaurantsmartsearch.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.utils.Constant;
import com.me.restaurantsmartsearch.utils.Utils;

/**
 * Created by anhth_58 on 3/23/2017.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private View btnContinue;
    private EditText edUserName, edPassword;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initListener();
    }

    public void initView(){
        btnContinue = findViewById(R.id.btn_continue);
        edUserName = (EditText) findViewById(R.id.et_account);
        edPassword = (EditText) findViewById(R.id.et_password);
        edUserName.setText(Constant.USER_NAME);
    }

    public void initListener(){
        btnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_continue:
                if(isValid()){
                    if(!edUserName.getText().toString().equals(Constant.USER_NAME)){
                        Utils.reportMessage(LoginActivity.this, getResources().getString(R.string.username_invalid));
                    }
                    else if(!edPassword.getText().toString().equals(Constant.PASS_WORD)){
                        Utils.reportMessage(LoginActivity.this, getResources().getString(R.string.password_invalid));
                    }
                    else {
                        showProgressDialog("ĐĂNG NHẬP");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                            }
                        },1000);
                    }
                }
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

    public boolean isValid(){
        if(TextUtils.isEmpty(edUserName.getText())){
            Utils.reportMessage(LoginActivity.this, getResources().getString(R.string.username_empty));
            return false;
        }
        else if (TextUtils.isEmpty(edPassword.getText())){
            Utils.reportMessage(LoginActivity.this, getResources().getString(R.string.password_empty));
            return false;
        }
        else if(edPassword.getText().length() < 6){
            Utils.reportMessage(LoginActivity.this, getResources().getString(R.string.password_validate));
            return false;
        }
        return true;
    }
}
