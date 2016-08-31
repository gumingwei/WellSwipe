package com.well.swipe;

import android.net.Uri;

import com.well.swipecomm.SwipeSettings;

/**
 * Created by mingwei on 3/15/16.
 * <p/>
 * <p/>
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 */
public class SwipefreeSettings extends SwipeSettings {

    public static class Favorites {

        public static final Uri CONTENT_URI = Uri.parse("content://" + SwipefreeProvider.AUTHORITY + "/" + SwipefreeProvider.TABLE_FAVORITES);

        public static final Uri CONTENT_URI_WHITELIST = Uri.parse("content://" + SwipefreeProvider.AUTHORITY + "/" + SwipefreeProvider.TABLE_WHITELIST);

    }
}
