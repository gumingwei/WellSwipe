package com.well.swipe;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.os.*;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.well.swipe.service.SwipeService;

import java.lang.ref.WeakReference;

/**
 * Created by mingwei on 2/29/16.
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class SwipefreeApplication extends Application {

    private LauncherModel mModel;

    private IconCache mIconCache;

    private WeakReference<SwipefreeProvider> mSwipeProvider;

    /**
     * GoogleAnlytatic
     */
    private Tracker mTracker;

    @Override
    public void onCreate() {
        super.onCreate();
        mIconCache = new IconCache(this);
        mModel = new LauncherModel(this, mIconCache);
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

        ContentResolver resolver = getContentResolver();
        resolver.registerContentObserver(SwipeSettings.Favorites.CONTENT_URI, true,
                mFavoritesObserver);

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterReceiver(mModel);
    }

    public LauncherModel setLaunchr(SwipeService service) {
        mModel.initCallBack(service);
        return mModel;
    }

    public void setProvider(SwipefreeProvider provider) {
        mSwipeProvider = new WeakReference<>(provider);
    }

    public SwipefreeProvider getProvider() {
        return mSwipeProvider.get();
    }

    private final ContentObserver mFavoritesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            // If the database has ever changed, then we really need to force a reload of the
            // workspace on the next load
            //mModel.resetLoadedState(false, true);
            //mModel.startLoaderFromBackground();
        }
    };

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            mTracker = analytics.newTracker(R.xml.global_tracker);
        }
        return mTracker;
    }
}
