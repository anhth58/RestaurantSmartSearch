package com.me.restaurantsmartsearch.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.activity.MainActivity;
import com.me.restaurantsmartsearch.activity.RestaurantDetailActivity;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by anhth_58 on 3/20/2017.
 */

public class RestaurantRecommendAdapter extends RecyclerView.Adapter<RestaurantRecommendAdapter.MyViewHolder> {

    private Context mContext;
    private List<Restaurant> list;
    private int mPosition;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, count, time, price;
        public ImageView thumbnail, overflow;
        private View cardView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            overflow = (ImageView) view.findViewById(R.id.overflow);
            time = (TextView) view.findViewById(R.id.tv_time);
            price = (TextView) view.findViewById(R.id.tv_price);
            cardView = view.findViewById(R.id.v_content);
        }
    }


    public RestaurantRecommendAdapter(Context mContext, List<Restaurant> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_restaurant_recommend, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        final Restaurant restaurant = list.get(position);
        holder.title.setText(restaurant.getName());
        holder.count.setText(restaurant.getAddress());
        holder.time.setText(restaurant.getTime());
        holder.price.setText(restaurant.getPrice());

        // loading album cover using Glide library
        if(!TextUtils.isEmpty(restaurant.getImage())){
            Picasso.with(mContext)
                    .load(restaurant.getImage().replace("80x80","1000x750"))
                    .placeholder(R.drawable.im_placeholder)
                    .error(R.drawable.im_placeholder)
                    .into(holder.thumbnail);
        }
        else {
            holder.thumbnail.setImageResource(R.drawable.im_avatar);
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, RestaurantDetailActivity.class);
                intent.putExtra("restaurant", restaurant);
                mContext.startActivity(intent);
            }
        });

        holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPosition = position;
                showPopupMenu(holder.overflow);
            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_restaurant, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    //Toast.makeText(mContext, "See in google map", Toast.LENGTH_SHORT).show();
                    if(mContext instanceof MainActivity){
                        ((MainActivity)mContext).getViewPager().setCurrentItem(1);
                        ((MainActivity)mContext).moveToPoint(mPosition);
                    }
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}