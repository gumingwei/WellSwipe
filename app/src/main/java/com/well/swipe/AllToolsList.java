package com.well.swipe;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by mingwei on 3/25/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 * 所有Tools数据
 */
public class AllToolsList {

    public ArrayList<ItemSwipeTools> mSwipeDataList;

    public String mSwipeActionArrAy[];

    public String mSwipeTitleArrAy[];

    public AllToolsList(Context context) {
        mSwipeDataList = new ArrayList<>();
        mSwipeActionArrAy = context.getResources().getStringArray(R.array.swipe_tools_action_array);
        mSwipeTitleArrAy = context.getResources().getStringArray(R.array.swipe_tools_title_array);
        for (int i = 0; i < mSwipeActionArrAy.length; i++) {
            ItemSwipeTools itemswitch = new ItemSwipeTools();
            itemswitch.mTitle = mSwipeTitleArrAy[i];
            itemswitch.mAction = mSwipeActionArrAy[i];
            mSwipeDataList.add(itemswitch);
        }
    }

    public void printF() {
        for (int i = 0; i < mSwipeDataList.size(); i++) {
            
        }
    }

}
