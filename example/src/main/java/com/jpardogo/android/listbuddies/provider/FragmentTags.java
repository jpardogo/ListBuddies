package com.jpardogo.android.listbuddies.provider;

/**
 * Created by jpardogo on 22/02/2014.
 */
public enum FragmentTags {
    LIST_BUDDIES("ListBuddiesFragment"),
    CUSTOMIZE("CustomizeFragment");

    private String text;

    private FragmentTags(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
