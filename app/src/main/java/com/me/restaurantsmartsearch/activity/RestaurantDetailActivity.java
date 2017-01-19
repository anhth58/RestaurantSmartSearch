package com.me.restaurantsmartsearch.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.squareup.picasso.Picasso;

/**
 * Created by Laptop88T on 11/22/2016.
 */
public class RestaurantDetailActivity extends AppCompatActivity {
    private TextView tvName, tvServeScore, tvQualityScore, tvLocationScore, tvSpaceScore, tvPriceScore, tvLocation, tvType, tvTime, tvPrice;
    private ImageView imCover;
    Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        initView();
        restaurant = getIntent().getParcelableExtra("restaurant");
        if(restaurant != null){
            fillData();
        }
    }

    public void initView() {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvServeScore = (TextView) findViewById(R.id.tv_p_serve);
        tvQualityScore = (TextView) findViewById(R.id.tv_p_quality);
        tvLocationScore = (TextView) findViewById(R.id.tv_p_location);
        tvSpaceScore = (TextView) findViewById(R.id.tv_p_space);
        tvPriceScore = (TextView) findViewById(R.id.tv_p_price);

        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvPrice = (TextView) findViewById(R.id.tv_price);

        imCover = (ImageView) findViewById(R.id.im_cover);
    }
    public void fillData(){
        tvName.setText(restaurant.getName());
        tvServeScore.setText(restaurant.getpServe()+"");
        tvQualityScore.setText(restaurant.getpQuality()+"");
        tvLocationScore.setText(restaurant.getpLocation()+"");
        tvPriceScore.setText(restaurant.getpCost()+"");
        tvSpaceScore.setText(restaurant.getpSpace()+"");
        tvLocation.setText(restaurant.getAddress());
        tvPrice.setText(restaurant.getPrice());
        tvType.setText(restaurant.getType().split("-")[0].split(",")[0]);
        tvTime.setText(restaurant.getTime());
        Picasso.with(this)
                .load(restaurant.getImage())
                .placeholder(R.color.gray)
                .error(R.color.gray)
                .into(imCover);
    }
}
