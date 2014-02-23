package com.jpardogo.android.listbuddies.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.models.KeyValuePair;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.Optional;

/**
 * Created by jpardogo on 22/02/2014.
 */
public class CustomizeSpinnersAdapter implements SpinnerAdapter {
    private Context mContext;
    private List<KeyValuePair> mItems;
    private String mMainItemTitle;

    public CustomizeSpinnersAdapter(Context context, List<KeyValuePair> items, String mainTitle) {
        mContext = context;
        mItems = items;
        mMainItemTitle = mainTitle;
    }

    public enum OptionTypes {
        BLACK,
        EMPTY,
        INSET;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_dropdown, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(mItems.get(position).getKey());
        return convertView;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.spinner_item_main, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.title.setText(mMainItemTitle);
        holder.subtitle.setText(mItems.get(position).getKey());
        return convertView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    static class ViewHolder {
        @InjectView(R.id.title)
        TextView title;
        @Optional
        @InjectView(R.id.subtitle)
        TextView subtitle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}
