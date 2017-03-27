package com.me.restaurantsmartsearch.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.me.restaurantsmartsearch.R;
import com.me.restaurantsmartsearch.model.Restaurant;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Laptop88T on 11/16/2016.
 */
public class RestaurantAdapter extends BaseAdapter {
    ArrayList<Restaurant> list;
    Context mContext;

    public RestaurantAdapter(Context context, ArrayList<Restaurant> _list) {
        mContext = context;
        list = _list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Restaurant getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final Restaurant restaurant = list.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_restaurant, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.tv_address);
            holder.tvType = (TextView) convertView.findViewById(R.id.tv_type);
            holder.tvTime = (TextView) convertView.findViewById(R.id.tv_time);
            holder.imAvatar = (ImageView) convertView.findViewById(R.id.im_avatar);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if(!restaurant.isValid()) return convertView;
        holder.tvTime.setText(restaurant.getTime());
        holder.tvName.setText(restaurant.getName());
        holder.tvType.setText(restaurant.getType().split("-")[0].split(",")[0]);
        holder.tvAddress.setText(restaurant.getAddress());
        if(!TextUtils.isEmpty(restaurant.getImage())){
            Picasso.with(mContext)
                    .load(restaurant.getImage())
                    .placeholder(R.drawable.im_avatar)
                    .error(R.drawable.im_avatar)
                    .into(holder.imAvatar);
        }
        else {
            holder.imAvatar.setImageResource(R.drawable.im_avatar);
        }
        return convertView;
    }

    public void setRestaurant(ArrayList<Restaurant> restaurant) {
        this.list = restaurant;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView tvName, tvAddress, tvType, tvTime;
        ImageView imAvatar;
    }
}
