package com.jpardogo.listbuddies.lib.provider;

/**
 * Created by jpardogo on 23/02/2014.
 */
public enum ScrollConfigOptions {
    LEFT,
    RIGHT;

    public int getConfigValue() {
        return ordinal() + 1;
    }
}
