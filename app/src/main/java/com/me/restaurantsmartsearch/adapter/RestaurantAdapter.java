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
import java.util.Calendar;

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
            holder.imStatus = (ImageView) convertView.findViewById(R.id.im_status);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        if(!restaurant.isValid()) return convertView;
        holder.tvTime.setText(restaurant.getTime());
        holder.tvName.setText(restaurant.getName());
        holder.tvType.setText(restaurant.getType().split("-")[0].split(",")[0]);
        holder.tvAddress.setText(restaurant.getAddress());

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
            holder.imStatus.setImageResource(R.drawable.ic_opening);
        }
        else {
            holder.imStatus.setImageResource(R.drawable.ic_closed);
        }

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
        ImageView imAvatar, imStatus;
    }
}
