package com.me.restaurantsmartsearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.me.restaurantsmartsearch.R;

import java.util.ArrayList;

/**
 * Created by Laptop88T on 1/5/2017.
 */
public class SuggestAdapter extends BaseAdapter {
    ArrayList<String> list = new ArrayList<>();
    Context mContext;

    public SuggestAdapter(Context context, ArrayList<String> _list) {
        mContext = context;
        list = _list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public String getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        final String string = list.get(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.row_suggest, null);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);
            holder.imArrow = (ImageView) convertView.findViewById(R.id.im_arrow);
            convertView.setTag(holder);
        }
        holder = (ViewHolder) convertView.getTag();
        holder.tvName.setText(string);
        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
        ImageView imArrow;
    }

    public void setData(ArrayList<String> restaurant) {
        this.list = restaurant;
        notifyDataSetChanged();
    }
}
