package com.well.swipe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by mingwei on 3/31/16.
 */
public class SettingHelper {

    private volatile static SettingHelper mInstance;

    Context mContext;

    public static final String PREFERENCE_NAME = "com.well.swipe.setting";

    private SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;

    private SettingHelper(Context context) {
        mContext = context;
        mSharedPreferences = mContext.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static SettingHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SettingHelper.class) {
                if (mInstance == null) {
                    mInstance = new SettingHelper(context);
                }
            }
        }
        return mInstance;
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    public String getString(String key) {
        return mSharedPreferences.getString(key, "");
    }

    public void putInt(String key, int value) {
        mEditor.putInt(key, value);
        mEditor.commit();
    }

    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    public int getInt(String key, int defvalue) {
        return mSharedPreferences.getInt(key, defvalue);
    }

    public void putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        mEditor.commit();
    }

    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {
        return mSharedPreferences.getBoolean(key, def);
    }


}
