package com.me.restaurantsmartsearch.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.nlp.RestaurantNLP;
import com.me.restaurantsmartsearch.utils.AnimatorUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TabLayout tabLayout;
    private CustomViewpager viewPager;
    private ImageView imSearch, imSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //init nlp
        RestaurantNLP.init(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //importDataToElasticServer();
            }
        });

        viewPager = (CustomViewpager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        imSearch = (ImageView) findViewById(R.id.im_search);
        imSearch.setOnClickListener(this);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void importDataToElasticServer() {
        Realm.init(MainActivity.this);
        Realm realm = Realm.getDefaultInstance().getDefaultInstance();
        ArrayList<Restaurant> list = new ArrayList(realm.where(Restaurant.class).findAll());
        for (int i = 0; i < list.size(); i++) {
            Restaurant restaurant = list.get(i);
            ImportDataAsyncTask importDataAsyncTask = new ImportDataAsyncTask(restaurant.getId(), restaurant.getName(), restaurant.getAddress(), restaurant.getType(), restaurant.getDescription(), restaurant.getTime(), restaurant.getLongitude(), restaurant.getLatitude());
            importDataAsyncTask.execute();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListFragment(), "LIST");
        adapter.addFrag(new MapFragment(), "MAP");
        viewPager.setAdapter(adapter);
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
}
