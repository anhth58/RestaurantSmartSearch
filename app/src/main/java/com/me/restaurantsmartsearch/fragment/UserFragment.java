package com.me.restaurantsmartsearch.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.me.restaurantsmartsearch.R;

/**
 * Created by anhth_58 on 3/27/2017.
 */

public class UserFragment extends BaseFragment {
    @Override
    public String getTagName() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        return view;
    }
}
