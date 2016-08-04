package com.well.swipe;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mingwei on 3/16/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class AllAppsList {

    public static final int DEFAULT_APPLICATIONS_NUMBER = 42;

    /**
     * The list off all apps.
     */
    public ArrayList<ItemApplication> data =
            new ArrayList<>(DEFAULT_APPLICATIONS_NUMBER);
    /**
     * The list of apps that have been added since the last notify() call.
     */
    public ArrayList<ItemApplication> added =
            new ArrayList<>(DEFAULT_APPLICATIONS_NUMBER);
    /**
     * The list of apps that have been removed since the last notify() call.
     */
    public ArrayList<ItemApplication> removed = new ArrayList<>();
    /**
     * The list of apps that have been modified since the last notify() call.
     */
    public ArrayList<ItemApplication> modified = new ArrayList<>();
    /**
     * HomeAppList
     */
    public ArrayList<ItemApplication> homeapps = new ArrayList<>();

    private IconCache mIconCache;

    /**
     * Boring constructor.
     */
    public AllAppsList(IconCache iconCache) {
        mIconCache = iconCache;
    }

    /**
     * Add the supplied ApplicationInfo objects to the list, and enqueue it into the
     * list to broadcast when notify() is called.
     * <p/>
     * If the app is already in the list, doesn't add it.
     */
    public void add(ItemApplication info) {
        if (findActivity(data, info.mComponentName)) {
            return;
        }
        data.add(info);
        added.add(info);
    }

    public void addHomePackage(ItemApplication info) {
        if (findActivity(homeapps, info.mComponentName)) {
            return;
        }
        homeapps.add(info);
    }

    public void clear() {
        data.clear();
        // TODO: do we clear these too?
        added.clear();
        removed.clear();
        modified.clear();
    }

    public int size() {
        return data.size();
    }

    public ItemApplication get(int index) {
        return data.get(index);
    }

    /**
     * Add the icons for the supplied apk called packageName.
     */
    public void addPackage(Context context, String packageName) {
        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);

        if (matches.size() > 0) {
            for (ResolveInfo info : matches) {
                add(new ItemApplication(context.getPackageManager(), info, mIconCache, null));
            }
        }
    }

    public void addHomePackage(Context context) {
        List<ResolveInfo> list = findHomePackage(context);
        if (list.size() > 0) {
            for (ResolveInfo info : list) {
                addHomePackage(new ItemApplication(context.getPackageManager(), info, mIconCache, null));
            }
        }
    }


    /**
     * Remove the apps for the given apk identified by packageName.
     */
    public void removePackage(String packageName) {
        final List<ItemApplication> data = this.data;
        for (int i = data.size() - 1; i >= 0; i--) {
            ItemApplication info = data.get(i);
            final ComponentName component = info.mIntent.getComponent();
            if (packageName.equals(component.getPackageName())) {
                removed.add(info);
                data.remove(i);
            }
        }
        // This is more aggressive than it needs to be.
        mIconCache.flush();
    }

    /**
     * Add and remove icons for this package which has been updated.
     */
    public void updatePackage(Context context, String packageName) {
        final List<ResolveInfo> matches = findActivitiesForPackage(context, packageName);
        if (matches.size() > 0) {
            // Find disabled/removed activities and remove them from data and add them
            // to the removed list.
            for (int i = data.size() - 1; i >= 0; i--) {
                final ItemApplication applicationInfo = data.get(i);
                final ComponentName component = applicationInfo.mIntent.getComponent();
                if (packageName.equals(component.getPackageName())) {
                    if (!findActivity(matches, component)) {
                        removed.add(applicationInfo);
                        mIconCache.remove(component);
                        data.remove(i);
                    }
                }
            }

            // Find enabled activities and add them to the adapter
            // Also updates existing activities with new labels/icons
            int count = matches.size();
            for (int i = 0; i < count; i++) {
                final ResolveInfo info = matches.get(i);
                ItemApplication applicationInfo = findApplicationInfoLocked(
                        info.activityInfo.applicationInfo.packageName,
                        info.activityInfo.name);
                if (applicationInfo == null) {
                    add(new ItemApplication(context.getPackageManager(), info, mIconCache, null));
                } else {
                    mIconCache.remove(applicationInfo.mComponentName);
                    mIconCache.getTitleAndIcon(applicationInfo, info, null);
                    modified.add(applicationInfo);
                }
            }
        } else {
            // Remove all data for this package.
            for (int i = data.size() - 1; i >= 0; i--) {
                final ItemApplication applicationInfo = data.get(i);
                final ComponentName component = applicationInfo.mIntent.getComponent();
                if (packageName.equals(component.getPackageName())) {
                    removed.add(applicationInfo);
                    mIconCache.remove(component);
                    data.remove(i);
                }
            }
        }
    }

    /**
     * Query the package manager for MAIN/LAUNCHER activities in the supplied package.
     */
    private static List<ResolveInfo> findActivitiesForPackage(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();

        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mainIntent.setPackage(packageName);

        final List<ResolveInfo> apps = packageManager.queryIntentActivities(mainIntent, 0);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em>.
     */
    private static boolean findActivity(List<ResolveInfo> apps, ComponentName component) {
        final String className = component.getClassName();
        for (ResolveInfo info : apps) {
            final ActivityInfo activityInfo = info.activityInfo;
            if (activityInfo.name.equals(className)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether <em>apps</em> contains <em>component</em>.
     */
    private static boolean findActivity(ArrayList<ItemApplication> apps, ComponentName component) {
        final int N = apps.size();
        for (int i = 0; i < N; i++) {
            final ItemApplication info = apps.get(i);
            if (info.mComponentName.equals(component)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find an ApplicationInfo object for the given packageName and className.
     */
    private ItemApplication findApplicationInfoLocked(String packageName, String className) {
        for (ItemApplication info : data) {
            final ComponentName component = info.mIntent.getComponent();
            if (packageName.equals(component.getPackageName())
                    && className.equals(component.getClassName())) {
                return info;
            }
        }
        return null;
    }

    /**
     * 查找Homeapp
     *
     * @param context
     * @return
     */
    private static List<ResolveInfo> findHomePackage(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return apps != null ? apps : new ArrayList<ResolveInfo>();
    }

}
