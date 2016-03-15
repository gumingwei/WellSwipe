package com.well.swipe;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;


/**
 * Created by mingwei on 3/14/16.
 */
public class LauncherModel extends BroadcastReceiver {

    public LoadTask mLoadTask = new LoadTask();

    private SwipeApplication mApplication;

    public android.os.Handler mThreadHandel = new android.os.Handler(Looper.getMainLooper());

    public LauncherModel(SwipeApplication app) {
        mApplication = app;
        mLoadTask.run();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i("Gmw", "onReceive=" + action);

    }

    private class LoadTask implements Runnable {
        public LoadTask() {

        }

        @Override
        public void run() {
            loadWorkspace();
        }

        private void loadWorkspace() {
            mApplication.getProvider().loadDefaultFavoritesIfNecessary(R.xml.default_workspace);
        }
    }
}
