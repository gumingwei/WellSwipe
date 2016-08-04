package com.well.swipe.tools;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;

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
public class WifiAndData {

    public static final int TYPE_WIFI = 1;

    public static final int TYPE_MOBILE = 2;

    public static final int TYPE_NOT_CONNECTED = 0;

    private static final String WIFI_ID = "WI-FI";

    public static void setMobileDataEnabled(Context context, boolean enabled) {
        try {
            final ConnectivityManager conman = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final Class conmanClass = Class
                    .forName(conman.getClass().getName());
            final Field connectivityManagerField = conmanClass
                    .getDeclaredField("mService");
            connectivityManagerField.setAccessible(true);
            final Object connectivityManager = connectivityManagerField
                    .get(conman);
            final Class connectivityManagerClass = Class
                    .forName(connectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = connectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);
            setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            tryToSetMobileDataEnabled(context, enabled);
        }
    }

    public static void tryToSetMobileDataEnabled(Context context, boolean arg0) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        Class<?> mClass = mConnectivityManager.getClass();
        Class<?>[] mClass2 = new Class[1];
        mClass2[0] = boolean.class;
        try {
            Method method = mClass.getMethod("setMobileDataEnabled", mClass2);
            method.invoke(mConnectivityManager, arg0);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static boolean isMobileDataEnable(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mobileDataEnabled;
    }

    public static boolean isWifiEnable(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    public static void setWifiEnable(Context context, boolean enable) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enable);
    }

    public static int getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return TYPE_WIFI;

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return TYPE_MOBILE;
        }
        return TYPE_NOT_CONNECTED;
    }

    public static String getWifiID(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        String wifiID = info.getSSID();

        if (TextUtils.isEmpty(wifiID) || wifiID.equals("0x")
                || wifiID.equals("<unknown ssid>")) {
            wifiID = WIFI_ID;
        }
        return wifiID;
    }

    public static BitmapDrawable getWifiDrawableState(Context context) {
        if (isWifiEnable(context)) {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_wifi_on);
        } else {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_wifi_off);
        }
    }

    public static BitmapDrawable getDataDrawableState(Context context) {
        if (isMobileDataEnable(context)) {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_mobile_network_off);
        } else {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_mobile_network_on);
        }
    }
}
