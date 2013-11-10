package com.jpardogo.android.listbuddies.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.jpardogo.android.listbuddies.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jpardogo on 07/11/2013.
 */
public class CircularLoopAdapater extends ArrayAdapter<String> {

    private ArrayList<String> mItems = new ArrayList<String>();
    ;
    private Context mContext;

    public CircularLoopAdapater(Context context, String[] imagesUrl) {
        super(context, 0);
        mContext = context;

        initArray(imagesUrl);

    }

    /***
     * Init array with the images urls
     * @param imageUrls
     */
    private void initArray(String[] imageUrls) {
        int max = imageUrls.length;
        for (int i = 0; i < max; i++) {
            mItems.add(imageUrls[i]);
        }
    }

    /**
     * In getCount(), we simply return Integer.MAX_VALUE, it will give you about 2 billion items,
     * which should be enough to look like infinite.
     *
     * We can see the answer to the question on here where Romain Guy confirm this solution:
     *
     * http://stackoverflow.com/questions/2332847/how-to-create-a-closed-circular-listview
     *
     */
    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public String getItem(int position) {
        return mItems.get(getPosition(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list, parent, false);

            holder.image = (ImageView) convertView.findViewById(R.id.image);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.image.setImageBitmap(null);
        Picasso.with(mContext).load(getItem(position)).into(holder.image);

        return convertView;
    }

    /**
     * Gets the position that correspond to the position in the amount of items we actually have
     * @param position
     * @return
     */
    private int getPosition(int position) {
        return position % mItems.size();
    }

    static class ViewHolder {
        ImageView image;
    }

}
