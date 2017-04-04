package com.me.restaurantsmartsearch.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.me.restaurantsmartsearch.R;

/**
 * Created by anhth_58 on 3/28/2017.
 */

public class SortDialog extends Dialog implements View.OnClickListener {
    private onClickChooseAction mListener;
    public SortDialog(Context context, onClickChooseAction listener) {
        super(context, R.style.Theme_AppCompat_Dialog);
        setContentView(R.layout.dialog_sort);
        this.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        setCanceledOnTouchOutside(true);
        this.mListener = listener;
        findViewById(R.id.v_sort_name).setOnClickListener(this);
        findViewById(R.id.v_sort_distance).setOnClickListener(this);
        findViewById(R.id.v_cancel).setOnClickListener(this);
        findViewById(R.id.v_sort_price_cheap).setOnClickListener(this);
        findViewById(R.id.v_sort_price_ex).setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.v_sort_name:
                mListener.onSortName();
                dismiss();
                break;
            case R.id.v_sort_distance:
                mListener.onSortDistance();
                dismiss();
                break;
            case R.id.v_cancel:
                dismiss();
                break;
            case R.id.v_sort_price_cheap:
                mListener.onSortPrice();
                dismiss();
                break;
            case R.id.v_sort_price_ex:
                mListener.onSortPriceEx();
                dismiss();
                break;
            default:
                break;
        }
    }

    public interface onClickChooseAction {
        public void onSortName();

        public void onSortDistance();

        public void onSortPrice();

        public void onSortPriceEx();
    }
}