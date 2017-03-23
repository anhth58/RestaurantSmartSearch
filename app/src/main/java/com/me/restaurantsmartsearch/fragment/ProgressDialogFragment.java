package com.me.restaurantsmartsearch.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.utils.Common;

/**
 * Created by anhth_58 on 3/23/2017.
 */

public class ProgressDialogFragment extends DialogFragment {
    private static final String TAG = "ProgressDialogFragment";
    private TextView mTvtitle, mTvMessgae;
    protected String mStringTitle, mStringMessage;
    protected ProgressBar mProgress;

    public static ProgressDialogFragment newInstance(String title, String message, boolean haveCancel) {
        ProgressDialogFragment f = new ProgressDialogFragment();
        f.mStringTitle = title;
        f.mStringMessage = message;
        f.setCancelable(haveCancel);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setStyle(DialogFragment.STYLE_NO_TITLE, R.style.BChat_Loading);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            if(!getResources().getBoolean(R.bool.portrait_only)){
                int width = (int) (Common.screenSize.x * 0.3);
                dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            }else {
                int width = (int) (Common.screenSize.x * 0.8);
                dialog.getWindow().setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View v = initControl(inflater, container);
        initEvent();
        return v;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        try {
            mProgress.setEnabled(false);
            mProgress.setVisibility(View.INVISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "dismiss", e);
        }
        super.onDismiss(dialog);
    }

    /**
     * init controller
     *
     * @param: n/a
     * @return: n/a
     * @throws: n/a
     */
    private View initControl(LayoutInflater inflater, ViewGroup container) {
        if (mStringTitle != null) {
            return null;
        } else {
            View v = inflater.inflate(R.layout.progress_dialog, container, false);
            mProgress = (ProgressBar) v.findViewById(R.id.pr_loading);
            mTvMessgae = (TextView) v.findViewById(R.id.tv_message);
            return v;
        }
    }

    /**
     * init event for controller
     *
     * @param: n/a
     * @return: n/a
     * @throws: n/a
     */
    private void initEvent() {
        try {
            if (mTvtitle != null) {
                mTvtitle.setText(mStringTitle);
            }
            if (mTvMessgae != null) {
                mTvMessgae.setText(mStringMessage);
            }
            mProgress.setEnabled(true);
            mProgress.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            Log.e(TAG, "initEvent", e);
        }
    }
}
