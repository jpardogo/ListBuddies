package com.jpardogo.android.listbuddies.models;

import android.content.Context;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.adapters.CustomizeSpinnersAdapter;
import com.jpardogo.listbuddies.lib.views.ListBuddiesLayout;

/**
 * Created by jpardogo on 22/02/2014.
 */
public class KeyValuePair {
    private String key;
    private Object value;

    public KeyValuePair(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public int getColor(Context context) {
        int color = ListBuddiesLayout.ATTR_NOT_SET;
        if (value instanceof CustomizeSpinnersAdapter.OptionTypes) {
            switch ((CustomizeSpinnersAdapter.OptionTypes) value) {
                case BLACK:
                    color = context.getResources().getColor(R.color.black);
                    break;
                case INSET:
                    color = context.getResources().getColor(R.color.inset);
                    break;
            }
        } else {
            throw new ClassCastException("Wrong type of value, the value must be CustomizeSpinnersAdapter.OptionTypes");
        }
        return color;
    }

    public String getKey() {
        return key;
    }


    public int getScrollOption() {
        return (Integer) value;
    }
}
