package com.me.restaurantsmartsearch.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
    CusEditText edSearch;
    SwipeMenuListView lvRestaurant;
    ArrayList<Restaurant> listResult = new ArrayList<>();
    RestaurantAdapter restaurantAdapter;
    ListView lvSugest;
    ArrayList<String> suggestList = new ArrayList<>();
    SuggestAdapter suggestAdapter;
    boolean isSearchOnline = false;
    Realm realmUI, realmSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);

        initView(view);
        initListener();
        initSwipeMenu();
        initData();
        Log.d("AA","#onCreateView");
        return view;
    }

    public void initView(View view) {
        edSearch = (CusEditText) view.findViewById(R.id.ed_search);
        lvRestaurant = (SwipeMenuListView) view.findViewById(R.id.lv_restaurant);
        lvSugest = (ListView) view.findViewById(R.id.lv_suggest);
        suggestAdapter = new SuggestAdapter(getActivity(), suggestList);
        lvSugest.setAdapter(suggestAdapter);
    }

    public void initListener() {
        edSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searchOnline();
                return false;
            }
        });

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSuggest();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lvRestaurant.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(listResult.get(position).isValid()){
                    Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                    intent.putExtra("restaurant", listResult.get(position));
                    getActivity().startActivity(intent);
                }
                else {
                    Realm.init(getActivity());
                    realmUI = Realm.getDefaultInstance();
                    listResult = new ArrayList<>(realmUI.where(Restaurant.class).findAll().subList(0, 10));
                    restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
                    lvRestaurant.setAdapter(restaurantAdapter);
                    restaurantAdapter.setRestaurant(listResult);
                    Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                    intent.putExtra("restaurant", listResult.get(position));
                    getActivity().startActivity(intent);
                }
            }
        });

        lvSugest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edSearch.setText(suggestList.get(position));
                searchOnline();
            }
        });
    }

    public void initSwipeMenu() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(50, 205,
                        50)));
                openItem.setWidth(Utils.dp2px(90, getActivity()));
                menu.addMenuItem(openItem);
                openItem.setIcon(R.drawable.location);
            }
        };

        lvRestaurant.setMenuCreator(creator);

        lvRestaurant.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        ((MainActivity) getActivity()).getViewPager().setCurrentItem(1);
                        ((MainActivity) getActivity()).moveToPoint(position);
                        break;
                }
                // false : close the menu; true : not close the menu
                return false;
            }
        });
    }

    public void initData(){
        Realm.init(getActivity());
        realmUI = Realm.getDefaultInstance();
        Log.d("Size",listResult.size() +"");
        if (!realmUI.isEmpty() && listResult.size() == 0) {
            listResult = new ArrayList<>(realmUI.where(Restaurant.class).findAll().subList(0, 10));
            restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
            lvRestaurant.setAdapter(restaurantAdapter);
            restaurantAdapter.setRestaurant(listResult);
        } else if(listResult.size() >0 && listResult.get(0).isValid()){
//            restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
            lvRestaurant.setAdapter(restaurantAdapter);
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
            listResult = new ArrayList<>(realmUI.where(Restaurant.class).findAll().subList(0, 10));
            restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
            lvRestaurant.setAdapter(restaurantAdapter);
            restaurantAdapter.setRestaurant(listResult);
        }
        else {
            Log.d("AA","valid");
        }
    }

    public void searchOnline() {
        isSearchOnline = true;
        hideSoftKeyboard();
        double lon = 0, lat = 0;
        String type = "";
        String fields[] = {"name"};
        SearchAsyncTask searchAsyncTask = new SearchAsyncTask(AccentRemover.removeAccent(edSearch.getText().toString()), 0 , type, lon, lat, fields, new SearchAsyncTask.OnSearchComplete() {
            @Override
            public void onSearchComplete(String response) {
                try {
                    isSearchOnline = false;
                    lvSugest.setVisibility(View.GONE);
                    listResult.clear();
                    Realm.init(getActivity());
                    realmSearch = Realm.getDefaultInstance();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject(Constant.HITS);
                    JSONArray jsonArray = jsonObject1.getJSONArray(Constant.HITS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int k = jsonArray.getJSONObject(i).optInt(Constant._ID);
                        listResult.add(realmSearch.where(Restaurant.class).equalTo(Constant.ID, k - 1).findFirst());
                    }
                    if (listResult.size() > 0) {
                        restaurantAdapter.setRestaurant(listResult);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        searchAsyncTask.execute();
    }

    public void getSuggest() {
        SuggestAsyncTask suggestAsyncTask = new SuggestAsyncTask(edSearch.getText().toString(), new SuggestAsyncTask.OnSuggestComplete() {
            @Override
            public void onSuggestComplete(String s) {
                try {
                    suggestList.clear();
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonSuggestArray = jsonObject.getJSONArray(Constant.RESTAURANT_SUGGEST);
                    JSONObject jsonSuggest = jsonSuggestArray.getJSONObject(0);
                    JSONArray options = jsonSuggest.getJSONArray(Constant.OPTIONS);
                    for (int i = 0; i < options.length(); i++) {
                        JSONObject option = options.getJSONObject(i);
                        JSONObject source = option.getJSONObject(Constant._SOURCE);
                        JSONObject suggest = source.getJSONArray(Constant.SUGGEST).getJSONObject(0);
                        String suggestString = suggest.getString(Constant.INPUT);
                        suggestList.add(suggestString);
                    }

                    if (suggestList.size() > 0 && !isSearchOnline) {
                        lvSugest.setVisibility(View.VISIBLE);
                        suggestAdapter.setData(suggestList);
                    } else {
                        lvSugest.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        suggestAsyncTask.execute();
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
}
