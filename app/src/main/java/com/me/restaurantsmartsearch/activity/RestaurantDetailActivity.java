package com.me.restaurantsmartsearch.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

/**
 * Created by Laptop88T on 11/22/2016.
 */
public class RestaurantDetailActivity extends AppCompatActivity {
    private TextView tvStatus, tvName, tvServeScore, tvQualityScore, tvLocationScore, tvSpaceScore, tvPriceScore, tvLocation, tvType, tvTime, tvPrice;
    private ImageView imCover, imLocation, imBack;
    private FloatingActionButton fabStar;
    Restaurant restaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        initView();
        restaurant = getIntent().getParcelableExtra("restaurant");
        if (restaurant != null) {
            fillData();
        }
        initListener();
    }

    public void initView() {
        tvName = (TextView) findViewById(R.id.tv_name);
        tvServeScore = (TextView) findViewById(R.id.tv_p_serve);
        tvQualityScore = (TextView) findViewById(R.id.tv_p_quality);
        tvLocationScore = (TextView) findViewById(R.id.tv_p_location);
        tvSpaceScore = (TextView) findViewById(R.id.tv_p_space);
        tvPriceScore = (TextView) findViewById(R.id.tv_p_price);
        tvStatus = (TextView) findViewById(R.id.tv_status);

        tvLocation = (TextView) findViewById(R.id.tv_location);
        tvType = (TextView) findViewById(R.id.tv_type);
        tvTime = (TextView) findViewById(R.id.tv_time);
        tvPrice = (TextView) findViewById(R.id.tv_price);

        imCover = (ImageView) findViewById(R.id.im_cover);
        imLocation = (ImageView) findViewById(R.id.im_location);
        imBack = (ImageView) findViewById(R.id.im_back);
        fabStar = (FloatingActionButton) findViewById(R.id.fab_star);

    }

    public void fillData() {
        tvName.setText(restaurant.getName());
        tvServeScore.setText(restaurant.getpServe() + "");
        tvQualityScore.setText(restaurant.getpQuality() + "");
        tvLocationScore.setText(restaurant.getpLocation() + "");
        tvPriceScore.setText(restaurant.getpCost() + "");
        tvSpaceScore.setText(restaurant.getpSpace() + "");
        tvLocation.setText(restaurant.getAddress());
        tvPrice.setText(restaurant.getPrice());
        tvType.setText(restaurant.getType().split("-")[0].split(",")[0]);
        tvTime.setText(restaurant.getTime());

        String timeInOut[] = restaurant.getTime().trim().split("-");
        float timeIn = 0, timeOut = 0;

        if (timeInOut.length == 2) {
            if(timeInOut[0].trim().length() == 8){
                timeIn = Integer.parseInt(timeInOut[0].trim().substring(0, 2)) +(float) Float.parseFloat(timeInOut[0].trim().substring(3, 5)) / 60;
                if (timeInOut[0].substring(6, 8).equals("PM")) timeIn += 12;
            }
            else {
                timeIn = Integer.parseInt(timeInOut[0].trim().substring(0, 1)) + (float)Float.parseFloat(timeInOut[0].trim().substring(2, 4)) / 60;
                if (timeInOut[0].substring(5, 7).equals("PM")) timeIn += 12;
            }

            if(timeInOut[1].trim().length() == 8){
                timeOut = Integer.parseInt(timeInOut[1].trim().substring(0, 2)) + (float)Integer.parseInt(timeInOut[1].trim().substring(3, 5)) / 60;
                if (timeInOut[1].substring(7, 9).equals("PM")) timeOut += 12;
            }
            else {
                timeOut = Integer.parseInt(timeInOut[1].trim().substring(0, 1)) + (float)Float.parseFloat(timeInOut[1].trim().substring(2, 4)) / 60;
                if (timeInOut[1].substring(6, 8).equals("PM")) timeOut += 12;
            }

        }

        Calendar rightNow = Calendar.getInstance();
        int hour1 = rightNow.get(Calendar.HOUR_OF_DAY);

        int minute = rightNow.get(Calendar.MINUTE);
        float hour = hour1 + (float)minute/60;

        if(hour > timeIn && hour < timeOut){
            tvStatus.setText(R.string.p_opening);
            tvStatus.setTextColor(getResources().getColor(R.color.green));
        }
        else {
            tvStatus.setText(R.string.p_closed);
            tvStatus.setTextColor(getResources().getColor(R.color.red));
        }

        if(!TextUtils.isEmpty(restaurant.getImage())){
            Picasso.with(this)
                    .load(restaurant.getImage().replace("80x80","1000x750"))
                    .placeholder(R.drawable.im_placeholder)
                    .error(R.drawable.im_placeholder)
                    .into(imCover);
        }
    }

    private void initListener() {
        imLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RestaurantDetailActivity.this, MapActivity.class);
                if (restaurant != null) intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        });
        imBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fabStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fabStar.setImageResource(R.drawable.ic_star_enable);
            }
        });
    }
}
