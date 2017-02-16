package com.me.restaurantsmartsearch.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.me.restaurantsmartsearch.activity.MainActivity;
import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.activity.RestaurantDetailActivity;
import com.me.restaurantsmartsearch.adapter.RestaurantAdapter;
import com.me.restaurantsmartsearch.adapter.SuggestAdapter;
import com.me.restaurantsmartsearch.async.SearchAsyncTask;
import com.me.restaurantsmartsearch.async.SuggestAsyncTask;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.view.CusEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_fragment, container, false);
        edSearch = (CusEditText) view.findViewById(R.id.ed_search);
        lvRestaurant = (SwipeMenuListView) view.findViewById(R.id.lv_restaurant);
        lvSugest = (ListView) view.findViewById(R.id.lv_suggest);
        suggestAdapter = new SuggestAdapter(getActivity(), suggestList);
        lvSugest.setAdapter(suggestAdapter);
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
                Intent intent = new Intent(getActivity(), RestaurantDetailActivity.class);
                intent.putExtra("restaurant", listResult.get(position));
                getActivity().startActivity(intent);
            }
        });

        lvSugest.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                edSearch.setText(suggestList.get(position));
                searchOnline();
            }
        });

        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getActivity());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(50, 205,
                        50)));
                openItem.setWidth(dp2px(90));
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

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public String getTagName() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (listResult.size() == 0) {
            Realm.init(getActivity());
            Realm realm = Realm.getDefaultInstance();
            listResult = new ArrayList<>(realm.where(Restaurant.class).findAll().subList(0, 10));
            restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
            lvRestaurant.setAdapter(restaurantAdapter);
            restaurantAdapter.setRestaurant(listResult);
        }
    }

    public void searchOnline(){
        isSearchOnline = true;
        hideSoftKeyboard();
        SearchAsyncTask searchAsyncTask = new SearchAsyncTask(AccentRemover.removeAccent(edSearch.getText().toString()), new SearchAsyncTask.OnSearchComplete() {
            @Override
            public void onSearchComplete(String response) {
                try {
                    isSearchOnline = false;
                    lvSugest.setVisibility(View.GONE);
                    listResult.clear();
                    Realm.init(getActivity());
                    Realm realm = Realm.getDefaultInstance();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject("hits");
                    JSONArray jsonArray = jsonObject1.getJSONArray("hits");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int k = jsonArray.getJSONObject(i).optInt("_id");
                        listResult.add(realm.where(Restaurant.class).equalTo("id", k - 1).findFirst());
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
    public void getSuggest(){
        SuggestAsyncTask suggestAsyncTask = new SuggestAsyncTask(edSearch.getText().toString(), new SuggestAsyncTask.OnSuggestComplete() {
            @Override
            public void onSuggestComplete(String s) {
                try {
                    suggestList.clear();
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonSuggestArray = jsonObject.getJSONArray("restaurant-suggest");
                    JSONObject jsonSuggest = jsonSuggestArray.getJSONObject(0);
                    JSONArray options = jsonSuggest.getJSONArray("options");
                    Log.d("SIZE", options.length() + "");
                    for (int i = 0; i < options.length(); i++) {
                        JSONObject option = options.getJSONObject(i);
                        JSONObject source = option.getJSONObject("_source");
                        JSONObject suggest = source.getJSONArray("suggest").getJSONObject(0);
                        String suggestString = suggest.getString("input");
                        suggestList.add(suggestString);
                        Log.d("String", suggestString);
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

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
}
