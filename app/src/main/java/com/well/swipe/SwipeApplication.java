package com.well.swipe;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.lang.ref.WeakReference;

/**
 * Created by mingwei on 2/29/16.
 */
public class SwipeApplication extends Application {

    private LauncherModel mModel;

    private WeakReference<SwipeProvider> mSwipeProvider;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("Gmw", "SwipeApplication");
        mModel = new LauncherModel(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addDataScheme("package");
        registerReceiver(mModel, filter);
        filter = new IntentFilter();
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
        filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
        filter.addAction(Intent.ACTION_LOCALE_CHANGED);
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        registerReceiver(mModel, filter);


    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(mModel);
    }

    public void setProvider(SwipeProvider provider) {
        mSwipeProvider = new WeakReference<SwipeProvider>(provider);
    }

    public SwipeProvider getProvider() {
        return mSwipeProvider.get();
    }
}
