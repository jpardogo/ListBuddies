package com.jpardogo.android.listbuddies.Utils;

import android.content.SharedPreferences;

import com.jpardogo.android.listbuddies.ListBuddies;
import com.jpardogo.android.listbuddies.provider.SharedPrefFiles;
import com.jpardogo.android.listbuddies.provider.SharedPrefKeys;

/**
 * Created by jpardogo on 23/02/2014.
 */
public class SharePreferences {

    public static void saveCustomization(SharedPrefKeys prefKey, int progress) {
        SharedPreferences customize_pref = getCustomizePref();
        SharedPreferences.Editor editor = customize_pref.edit();
        editor.putInt(prefKey.toString(), progress);
        editor.commit();
    }

    public static int getValue(SharedPrefKeys prefKey) {
        SharedPreferences customize_pref = getCustomizePref();
        return customize_pref.getInt(prefKey.toString(), 0);
    }

    private static SharedPreferences getCustomizePref() {
        return ListBuddies.getAppContext().getSharedPreferences(SharedPrefFiles.CUSTOMIZE_SETTINGS.toString(), 0);
    }

    public static void reset() {
        for (SharedPrefKeys keys : SharedPrefKeys.values()) {
            saveCustomization(keys, 0);
        }
    }
}
