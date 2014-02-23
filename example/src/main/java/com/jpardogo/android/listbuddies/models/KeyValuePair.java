package com.jpardogo.android.listbuddies.models;

import android.content.Context;
import android.graphics.Color;

import com.jpardogo.android.listbuddies.R;
import com.jpardogo.android.listbuddies.adapters.CustomizeSpinnersAdapter;

import java.util.Random;

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
        int color = -1;
        if (value instanceof CustomizeSpinnersAdapter.OptionTypes) {
            switch ((CustomizeSpinnersAdapter.OptionTypes) value) {
                case BLACK:
                    color = context.getResources().getColor(R.color.black);
                    break;
                case RANDOM:
                    Random rnd = new Random();
                    color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
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
