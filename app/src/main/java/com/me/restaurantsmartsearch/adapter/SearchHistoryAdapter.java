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
 * Created by H110 on 11/3/2016.
 */
public class SearchHistoryAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private IOnCellListView mCallBackListener;
    private ArrayList<String> data;
    private int mPosition;

    public interface IOnCellListView {
        void onRightClick(String sSearch);

        void onMidClick(String sSearch, int position);

        void onLeftClick(String sSearch);
    }

    public SearchHistoryAdapter(Context context, ArrayList<String> _data, IOnCellListView iOnCellListView) {
        mInflater = LayoutInflater.from(context);
        data = _data;
        mCallBackListener = iOnCellListView;
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textContent.setText(data.get(position));
        holder.imageSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBackListener.onLeftClick(data.get(position));
            }
        });
        holder.imageFillKey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBackListener.onRightClick(data.get(position));
            }
        });
        holder.textContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCallBackListener.onMidClick(data.get(position), position);
            }
        });
        return convertView;
    }

    public void setData(ArrayList<String> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    private class ViewHolder {
        TextView textContent;
        ImageView imageFillKey, imageSearch;

        public ViewHolder(View view) {
            textContent = (TextView) view.findViewById(R.id.tvContent);
            imageFillKey = (ImageView) view.findViewById(R.id.ivFillText);
            imageSearch = (ImageView) view.findViewById(R.id.ivSearch);
        }
    }

    public void setCallBackListener(IOnCellListView mCallBackListener) {
        this.mCallBackListener = mCallBackListener;
    }
}

