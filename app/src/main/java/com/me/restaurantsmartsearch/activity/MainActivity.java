package com.me.restaurantsmartsearch.activity;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.adapter.ViewPagerAdapter;
import com.me.restaurantsmartsearch.async.ImportDataAsyncTask;
import com.me.restaurantsmartsearch.customview.CustomViewpager;
import com.me.restaurantsmartsearch.fragment.BaseFragment;
import com.me.restaurantsmartsearch.fragment.ListFragment;
import com.me.restaurantsmartsearch.fragment.MapFragment;
import com.me.restaurantsmartsearch.fragment.SearchFragment;
import com.me.restaurantsmartsearch.fragment.UserFragment;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.nlp.RestaurantNLP;
import com.me.restaurantsmartsearch.utils.AccentRemover;
import com.me.restaurantsmartsearch.utils.AnimatorUtils;
import com.me.restaurantsmartsearch.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private CustomViewpager viewPager;
    private ImageView imSearch, imSetting;
    private FloatingActionButton fab;
    private Realm realm;
    private int[] tabIcons = {
            R.drawable.ic_home_white,
            R.drawable.ic_map,
            R.drawable.ic_user
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //init nlp
        RestaurantNLP.init(this);
        initView();
        initListener();
        setupTabIcons();
    }

    public void initView() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        viewPager = (CustomViewpager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        imSearch = (ImageView) findViewById(R.id.im_search);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.setOffscreenPageLimit(3);
    }

    public void initListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //importDataToElasticServer();
                //mCreateAndSaveFile("SSS");
                Realm.init(MainActivity.this);
                realm = Realm.getDefaultInstance().getDefaultInstance();
                Log.d("16900",realm.where(Restaurant.class).equalTo("id",16900).findFirst().getName());
                Log.d("20900",realm.where(Restaurant.class).equalTo("id",20900).findFirst().getName());
                Log.d("26030",realm.where(Restaurant.class).equalTo("id",26030).findFirst().getName());
            }
        });
        imSearch.setOnClickListener(this);

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.white);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                int tabIconColor = ContextCompat.getColor(MainActivity.this, R.color.black);
                tab.getIcon().setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
            }
        });
    }

    public void importDataToElasticServer() {
        Realm.init(MainActivity.this);
        realm = Realm.getDefaultInstance().getDefaultInstance();
        ArrayList<Restaurant> list = new ArrayList(realm.where(Restaurant.class).findAll());
        for (int i = 0; i < list.size(); i++) {
            Restaurant restaurant = list.get(i);
            ImportDataAsyncTask importDataAsyncTask = new ImportDataAsyncTask(restaurant.getId() + 25506, restaurant.getName(), restaurant.getAddress(), restaurant.getType(), restaurant.getDescription(), restaurant.getTime(), restaurant.getLongitude(), restaurant.getLatitude(),restaurant.getPrice());
            importDataAsyncTask.execute();
        }
    }

    public void mCreateAndSaveFile(String params) {
        try {
            Realm.init(MainActivity.this);
            Realm realm = Realm.getDefaultInstance().getDefaultInstance();
            File file1 = new File(new File(Environment.getExternalStorageDirectory().toString()),"restaurants_foody.json");
            FileWriter file = new FileWriter(file1);
            ArrayList<Restaurant> list = new ArrayList<>(realm.where(Restaurant.class).findAll());
            Log.d("Size",list.size()+"");
            for(int i = 0; i< list.size();i++){
                //{"index":{"_index":"test","_type":"test","_id":"1"}}
                JSONObject index = new JSONObject();
                index.put("_index","mydb");
                index.put("_type","restaurant");
                index.put("_id",Integer.toString(i + 25506));
                JSONObject action = new JSONObject();
                action.put("index",index);
                Restaurant restaurant = list.get(i);
                JSONObject jsonObject = new JSONObject();
                JSONObject jsonSuggest = new JSONObject();
                JSONObject jsonSuggest1 = new JSONObject();
                JSONArray suggest = new JSONArray();
                JSONObject pin = new JSONObject();
                JSONObject location = new JSONObject();
                location.put(Constant.LAT, restaurant.getLatitude());
                location.put(Constant.LON, restaurant.getLongitude());
                pin.put(Constant.LOCATION, location);

                //jsonObject.put(Constant.PIN, pin);
                jsonObject.put(Constant.NAME, AccentRemover.removeAccent(restaurant.getName()));
                jsonObject.put(Constant.ADDRESS, AccentRemover.removeAccent(restaurant.getAddress()));
                jsonObject.put(Constant.DESCRIPTION, AccentRemover.removeAccent(restaurant.getDescription()));
                jsonObject.put(Constant.TYPE, AccentRemover.removeAccent(restaurant.getType()));
                jsonObject.put(Constant.TIME, AccentRemover.removeAccent(restaurant.getTime()));
                jsonObject.put(Constant.IMAGE, AccentRemover.removeAccent(restaurant.getImage()));

                String timeInOut[] = restaurant.getTime().split("-");
                float timeIn = 0, timeOut = 0;
                int priceMin = 0, priceMax = 0;

                if (timeInOut.length == 2) {
                    if(timeInOut[0].trim().length() == 8)timeIn = Integer.parseInt(timeInOut[0].trim().substring(0, 2)) + Integer.parseInt(timeInOut[0].trim().substring(3, 5)) / 60;
                    else {
                        timeIn = Integer.parseInt(timeInOut[0].trim().substring(0, 1)) + Integer.parseInt(timeInOut[0].trim().substring(2, 4)) / 60;
                    }
                    if (timeInOut[0].substring(6, 8).equals("PM")) timeIn += 12;
                    if(timeInOut[1].trim().length() == 8)timeOut = Integer.parseInt(timeInOut[1].trim().substring(0, 2)) + Integer.parseInt(timeInOut[1].trim().substring(3, 5)) / 60;
                    else {
                        timeOut = Integer.parseInt(timeInOut[1].trim().substring(0, 1)) + Integer.parseInt(timeInOut[1].trim().substring(2, 4)) / 60;
                    }
                    if (timeInOut[1].substring(6, 8).equals("PM")) timeOut += 12;
                }

                String priceMinMax[] = AccentRemover.removeAccent(restaurant.getPrice()).split("-");

                if (priceMinMax.length == 2) {
                    priceMin = Integer.parseInt(priceMinMax[0].replaceAll("d","").replace(".","").trim());
                    priceMax = Integer.parseInt(priceMinMax[1].replaceAll("d","").replace(".","").trim());
                }

//                jsonObject.put(Constant.TIME_IN, timeIn);
//                jsonObject.put(Constant.TIME_OUT, timeOut);
//                jsonObject.put(Constant.MAX_PRICE, priceMin);
//                jsonObject.put(Constant.MIN_PRICE, priceMax);
                JSONObject point = new JSONObject();
                point.put("pLocation",restaurant.getpLocation());
                point.put("pSpace",restaurant.getpSpace());
                point.put("pCost",restaurant.getpCost());
                point.put("pServe",restaurant.getpServe());
                point.put("pQuality",restaurant.getpQuality());
                jsonObject.put("point",point);
                jsonObject.put(Constant.PRICE,restaurant.getPrice());
                jsonObject.put(Constant.LONGITUDE, restaurant.getLongitude());
                jsonObject.put(Constant.LATITUDE, restaurant.getLatitude());
                jsonSuggest.put(Constant.INPUT, restaurant.getName());
                jsonSuggest.put(Constant.WEIGHT, i + 1000);
                jsonSuggest1.put(Constant.INPUT, AccentRemover.removeAccent(restaurant.getName()));
                jsonSuggest1.put(Constant.WEIGHT, i + 10);
                suggest.put(jsonSuggest);
                suggest.put(jsonSuggest1);
                //jsonObject.put(Constant.SUGGEST, suggest);
                //file.write(action.toString()+"\n");
                file.write(jsonObject.toString()+",");
                file.flush();
                Log.d("Writing",i +"");
            }
            file.close();
            Log.d("Write", "done");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListFragment(), "LIST");
        adapter.addFrag(new MapFragment(), "MAP");
        adapter.addFrag(new UserFragment(), "USER");
        viewPager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    public void addSearchResultToMap(List<Restaurant> list) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof MapFragment) {
                ((MapFragment) fragment).addSearchResult(list);
                return;
            }
        }
    }

    public void moveToPoint(int pos) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if (fragment instanceof MapFragment) {
                ((MapFragment) fragment).moveCamera(pos);
                return;
            }

        }
    }

    public CustomViewpager getViewPager() {
        return viewPager;
    }

    private void replaceFragment(BaseFragment fragment, AnimatorUtils.Animator animator) {
        if (fragment == null) {
            return;
        }
        FragmentTransaction transaction = getSupportFragmentManager().
                beginTransaction()
                .setCustomAnimations(animator.animEnter, animator.animExit, animator.popEnter, animator.popExit)
                .replace(R.id.main_content, fragment, fragment.getClass().getSimpleName());
        if (getSupportFragmentManager().getFragments() != null) {
            transaction.addToBackStack(fragment.getTagName());
        }
        transaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_search:
                replaceFragment(new SearchFragment(), AnimatorUtils.getAnimationBottomToTop());
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
