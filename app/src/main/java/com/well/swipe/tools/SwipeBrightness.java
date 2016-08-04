package com.well.swipe.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.PowerManager;
import android.provider.Settings;

import com.well.swipe.R;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by mingwei on 3/27/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SwipeBrightness extends SwipeTools {

    private static final int LIGHT_NORMAL = 64;

    private static final int LIGHT_50_PERCENT = 127;

    private static final int LIGHT_75_PERCENT = 191;

    private static final int LIGHT_100_PERCENT = 255;

    private static final int LIGHT_AUTO = 0;

    private static final int LIGHT_ERR = -1;

    private volatile static SwipeBrightness mInstance;

    private PowerManager mPowerManager;

    private SwipeBrightness(Context context) {
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    }

    public static SwipeBrightness getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SwipeBrightness.class) {
                if (mInstance == null) {
                    mInstance = new SwipeBrightness(context);
                }
            }
        }
        return mInstance;
    }

    private int getBrightStatus(Context context) {

        int light = 0;
        boolean auto = false;
        try {
            auto = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
            if (!auto) {
                light = android.provider.Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, -1);
                if (light > 0 && light <= LIGHT_NORMAL) {
                    return LIGHT_NORMAL;
                } else if (light > LIGHT_NORMAL && light <= LIGHT_50_PERCENT) {
                    return LIGHT_50_PERCENT;
                } else if (light > LIGHT_50_PERCENT && light <= LIGHT_75_PERCENT) {
                    return LIGHT_75_PERCENT;
                } else if (light > LIGHT_75_PERCENT && light <= LIGHT_100_PERCENT) {
                    return LIGHT_100_PERCENT;
                }
            } else {
                return LIGHT_AUTO;
            }
        } catch (Settings.SettingNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return LIGHT_ERR;

    }

    public void setBrightStatus(Context context) {
        int light = 0;

        switch (getBrightStatus(context)) {
            case LIGHT_NORMAL:
                light = LIGHT_50_PERCENT - 1;
                break;
            case LIGHT_50_PERCENT:
                light = LIGHT_75_PERCENT - 1;
                break;
            case LIGHT_75_PERCENT:
                light = LIGHT_100_PERCENT - 1;
                break;
            case LIGHT_100_PERCENT:
                startAutoBrightness(context.getContentResolver());
                break;
            case LIGHT_AUTO:
                light = LIGHT_NORMAL - 1;
                stopAutoBrightness(context.getContentResolver());
                break;
            case LIGHT_ERR:
                light = LIGHT_NORMAL - 1;
                break;

        }

        setLight(light);
        setScreenLightValue(context.getContentResolver(), light);
    }

    private void setLight(int light) {
        try {
            /**
             * 得到PowerManager类对应的Class对象
             */
            Class<?> pmClass = Class.forName(mPowerManager.getClass().getName());
            /**
             * 得到PowerManager类中的成员mService（mService为PowerManagerService类型）
             */
            Field field = pmClass.getDeclaredField("mService");
            field.setAccessible(true);
            /**
             * 实例化mService
             */
            Object iPM = field.get(mPowerManager);
            /**
             * 得到PowerManagerService对应的Class对象
             */
            Class<?> iPMClass = Class.forName(iPM.getClass().getName());
            /**
             * 得到PowerManagerService的函数setBacklightBrightness对应的Method对象，
             * PowerManager的函数setBacklightBrightness实现在PowerManagerService中
             */
            Method method = iPMClass.getDeclaredMethod("setBacklightBrightness", int.class);
            method.setAccessible(true);
            /**
             * 调用实现PowerManagerService的setBacklightBrightness
             */
            method.invoke(iPM, light);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    /**
     * 启动自动调节亮度
     *
     * @param cr
     */
    public void startAutoBrightness(ContentResolver cr) {
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
    }

    /**
     * 关闭自动调节亮度
     *
     * @param cr
     */
    public void stopAutoBrightness(ContentResolver cr) {
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS_MODE,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
    }

    /**
     * 设置改变亮度值
     *
     * @param resolver
     * @param value
     */
    public void setScreenLightValue(ContentResolver resolver, int value) {
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
                value);
    }


    @Override
    public BitmapDrawable getDrawableState(Context context) {
        switch (getBrightStatus(context)) {
            case LIGHT_NORMAL:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_lightness_normal);
            case LIGHT_50_PERCENT:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_lightness_50);
            case LIGHT_75_PERCENT:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_lightness_75);
            case LIGHT_100_PERCENT:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_lightness_100);
            case LIGHT_AUTO:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_lightness_auto);
            case LIGHT_ERR:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_screen_lightness_normal);
        }
        return null;
    }

    @Override
    public String getTitleState(Context context) {
        switch (getBrightStatus(context)) {
            case LIGHT_NORMAL:
                return "nomal";
            case LIGHT_50_PERCENT:
                return "50";
            case LIGHT_75_PERCENT:
                return "75";
            case LIGHT_100_PERCENT:
                return "100";
            case LIGHT_AUTO:
                return "auto";
            case LIGHT_ERR:
                return "err";
        }
        return "";
    }
}
