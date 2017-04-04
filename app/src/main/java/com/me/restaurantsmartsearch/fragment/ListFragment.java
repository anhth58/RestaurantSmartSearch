package com.me.restaurantsmartsearch.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.activity.MainActivity;
import com.me.restaurantsmartsearch.activity.RestaurantDetailActivity;
import com.me.restaurantsmartsearch.adapter.RestaurantAdapter;
import com.me.restaurantsmartsearch.adapter.RestaurantRecommendAdapter;
import com.me.restaurantsmartsearch.adapter.SuggestAdapter;
import com.me.restaurantsmartsearch.async.SearchAsyncTask;
import com.me.restaurantsmartsearch.async.SuggestAsyncTask;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;
import com.me.restaurantsmartsearch.utils.Utils;
import com.me.restaurantsmartsearch.view.CusEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by Laptop88T on 11/15/2016.
 */
public class ListFragment extends BaseFragment {
    ArrayList<Restaurant> listResult = new ArrayList<>();
    RestaurantRecommendAdapter restaurantAdapter;
    Realm realmUI;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        initView(view);
        initListener();
        initData();
        return view;
    }

    public void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.lv_restaurant);
    }

    public void initListener() {

    }

    public void initData(){
        Realm.init(getActivity());
        realmUI = Realm.getDefaultInstance();
        Log.d("Size",listResult.size() +"");
        if (!realmUI.isEmpty() && listResult.size() == 0) {
            listResult = new ArrayList<>(realmUI.where(Restaurant.class).greaterThan("id",25505).lessThan("id",25520).findAll());
            restaurantAdapter = new RestaurantRecommendAdapter(getActivity(), listResult);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(restaurantAdapter);

        } else if(listResult.size() >0 && listResult.get(0).isValid()){
//            restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
            recyclerView.setAdapter(restaurantAdapter);
            restaurantAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getTagName() {
        return ListFragment.class.getSimpleName();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("AA","#onResume");
        if(listResult.size() >0 && !listResult.get(0).isValid()){
            Log.d("AA","invalid");
            Realm.init(getActivity());
            realmUI = Realm.getDefaultInstance();
            listResult = new ArrayList<>(realmUI.where(Restaurant.class).greaterThan("id",25505).lessThan("id",25520).findAll());
            restaurantAdapter = new RestaurantRecommendAdapter(getActivity(), listResult);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(restaurantAdapter);
        }
        else {
            Log.d("AA","valid");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("AA","#onPause");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        if(realmUI != null)realmUI.close();
//        if(realmSearch != null)realmSearch.close();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}


