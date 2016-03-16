package com.well.swipe;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


/**
 * Created by mingwei on 3/14/16.
 */
public class LauncherModel extends BroadcastReceiver {
    /**
     * 加载default_workspace
     */
    public LoadTask mLoadTask;
    /**
     * 应用Application注册LauncherModel
     */
    private SwipeApplication mApplication;
    /**
     * 所有的app数据
     */
    private AllAppsList mAllAppsList;
    /**
     * 图片缓存
     */
    private IconCache mIconCache;

    private final HandlerThread mWorkerThread = new HandlerThread("swipe-load");

    private final Handler mWorker = new Handler();

    private Callback mCallBack;

    private WeakReference<Callback> mCallback;

    public interface Callback {
        /**
         * 绑定所有的app数据
         *
         * @param appslist
         */
        void bindAllApps(List<ItemApplication> appslist);

        /**
         * 绑定要在swipe上显示的app
         *
         * @param appslist applist
         */
        void bindFavorites(List<ItemApplication> appslist);
    }

    public LauncherModel(SwipeApplication app, IconCache iconCache) {
        mApplication = app;
        mAllAppsList = new AllAppsList(iconCache);
        mIconCache = iconCache;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

    }

    public void initCallBack(Callback callback) {
        mCallback = new WeakReference<>(callback);
    }

    public void startLoadTask() {
        mLoadTask = new LoadTask(mApplication);
        mLoadTask.run();
    }

    static ComponentName getComponentNameFromResolveInfo(ResolveInfo info) {
        if (info.activityInfo != null) {
            return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
        } else {
            return new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name);
        }
    }

    private class LoadTask implements Runnable {

        private Context mContext;

        private HashMap<Object, CharSequence> mLabelCache;

        public LoadTask(Context context) {
            mContext = context;
            mLabelCache = new HashMap<>();
        }

        @Override
        public void run() {
            loadWorkspace();
            loadFavorites();
            loadAndBindAllApps();
        }

        private void loadWorkspace() {
            mApplication.getProvider().loadDefaultFavoritesIfNecessary(R.xml.default_workspace);
        }

        private void loadAndBindAllApps() {
            Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            PackageManager manager = mContext.getPackageManager();
            List<ResolveInfo> mInfoLists = manager.queryIntentActivities(mainIntent, 0);
            Collections.sort(mInfoLists,
                    new LauncherModel.ShortcutNameComparator(manager, mLabelCache));

            for (int i = 0; i < mInfoLists.size(); i++) {
                mAllAppsList.data.add(new ItemApplication(manager, mInfoLists.get(i), mIconCache, mLabelCache));
            }
            ArrayList<ItemApplication> applications = new ArrayList<>();
            if (applications.size() != 0) {
                applications.clear();
            }
            applications.addAll(mAllAppsList.data);
            mCallback.get().bindAllApps(applications);

        }

        private void loadFavorites() {
            Log.i("Gmw", "loadFavorites");
            ContentResolver resolver = mContext.getContentResolver();
            Cursor cursor = resolver.query(SwipeSettings.Favorites.CONTENT_URI, null, null, null, null);
            Log.i("Gmw", "loadFavorites_count=" + cursor.getCount());
        }

    }

    private class PackageUpdataTask implements Runnable {

        @Override
        public void run() {

        }
    }

    public static class ShortcutNameComparator implements Comparator<ResolveInfo> {
        private Collator mCollator;
        private PackageManager mPackageManager;
        private HashMap<Object, CharSequence> mLabelCache;

        ShortcutNameComparator(PackageManager pm) {
            mPackageManager = pm;
            mLabelCache = new HashMap<Object, CharSequence>();
            mCollator = Collator.getInstance();
        }

        ShortcutNameComparator(PackageManager pm, HashMap<Object, CharSequence> labelCache) {
            mPackageManager = pm;
            mLabelCache = labelCache;
            mCollator = Collator.getInstance();
        }

        public final int compare(ResolveInfo a, ResolveInfo b) {
            CharSequence labelA, labelB;
            ComponentName keyA = LauncherModel.getComponentNameFromResolveInfo(a);
            ComponentName keyB = LauncherModel.getComponentNameFromResolveInfo(b);
            if (mLabelCache.containsKey(keyA)) {
                labelA = mLabelCache.get(keyA);
            } else {
                labelA = a.loadLabel(mPackageManager).toString();

                mLabelCache.put(keyA, labelA);
            }
            if (mLabelCache.containsKey(keyB)) {
                labelB = mLabelCache.get(keyB);
            } else {
                labelB = b.loadLabel(mPackageManager).toString();

                mLabelCache.put(keyB, labelB);
            }
            return mCollator.compare(labelA, labelB);
        }
    }

    ;
}
