package com.jpardogo.android.listbuddies.provider;

/**
 * Created by jpardogo on 22/02/2014.
 */
public enum ExtraArgumentKeys {
    OPEN_ACTIVITES("OPEN_ACTIVITES");

    private String text;

    private ExtraArgumentKeys(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
