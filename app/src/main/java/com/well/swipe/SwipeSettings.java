package com.well.swipe;

import android.net.Uri;

/**
 * Created by mingwei on 3/15/16.
 */
public class SwipeSettings {

    static class Favorites {

        static final String ITEM_INDEX = "item_index";

        static final String ITEM_TITLE = "item_title";

        static final String ITEM_URI = "item_uri";

        static final String ITEM_TEST = "item_test";

        static final String ITEM_INTENT = "item_intent";

        static final String ITEM_TYPE = "item_type";

        static final int ITEM_TYPE_APPLICATION = 1;

        static final int ITEM_TYPE_SWITCH = 2;

        static final String ITEM_ACTION = "item_action";

        static final String ITEM_ICON = "item_icon";

        static final Uri CONTENT_URI = Uri.parse("content://" + SwipeProvider.AUTHORITY + "/" + SwipeProvider.TABLE_FAVORITES);
    }
}
