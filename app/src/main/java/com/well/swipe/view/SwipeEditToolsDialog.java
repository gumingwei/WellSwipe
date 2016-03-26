package com.well.swipe.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.well.swipe.ItemApplication;
import com.well.swipe.ItemSwipeSwitch;
import com.well.swipe.R;

import java.util.ArrayList;

/**
 * Created by mingwei on 3/25/16.
 */
public class SwipeEditToolsDialog extends SwipeDialog implements View.OnClickListener {
    /**
     * 内部的装item的
     */
    private GridLayout mGridLayout;

    private ArrayList<ItemSwipeSwitch> mDatalist;

    private ArrayList<ItemSwipeSwitch> mFixedList;

    private ArrayList<ItemSwipeSwitch> mSelectedList;

    public SwipeEditToolsDialog(Context context) {
        this(context, null);
    }

    public SwipeEditToolsDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeEditToolsDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPositiveBtn.setOnClickListener(this);
        mNegativeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mPositiveBtn) {
            mOnDialogListener.onPositive(this);
        } else if (v == mNegativeBtn) {
            mOnDialogListener.onNegative(this);
        } else if (v instanceof GridLayoutItemView) {
            /**
             * 从tag中拿到当前点击的是那一项，然后改变mFixedDataList的数据
             * 刷新视图
             */
            int index = (int) v.getTag();
            if (mFixedList.get(index).isChecked) {
                mFixedList.get(index).isChecked = false;
                refreshView();
            } else {
                if (getNewSelectList().size() < 9) {
                    mFixedList.get(index).isChecked = true;
                    refreshView();
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.favorite_up_to_9),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public View createContentView() {
        mGridLayout = new GridLayout(getContext());
        mGridLayout.setColumnCount(4);
        return mGridLayout;
    }

    /**
     * 全部的数据
     *
     * @param switches
     */
    public void setGridData(ArrayList<ItemSwipeSwitch> switches) {
        mDatalist = new ArrayList<>();
        mDatalist.addAll(switches);
    }

    /**
     * 已经选中的数据
     *
     * @param switchs
     */
    public void setSelectedData(ArrayList<ItemSwipeSwitch> switchs) {
        mSelectedList = new ArrayList<>();
        mSelectedList.addAll(switchs);
        refreshGrid();
    }

    /**
     * 刷新GridLayout
     */
    public void refreshGrid() {
        mGridLayout.removeAllViews();

        ArrayList<ItemSwipeSwitch> select = new ArrayList<>();
        ArrayList<ItemSwipeSwitch> normal = new ArrayList<>();

        if (mDatalist != null && mDatalist.size() > 0) {
            for (int i = 0; i < mDatalist.size(); i++) {
                if (contains(mSelectedList, mDatalist.get(i))) {
                    select.add(mDatalist.get(i));
                } else {
                    normal.add(mDatalist.get(i));
                }
            }
        }

        mFixedList = new ArrayList<>();
        /**
         * 把select、normal的数据合并到mFixedList中
         */
        merge(select, true);
        merge(normal, false);
        /**
         * 刷新界面
         */
        refreshView();
    }

    /**
     * 判定一个item是否在List中
     *
     * @param switches    目标itemlist
     * @param swipeSwitch 目标
     * @return
     */
    public boolean contains(ArrayList<ItemSwipeSwitch> switches, ItemSwipeSwitch swipeSwitch) {
        for (int i = 0; i < switches.size(); i++) {
            if (switches.get(i).mAction.equals(swipeSwitch.mAction)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 遍历list生成View
     */
    public void refreshView() {
        mGridLayout.removeAllViews();
        for (int i = 0; i < mFixedList.size(); i++) {
            final GridLayoutItemView itemview = (GridLayoutItemView) LayoutInflater.from(getContext())
                    .inflate(R.layout.gridlayout_item_layout, null);
            itemview.setTag(i);
            itemview.setItemTitle(mFixedList.get(i).mTitle.toString());
            itemview.setOnClickListener(this);
            itemview.setChecked(mFixedList.get(i).isChecked);
            mGridLayout.addView(itemview, new LinearLayout.LayoutParams(mSize, mSize));
        }
        mDialogTitle.setText(String.format(mTitleFormat, String.valueOf(getNewSelectList().size()), "9"));
    }

    /**
     * 合并list
     *
     * @param list
     * @param checked
     */
    public void merge(ArrayList<ItemSwipeSwitch> list, boolean checked) {
        for (int i = 0; i < list.size(); i++) {
            ItemSwipeSwitch item = list.get(i);
            item.isChecked = checked;
            mFixedList.add(item);
        }
    }

    /**
     * 从FixedList中读取到选中的list
     *
     * @return
     */
    public ArrayList<ItemSwipeSwitch> getNewSelectList() {
        ArrayList<ItemSwipeSwitch> newlist = new ArrayList<>();
        for (int i = 0; i < mFixedList.size(); i++) {
            if (mFixedList.get(i).isChecked) {
                newlist.add(mFixedList.get(i));
            }
        }
        return newlist;
    }

    public boolean compare() {
        return compare(getContext(), mSelectedList, getNewSelectList());
    }

    public boolean compare(Context context, ArrayList<ItemSwipeSwitch> oldlist, ArrayList<ItemSwipeSwitch> newlist) {
        /**
         * 长度相等的时候经一步比较，负责直接更新
         */
        if (newlist.size() == oldlist.size()) {
            boolean bool = false;
            for (int i = 0; i < newlist.size(); i++) {
                if (!newlist.get(i).mAction.equals(oldlist.get(i).mAction)) {
                    bool = true;
                }
            }
            if (bool) {
                deleteList(context, oldlist);
                addList(context, newlist);
                return true;
            }
        } else {
            //替换
            deleteList(context, oldlist);
            addList(context, newlist);
            return true;
        }
        return false;
    }

    /**
     * 删除一个ItemApp List
     *
     * @param context
     * @param oldlist 需要删除的list数据
     */
    public void deleteList(Context context, ArrayList<ItemSwipeSwitch> oldlist) {
        for (int i = 0; i < oldlist.size(); i++) {
            oldlist.get(i).delete(context);
        }
    }


    /**
     * 新增的ItemList数据
     *
     * @param context
     * @param newlist
     */
    public void addList(Context context, ArrayList<ItemSwipeSwitch> newlist) {
        for (int i = 0; i < newlist.size(); i++) {
            newlist.get(i).insert(context, i);
        }
    }


}
