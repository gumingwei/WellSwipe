package com.well.swipe.preference;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.well.swipe.ItemApplication;
import com.well.swipe.R;
import com.well.swipe.view.GridLayoutItemView;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by mingwei on 3/31/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class SwipeWhitelistDialog extends SwipeDialog implements AdapterView.OnItemClickListener {

    private TextView mTitle;

    private View mContentView;

    private LinearLayout mItemContent;

    private GridView mGridView;

    private GridAdapter mAdapter;

    private Button mNegativeBtn;

    private Button mPositiveBtn;

    private ArrayList<ItemApplication> mWhiteList = new ArrayList<>();

    //private List<ItemApplication> mWhiteListFix = new ArrayList<>();

    public SwipeWhitelistDialog(Context context) {
        super(context);
    }

    @Override
    public View onCreateView() {
        mContentView = LayoutInflater.from(getContext()).inflate(R.layout.swipe_dialog_whitelist, null);
        mTitle = (TextView) mContentView.findViewById(R.id.dialog_title);
        mItemContent = (LinearLayout) mContentView.findViewById(R.id.dialog_content_list);
        mGridView = (GridView) mContentView.findViewById(R.id.dialog_gridview);
        mGridView.setOnItemClickListener(this);
        mNegativeBtn = (Button) mContentView.findViewById(R.id.dialog_cancel);
        mPositiveBtn = (Button) mContentView.findViewById(R.id.dialog_ok);
        return mContentView;
    }

    public SwipeWhitelistDialog setTitle(String title) {
        mTitle.setText(title);
        return this;
    }

    public SwipeWhitelistDialog onPositive(View.OnClickListener listener) {
        mPositiveBtn.setOnClickListener(listener);
        return this;
    }

    public SwipeWhitelistDialog onNegative(View.OnClickListener listener) {
        mNegativeBtn.setOnClickListener(listener);
        return this;
    }

    public SwipeWhitelistDialog setWhiteList(ArrayList<ItemApplication> whitelist) {
        mWhiteList = whitelist;
        //mWhiteListFix = whitelist;
        return this;
    }

    public SwipeWhitelistDialog setGridData(ArrayList<ItemApplication> applis) {
        mAdapter = new GridAdapter(applis);
        mGridView.setAdapter(mAdapter);
        return this;
    }


    @Override
    public void dissmis() {
        super.dissmis();
        mItemContent.removeAllViews();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mGridView) {
            GridLayoutItemView itemview = (GridLayoutItemView) view;
            ItemApplication itemdata = (ItemApplication) view.getTag();
            if (itemview.getCheckBox().isChecked()) {
                itemview.setChecked(false);

                Iterator<ItemApplication> iterator = mWhiteList.iterator();
                while (iterator.hasNext()) {
                    ItemApplication app = iterator.next();
                    if (app.mIntent.getComponent().getPackageName().equals(itemdata.mIntent.getComponent().
                            getPackageName()) && app.mIntent.getComponent().getClassName().equals(itemdata.
                            mIntent.getComponent().getClassName())) {
                        iterator.remove();
                    }
                }
            } else {
                itemview.setChecked(true);
                mWhiteList.add(itemdata);
            }

        }
    }

    public ArrayList<ItemApplication> getWhitelist() {
        return mWhiteList;
    }

    class GridAdapter extends BaseAdapter {

        ArrayList<ItemApplication> appslist;

        public GridAdapter(ArrayList<ItemApplication> list) {
            appslist = list;
        }

        @Override
        public boolean hasStableIds() {
            return super.hasStableIds();
        }

        @Override
        public int getCount() {
            return appslist.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.gridlayout_item_layout, null);
            }
            ((GridLayoutItemView) convertView).setItemIcon(appslist.get(position).mIconBitmap);
            ((GridLayoutItemView) convertView).setTitle(appslist.get(position).mTitle.toString());
            ((GridLayoutItemView) convertView).setChecked(contain(mWhiteList, appslist.get(position)));
            (convertView).setTag(appslist.get(position));
            return convertView;
        }
    }

    public boolean contain(ArrayList<ItemApplication> list, ItemApplication app) {
        for (ItemApplication appobj : list) {
            if (app.mIntent.getComponent().getPackageName().equals(appobj.mIntent.getComponent().
                    getPackageName()) && app.mIntent.getComponent().getClassName().equals(appobj.
                    mIntent.getComponent().getClassName())) {
                return true;
            }
        }
        return false;
    }
}
