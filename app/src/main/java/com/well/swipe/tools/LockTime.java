package com.well.swipe.tools;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/28/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class LockTime extends SwipeTools {

    private static final int TIEM_15 = 15 * 1000;

    private static final int TIEM_30 = 30 * 1000;

    private static final int TIEM_60 = 60 * 1000;

    private static final int TIEM_120 = 120 * 1000;

    private static final int TIEM_300 = 300 * 1000;

    private static final int TIEM_600 = 600 * 1000;

    private static final int TIEM_1800 = 1800 * 1000;

    private volatile static LockTime mInstance;

    private LockTime() {
    }

    public static LockTime getInstance() {
        if (mInstance == null) {
            synchronized (LockTime.class) {
                if (mInstance == null) {
                    mInstance = new LockTime();
                }
            }
        }
        return mInstance;
    }

    private int getScreenOffTime(Context context) {
        int screenOffTime = 0;
        try {
            screenOffTime = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return screenOffTime;
    }

    private void setScreenOffTime(int paramInt, Context context) {
        try {
            Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    public int getState(Context context) {
        int value = getScreenOffTime(context);
        if (0 < value && value <= TIEM_15) {
            return TIEM_15;
        } else if (TIEM_15 < value && value <= TIEM_30) {
            return TIEM_30;
        } else if (TIEM_30 < value && value <= TIEM_60) {
            return TIEM_60;
        } else if (TIEM_60 < value && value <= TIEM_120) {
            return TIEM_120;
        } else if (TIEM_120 < value && value <= TIEM_300) {
            return TIEM_300;
        } else if (TIEM_300 < value && value <= TIEM_600) {
            return TIEM_600;
        } else if (TIEM_600 < value && value <= TIEM_1800) {
            return TIEM_1800;
        }
        return -1;
    }

    public void changeState(Context context) {
        switch (getState(context)) {
            case TIEM_15:
                setScreenOffTime(TIEM_30, context);
                break;
            case TIEM_30:
                setScreenOffTime(TIEM_60, context);
                break;
            case TIEM_60:
                setScreenOffTime(TIEM_120, context);
                break;
            case TIEM_120:
                setScreenOffTime(TIEM_300, context);
                break;
            case TIEM_300:
                setScreenOffTime(TIEM_600, context);
                break;
            case TIEM_600:
                setScreenOffTime(TIEM_1800, context);
                break;
            case TIEM_1800:
                setScreenOffTime(TIEM_15, context);
                break;
        }
    }

    @Override
    public BitmapDrawable getDrawableState(Context context) {
        switch (getState(context)) {
            case TIEM_15:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_15s);
            case TIEM_30:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_30s);
            case TIEM_60:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_60s);
            case TIEM_120:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_2m);
            case TIEM_300:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_5m);
            case TIEM_600:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_10m);
            case TIEM_1800:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_30m);
        }
        return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_timeout_15s);
    }

    @Override
    public String getTitleState(Context context) {
        switch (getState(context)) {
            case TIEM_15:
                return context.getResources().getString(R.string.time_15);
            case TIEM_30:
                return context.getResources().getString(R.string.time_30);
            case TIEM_60:
                return context.getResources().getString(R.string.time_60);
            case TIEM_120:
                return context.getResources().getString(R.string.time_120);
            case TIEM_300:
                return context.getResources().getString(R.string.time_300);
            case TIEM_600:
                return context.getResources().getString(R.string.time_600);
            case TIEM_1800:
                return context.getResources().getString(R.string.time_1800);
        }
        return "";
    }
}

