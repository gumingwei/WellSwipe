package com.well.swipe.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.well.swipecomm.utils.SettingHelper;

/**
 * Created by mingwei on 3/30/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SwipePreference extends RelativeLayout {

    private static final String SETTING_PREFERENCE = SettingHelper.PREFERENCE_NAME;

    private String mKey;

    private SharedPreferences mPreference;

    private SharedPreferences.Editor mEditor;

    public SwipePreference(Context context) {
        this(context, null);
    }

    public SwipePreference(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipePreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPreference = context.getSharedPreferences(SETTING_PREFERENCE, Context.MODE_PRIVATE);
        mEditor = mPreference.edit();
    }

    public void setKey(String key) {
        mKey = key;
    }

    public void setValues(int value) {
        mEditor.putInt(mKey, value);
        mEditor.commit();
    }

    public void setValues(boolean value) {
        mEditor.putBoolean(mKey, value);
        mEditor.commit();
    }

    public void setValues(String value) {
        mEditor.putString(mKey, value);
        mEditor.commit();
    }

    public String getStringValue() {
        return mPreference.getString(mKey, "");
    }

    public int getIntValue() {
        return mPreference.getInt(mKey, 0);
    }

    public int getIntValue(int def) {
        return mPreference.getInt(mKey, def);
    }

    public boolean getBooleanValue() {
        return mPreference.getBoolean(mKey, false);
    }

    public boolean getBooleanValue(boolean def) {
        return mPreference.getBoolean(mKey, def);
    }

    public String getKey() {
        return mKey;
    }
}
