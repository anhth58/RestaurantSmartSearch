package com.me.restaurantsmartsearch.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.activity.RestaurantDetailActivity;
import com.me.restaurantsmartsearch.adapter.RestaurantAdapter;
import com.me.restaurantsmartsearch.adapter.SearchHistoryAdapter;
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
import com.me.restaurantsmartsearch.view.dialog.SortDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;

/**
 * Created by Laptop88T on 2/9/2017.
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private ImageView mImageBack, imMicro, imSort;
    private ListView lvRestaurant;
    private ListView mListSearch;
    private EditText mInputSearch;
    private ProgressBar prLoading;
    private SwipeRefreshLayout vRefresh;
    private View imClear, imRecord, tvTitle, tvNoResult, footerView;
    private GridView mGvResult;
    private ArrayList<String> mListHistorySearch = new ArrayList<>();
    private SearchHistoryAdapter searchHistoryAdapter;
    private Realm realmSearch, realmSuggest;
    private boolean isSearching = false;
    private int page = 0;
    private int sortMode = 0;

    ArrayList<Restaurant> listResult = new ArrayList<>();
    RestaurantAdapter restaurantAdapter;
    boolean isSearchOnline = false;

    private Bundle mBundle;

    private PinController pinController;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fm_search, null);

        initView(rootView);
        initListener();
        getSuggestOffline();
        pinController = PinController.with(getActivity());
        return rootView;
    }

    public void initView(View view) {
        mImageBack = (ImageView) view.findViewById(R.id.ivBack);
        imMicro = (ImageView) view.findViewById(R.id.im_record);
        imSort = (ImageView) view.findViewById(R.id.im_settings);
        mListSearch = (ListView) view.findViewById(R.id.lvSuggest);
        mInputSearch = (EditText) view.findViewById(R.id.etSearch);
        tvTitle = view.findViewById(R.id.tv_title);
        tvNoResult = view.findViewById(R.id.tv_no_result);
        imClear = view.findViewById(R.id.im_clear);
        imRecord = view.findViewById(R.id.im_record);
        lvRestaurant = (ListView) view.findViewById(R.id.lv_restaurant);
        prLoading = (ProgressBar) view.findViewById(R.id.pr_loading);
        vRefresh = (SwipeRefreshLayout) view.findViewById(R.id.v_refresh);
        footerView = view.findViewById(R.id.v_footer);
        imSort.setOnClickListener(this);
        imMicro.setOnClickListener(this);
    }

    public void initListener() {
        vRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                searchOnline(mInputSearch.getText().toString(), page);
                Log.d("Refreshing", "SS");
            }
        });

        lvRestaurant.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                hideSoftKeyboard();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isSearching && firstVisibleItem > 0 && firstVisibleItem + visibleItemCount >= totalItemCount) {
                    searchOnline(mInputSearch.getText().toString(), ++page);
                }
            }
        });

        searchHistoryAdapter = new SearchHistoryAdapter(getActivity(), mListHistorySearch, new SearchHistoryAdapter.IOnCellListView() {
            @Override
            public void onRightClick(String sSearch) {
                mInputSearch.setText(sSearch.trim());
                mInputSearch.setSelection(sSearch.length());
            }

            @Override
            public void onMidClick(String sSearch, int position) {
                page = 0;
                listResult.clear();
                mListSearch.setVisibility(View.GONE);
                vRefresh.setVisibility(View.GONE);
                mInputSearch.setText(sSearch.trim());
                mInputSearch.setSelection(sSearch.length());
                searchOnline(sSearch.trim(), page);
            }

            @Override
            public void onLeftClick(String sSearch) {
                listResult.clear();
                restaurantAdapter.notifyDataSetChanged();
                mListSearch.setVisibility(View.GONE);
                mInputSearch.setText(sSearch.trim());
                mInputSearch.setSelection(sSearch.length());
                page = 0;
                searchOnline(sSearch.trim(), page);
            }
        });
        mListSearch.setAdapter(searchHistoryAdapter);

        mInputSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvRestaurant.setVisibility(View.GONE);
                vRefresh.setVisibility(View.GONE);
                mListSearch.setVisibility(View.VISIBLE);
                mInputSearch.setCursorVisible(true);
                showSoftKeyboard(mInputSearch);
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

        mInputSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                page = 0;
                searchOnline(mInputSearch.getText().toString(), page);
                return false;
            }
        });

        mInputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) getSuggest();
                else {
                    getSuggestOffline();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToSearch();
            }
        });

        mImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMainActivity().onBackPressed();
            }
        });
    }

    @Override
    public String getTagName() {
        return TAG;
    }

    @Override
    public String getTitle() {
        return TAG;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "#onPause");
        hideSoftKeyboard();
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

        isSearchOnline = true;
        isSearching = true;
        hideSoftKeyboard();
        String fields[] = {"name", "", ""};
        if (!TextUtils.isEmpty(address)) {
            fields[1] = "address";
            s = name.trim() + " " + address.trim();
        }

        Pin location = null;
        nearLocation = AccentRemover.removeAccent(nearLocation.trim());
        if (!TextUtils.isEmpty(nearLocation)) {
            if (!nearLocation.equals(Constant.HERE) && !nearLocation.equals(Constant.DAY))
                location = pinController.getPinByLocation(nearLocation);
            else {
                location = new Pin();
                location.setLongitude(Utils.currentLong);
                location.setLatitude(Utils.currentLat);
            }
            s = name;
        }
        if (location == null) {
            location = new Pin();
        }
        if (page > 0) {
            footerView.setVisibility(View.VISIBLE);
            prLoading.setVisibility(View.GONE);
        } else {
            footerView.setVisibility(View.GONE);
            prLoading.setVisibility(View.VISIBLE);
        }
        if (vRefresh.isRefreshing()) prLoading.setVisibility(View.GONE);
        SearchAsyncTask searchAsyncTask = new SearchAsyncTask(AccentRemover.removeAccent(s),sortMode, pageResult, type, location.getLongitude(), location.getLatitude(), fields, new SearchAsyncTask.OnSearchComplete() {
            @Override
            public void onSearchComplete(String response) {
                try {
                    imSort.setVisibility(View.VISIBLE);
                    imMicro.setVisibility(View.GONE);
                    footerView.setVisibility(View.GONE);
                    if (vRefresh.isRefreshing()) vRefresh.setRefreshing(false);
                    Log.d("#ResponseString", response);
                    prLoading.setVisibility(View.GONE);
                    isSearchOnline = false;
                    isSearching = false;
                    mListSearch.setVisibility(View.GONE);
                    if (pageResult == 0) listResult.clear();
                    Realm.init(getActivity());
                    realmSearch = Realm.getDefaultInstance();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject(Constant.HITS);
                    JSONArray jsonArray = jsonObject1.getJSONArray(Constant.HITS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int k = jsonArray.getJSONObject(i).optInt(Constant._ID);
                        listResult.add(realmSearch.where(Restaurant.class).equalTo(Constant.ID, k).findFirst());
                    }
                    if (listResult.size() > 0) {
                        lvRestaurant.setVisibility(View.VISIBLE);
                        vRefresh.setVisibility(View.VISIBLE);
                        if (restaurantAdapter == null) {
                            restaurantAdapter = new RestaurantAdapter(getActivity(), listResult);
                            lvRestaurant.setAdapter(restaurantAdapter);
                        } else {
                            restaurantAdapter.setRestaurant(listResult);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        searchAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void getSuggestOffline() {
        mInputSearch.setCursorVisible(true);
        lvRestaurant.setVisibility(View.GONE);
        vRefresh.setVisibility(View.GONE);
        mListSearch.setVisibility(View.VISIBLE);
        showSoftKeyboard(mInputSearch);
        Realm.init(getActivity());
        realmSuggest = Realm.getDefaultInstance();
        ArrayList<Restaurant> temp = new ArrayList<>(realmSuggest.where(Restaurant.class).findAll().subList(0, 10));
        mListHistorySearch.clear();
        for (int i = 0; i < temp.size(); i++) {
            mListHistorySearch.add(temp.get(i).getName());
        }
        searchHistoryAdapter.notifyDataSetChanged();
    }

    public void getSuggest() {
        SuggestAsyncTask suggestAsyncTask = new SuggestAsyncTask(mInputSearch.getText().toString(), new SuggestAsyncTask.OnSuggestComplete() {
            @Override
            public void onSuggestComplete(String s) {
                try {
                    mListHistorySearch.clear();
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonSuggestArray = jsonObject.getJSONArray(Constant.RESTAURANT_SUGGEST);
                    JSONObject jsonSuggest = jsonSuggestArray.getJSONObject(0);
                    JSONArray options = jsonSuggest.getJSONArray(Constant.OPTIONS);
                    for (int i = 0; i < options.length(); i++) {
                        JSONObject option = options.getJSONObject(i);
                        JSONObject source = option.getJSONObject(Constant._SOURCE);
                        JSONObject suggest = source.getJSONArray(Constant.SUGGEST).getJSONObject(0);
                        String suggestString = suggest.getString(Constant.INPUT);
                        mListHistorySearch.add(suggestString);
                    }

                    if (mListHistorySearch.size() > 0 && !isSearchOnline) {
                        searchHistoryAdapter.setData(mListHistorySearch);
                    } else {
                        mListSearch.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        suggestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void backToSearch() {
        mInputSearch.setText("");
        mInputSearch.setCursorVisible(true);
        mListSearch.setVisibility(View.VISIBLE);
        lvRestaurant.setVisibility(View.GONE);
        imMicro.setVisibility(View.VISIBLE);
        imSort.setVisibility(View.GONE);
        vRefresh.setVisibility(View.GONE);
        showSoftKeyboard(mInputSearch);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(realmSearch != null)realmSearch.close();
        if(realmSuggest != null)realmSuggest.close();
        pinController.getRealm().close();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.im_settings:
                SortDialog sortDialog = new SortDialog(getActivity(), new SortDialog.onClickChooseAction() {
                    @Override
                    public void onSortName() {
                        sortMode = 0;
                        page = 0;
                        searchOnline(mInputSearch.getText().toString(), page);
                    }

                    @Override
                    public void onSortDistance() {
                        sortMode = 1;
                        page = 0;
                        searchOnline(mInputSearch.getText().toString(), page);
                    }

                    @Override
                    public void onSortPrice() {
                        page = 0;
                        sortMode = 2;
                        searchOnline(mInputSearch.getText().toString(), page);
                    }

                    @Override
                    public void onSortPriceEx() {
                        page = 0;
                        sortMode = 3;
                        searchOnline(mInputSearch.getText().toString(), page);
                    }
                });
                sortDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                sortDialog.getWindow().setGravity(Gravity.BOTTOM);
                sortDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                sortDialog.show();
                break;
            case R.id.im_record:
                break;
            default:
                break;
        }
    }
}
