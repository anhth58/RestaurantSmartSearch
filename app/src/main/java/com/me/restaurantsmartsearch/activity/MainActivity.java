package com.me.restaurantsmartsearch.activity;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;
import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.adapter.ViewPagerAdapter;
import com.me.restaurantsmartsearch.async.ImportDataAsyncTask;
import com.me.restaurantsmartsearch.customview.CustomViewpager;
import com.me.restaurantsmartsearch.fragment.ListFragment;
import com.me.restaurantsmartsearch.fragment.MapFragment;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.me.restaurantsmartsearch.nlp.RestaurantNLP;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private CustomViewpager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init nlp
        RestaurantNLP.init(this);

        //example nlp
        String[][] names = RestaurantNLP.getNamesInQuery("Tìm quán chả cá gần nhất ở đường Cầu Giấy");
        for (int i=0; i<names.length; i++){
            System.out.println(names[i][0] + ":" + names[i][1]);
        }
        System.out.println(RestaurantNLP.getCategory("Tìm quán chả cá gần nhất ở đường Cầu Giấy"));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //importDataToElasticServer();
            }
        });
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (CustomViewpager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //importDataFromJSONFile();
    }

    public void importDataToElasticServer(){
        Realm.init(MainActivity.this);
        Realm realm = Realm.getDefaultInstance().getDefaultInstance();
        ArrayList<Restaurant> list = new ArrayList(realm.where(Restaurant.class).findAll());
        for (int i = 0; i < list.size(); i++) {
            Restaurant restaurant = list.get(i);
            ImportDataAsyncTask importDataAsyncTask = new ImportDataAsyncTask(restaurant.getId(), restaurant.getName(), restaurant.getAddress(), restaurant.getType(), restaurant.getDescription(), restaurant.getTime(), restaurant.getLongitude(), restaurant.getLatitude());
            importDataAsyncTask.execute();
        }
    }

    public void importDataFromJSONFile(){
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Realm.init(MainActivity.this);
                    Realm realm = Realm.getDefaultInstance();
                    JSONArray jsonArray = new JSONArray(loadJSONFromAsset());
                    Log.d("length",jsonArray.length()+"");
                    for(int i = 0; i < jsonArray.length();i++){
                        Restaurant restaurant = new Restaurant();
                        restaurant.setId(i);
                        restaurant.setName(jsonArray.getJSONObject(i).optString("name"));
                        restaurant.setAddress(jsonArray.getJSONObject(i).optString("address"));
                        restaurant.setTime(jsonArray.getJSONObject(i).optString("time"));
                        restaurant.setType(jsonArray.getJSONObject(i).optString("type"));
                        restaurant.setDescription(jsonArray.getJSONObject(i).optString("description"));
                        restaurant.setLatitude(jsonArray.getJSONObject(i).optDouble("latitude"));
                        restaurant.setLongitude(jsonArray.getJSONObject(i).optDouble("longitude"));
                        restaurant.setImage(jsonArray.getJSONObject(i).optString("image"));
                        restaurant.setPrice(jsonArray.getJSONObject(i).optString("price"));
                        restaurant.setpCost(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pCost"));
                        restaurant.setpLocation(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pLocation"));
                        restaurant.setpSpace(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pSpace"));
                        restaurant.setpServe(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pServe"));
                        restaurant.setpQuality(jsonArray.getJSONObject(i).getJSONObject("point").optDouble("pQuality"));
                        realm.beginTransaction();
                        realm.copyToRealmOrUpdate(restaurant);
                        realm.commitTransaction();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        t.run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListFragment(), "LIST");
        adapter.addFrag(new MapFragment(), "MAP");
        viewPager.setAdapter(adapter);
    }

    public void  addSearchResultToMap(List<Restaurant> list){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment instanceof MapFragment){
                ((MapFragment) fragment).addSearchResult(list);
                return;
            }
        }
    }

    public void  moveToPoint(int pos){
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for(Fragment fragment : fragments){
            if(fragment instanceof MapFragment){
                ((MapFragment) fragment).moveCamera(pos);
                return;
            }

        }
    }

    public CustomViewpager getViewPager(){
        return viewPager;
    }
}
