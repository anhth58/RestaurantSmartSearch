package com.me.restaurantsmartsearch.fragment;

import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.nlp.ContextNLP;
import com.me.restaurantsmartsearch.nlp.RestaurantNLP;
import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by Laptop88T on 2/9/2017.
 */
public class SearchFragment extends BaseFragment {
    private static final String TAG = SearchFragment.class.getSimpleName();
    private ImageView mImageBack;
    private ListView lvRestaurant;
    private ListView mListSearch;
    private EditText mInputSearch;
    private ProgressBar prLoading;
    private View imClear, imRecord, tvTitle, tvNoResult;
    private GridView mGvResult;
    private ArrayList<String> mListHistorySearch = new ArrayList<>();
    private SearchHistoryAdapter searchHistoryAdapter;

    ArrayList<Restaurant> listResult = new ArrayList<>();
    RestaurantAdapter restaurantAdapter;
    boolean isSearchOnline = false;

    private Bundle mBundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fm_search, null);

        initView(rootView);
        showSoftKeyboard(mInputSearch);
        initListener();

        return rootView;
    }

    public void initView(View view) {
        mImageBack = (ImageView) view.findViewById(R.id.ivBack);
        mListSearch = (ListView) view.findViewById(R.id.lvSuggest);
        mInputSearch = (EditText) view.findViewById(R.id.etSearch);
        tvTitle = view.findViewById(R.id.tv_title);
        tvNoResult = view.findViewById(R.id.tv_no_result);
        imClear = view.findViewById(R.id.im_clear);
        imRecord = view.findViewById(R.id.im_record);
        lvRestaurant = (ListView) view.findViewById(R.id.lv_restaurant);
        prLoading = (ProgressBar) view.findViewById(R.id.pr_loading);
    }

    public void initListener() {
        searchHistoryAdapter = new SearchHistoryAdapter(getActivity(), mListHistorySearch, new SearchHistoryAdapter.IOnCellListView() {
            @Override
            public void onRightClick(String sSearch) {
                mInputSearch.setText(sSearch.trim());
                mInputSearch.setSelection(sSearch.length());
            }

            @Override
            public void onMidClick(String sSearch, int position) {
                listResult.clear();
                mListSearch.setVisibility(View.GONE);
                mInputSearch.setText(sSearch.trim());
                searchOnline(sSearch.trim());
            }

            @Override
            public void onLeftClick(String sSearch) {
                listResult.clear();
                restaurantAdapter.notifyDataSetChanged();
                mListSearch.setVisibility(View.GONE);
                mInputSearch.setText(sSearch.trim());
                searchOnline(sSearch.trim());
            }
        });
        mListSearch.setAdapter(searchHistoryAdapter);

        mInputSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lvRestaurant.setVisibility(View.GONE);
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
                searchOnline(mInputSearch.getText().toString());
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

    public void searchOnline(String s) {
        //debug purpose, remove later
        String toast = "";
        //example nlp
        ContextNLP result = RestaurantNLP.query(s);
        toast += "Type: " + result.getType() + "\n";
        toast += "Location: " + result.getLocation();
        /*
        * Right now just three types are available:
        * ContextNLP.TYPE_CHEAP, ContextNLP.TYPE_NEAR, ContextNLP.TYPE_LOCATION
        * */
        Toast.makeText(getActivity(), toast, Toast.LENGTH_LONG).show();

        isSearchOnline = true;
        hideSoftKeyboard();
        prLoading.setVisibility(View.VISIBLE);
        SearchAsyncTask searchAsyncTask = new SearchAsyncTask(AccentRemover.removeAccent(s), new SearchAsyncTask.OnSearchComplete() {
            @Override
            public void onSearchComplete(String response) {
                try {
                    prLoading.setVisibility(View.GONE);
                    isSearchOnline = false;
                    mListSearch.setVisibility(View.GONE);
                    listResult.clear();
                    Realm.init(getActivity());
                    Realm realm = Realm.getDefaultInstance();
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject jsonObject1 = jsonObject.getJSONObject(Constant.HITS);
                    JSONArray jsonArray = jsonObject1.getJSONArray(Constant.HITS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int k = jsonArray.getJSONObject(i).optInt(Constant._ID);
                        listResult.add(realm.where(Restaurant.class).equalTo(Constant.ID, k - 1).findFirst());
                    }
                    if (listResult.size() > 0) {
                        lvRestaurant.setVisibility(View.VISIBLE);
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
        mListSearch.setVisibility(View.VISIBLE);
        showSoftKeyboard(mInputSearch);
        Realm.init(getActivity());
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Restaurant> temp = new ArrayList<>(realm.where(Restaurant.class).findAll().subList(0, 10));
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
        showSoftKeyboard(mInputSearch);
    }
}
