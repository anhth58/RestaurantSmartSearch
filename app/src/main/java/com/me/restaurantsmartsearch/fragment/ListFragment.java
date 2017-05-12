package com.me.restaurantsmartsearch.fragment;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
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
import com.me.restaurantsmartsearch.controller.PinController;
import com.me.restaurantsmartsearch.model.Pin;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.nlp.ContextNLP;
import com.me.restaurantsmartsearch.nlp.RestaurantNLP;
import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;
import com.me.restaurantsmartsearch.utils.Utils;
import com.me.restaurantsmartsearch.view.CusEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import io.realm.Realm;

/**
 * Created by Laptop88T on 11/15/2016.
 */
public class ListFragment extends BaseFragment {
    ArrayList<Restaurant> listResult = new ArrayList<>();
    ArrayList<Restaurant> listRecommended = new ArrayList<>();
    RestaurantRecommendAdapter restaurantAdapter, recommendedAdapter;
    Realm realmUI;
    private RecyclerView recyclerView, recommendedRecycle;
    private PinController pinController;
    private Realm realmSearch;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        initView(view);
        initListener();
        initData();
        pinController = PinController.with(getActivity());
        Calendar rightNow = Calendar.getInstance();
        final int hour1 = rightNow.get(Calendar.HOUR_OF_DAY);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(hour1 < 11){
                    searchOnline("Coffee gần đây",0);
                }
                else if(hour1 >= 11 && hour1 <13){
                    searchOnline("Cơm giá rẻ gần đây",0);
                }
                else if(hour1 >= 13 && hour1 <= 17){
                    searchOnline("Trà sữa gần đây",0);
                }
                else if(hour1 == 17){
                    searchOnline("Bia hơi gần đây",0);
                }
                else if(hour1 >= 18 && hour1 <20){
                    searchOnline("Cơm văn phòng gần đây", 0);
                }
                else if (hour1 >= 20){
                    searchOnline("Cà phê gần đây",0);
                }
            }
        },2000);

        return view;
    }

    public void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.lv_restaurant);
        recommendedRecycle = (RecyclerView) view.findViewById(R.id.lvRecommended);
    }

    public void initListener() {

    }

    public void initData(){
        Realm.init(getActivity());
        realmUI = Realm.getDefaultInstance();
        Log.d("Size",listResult.size() +"");
        if (!realmUI.isEmpty() && listResult.size() == 0) {
            listResult = new ArrayList<>(realmUI.where(Restaurant.class).greaterThan("id",25505).lessThan("id",25510).findAll());
            restaurantAdapter = new RestaurantRecommendAdapter(getActivity(), listResult);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(restaurantAdapter);
        } else if(listResult.size() >0 && listResult.get(0).isValid()){
//          restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
            recyclerView.setAdapter(restaurantAdapter);
            restaurantAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void searchOnline(String s, final int pageResult) {
        //debug purpose, remove later
        //example nlp
        ContextNLP result = RestaurantNLP.query(s);
        HashMap<String, String> hashMap = result.getHashMap();
        /*
        * Right now just three types are available:
        * ContextNLP.TYPE_CHEAP, ContextNLP.TYPE_NEAR, ContextNLP.TYPE_LOCATION
        * */
        String type, address, name, nearLocation;

        type = hashMap.get(ContextNLP.FIELD_TYPE);
        address = hashMap.get(ContextNLP.FIELD_ADDRESS);
        name = hashMap.get(ContextNLP.FIELD_NAME);
        nearLocation = hashMap.get(ContextNLP.FIELD_NEAR_LOCATION);

        Log.d("Hasmap", hashMap.toString());

        hideSoftKeyboard();
        String fields[] = {"name", "", ""};
        if (!TextUtils.isEmpty(address)) {
            fields[1] = "address";
            s = name.trim() + " " + address.trim();
        }

        Pin location = null;
        nearLocation = AccentRemover.removeAccent(nearLocation.trim());
        if (!TextUtils.isEmpty(nearLocation)) {
            if (!nearLocation.equals(Constant.HERE) && !nearLocation.equals(Constant.DAY)){
                location = pinController.getPinByLocation(nearLocation);
            }
            else {
                location = new Pin();
                location.setLongitude(Utils.currentLong);
                location.setLatitude(Utils.currentLat);
                Log.d("location", Utils.currentLat +" "+ Utils.currentLong);
            }
            s = name;
        }
        if (location == null) {
            location = new Pin();
        }

        SearchAsyncTask searchAsyncTask = new SearchAsyncTask(AccentRemover.removeAccent(s),0, pageResult, type, location.getLongitude(), location.getLatitude(), fields, new SearchAsyncTask.OnSearchComplete() {
            @Override
            public void onSearchComplete(String response) {
                try {
                    Realm.init(getActivity());
                    realmSearch = Realm.getDefaultInstance();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject(Constant.HITS);
                    JSONArray jsonArray = jsonObject1.getJSONArray(Constant.HITS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int k = jsonArray.getJSONObject(i).optInt(Constant._ID);
                        listRecommended.add(realmSearch.where(Restaurant.class).equalTo(Constant.ID, k).findFirst());
                    }
                    if (listRecommended.size() > 0) {
                        recommendedAdapter = new RestaurantRecommendAdapter(getActivity(), listRecommended);
                        RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getActivity(), 1);
                        recommendedRecycle.setLayoutManager(mLayoutManager2);
                        recommendedRecycle.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
                        recommendedRecycle.setItemAnimator(new DefaultItemAnimator());
                        recommendedRecycle.setAdapter(recommendedAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        searchAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
            listResult = new ArrayList<>(realmUI.where(Restaurant.class).greaterThan("id",25505).lessThan("id",25510).findAll());
            restaurantAdapter = new RestaurantRecommendAdapter(getActivity(), listResult);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(restaurantAdapter);

//            listRecommended = new ArrayList<>(realmUI.where(Restaurant.class).greaterThan("id",25505).lessThan("id",25520).findAll());
//            recommendedAdapter = new RestaurantRecommendAdapter(getActivity(), listRecommended);
//            RecyclerView.LayoutManager mLayoutManager2 = new GridLayoutManager(getActivity(), 1);
//            recommendedRecycle.setLayoutManager(mLayoutManager);
//            recommendedRecycle.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(10), true));
//            recommendedRecycle.setItemAnimator(new DefaultItemAnimator());
//            recommendedRecycle.setAdapter(restaurantAdapter);
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


