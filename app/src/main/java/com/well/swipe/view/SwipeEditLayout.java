package com.well.swipe.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.well.swipe.ItemApplication;
import com.well.swipe.R;
import com.well.swipe.utils.FastBitmapDrawable;
import com.well.swipe.utils.Pinyin;
import com.well.swipe.utils.SwipeWindowManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by mingwei on 3/19/16.
 */
public class SwipeEditLayout extends RelativeLayout implements View.OnClickListener {

    private SwipeWindowManager mManager;

    private TextView mHeaderTitle;

    private String mTitleFormat;

    public static final String JING_INDEXER_SIGN = "#";

    public static final String XIN_INDEXER_SING = "*";

    public static final String UNKONW_SING = "?";

    public static final String ZI_SIGN = "字";

    private GridLayout mHeaderGridLayout;

    private int mSize;

    private LinearLayout mContentLayout;

    private ListView mListView;

    private Button mCancelBtn;

    private Button mOkBtn;
    /**
     * 用来给数据进行排序的集合
     */
    private ArrayList<String> mKeys;
    /**
     * 用来装ListView的数据
     */
    private HashMap<String, ArrayList<ItemApplication>> mDataList;

    private KeyAdapter mAdapter;

    private ArrayList<ItemApplication> mApplist;
    /**
     * 用来显示Header的数据
     */
    private ArrayList<ItemApplication> mHeaderDataList;
    /**
     * 用来关闭时做比较的另一个数据集合
     */
    private ArrayList<ItemApplication> mFixedDataList;

    public OnChangeListener mOnChangeListener;

    public interface OnChangeListener {
        /**
         * 数据发生变化时通知Service更新数据
         *
         * @param bool 为true时更新数据，为false不更新
         */
        void onChanged(boolean bool);
    }

    public SwipeEditLayout(Context context) {
        this(context, null);
    }

    public SwipeEditLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeEditLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSize = getResources().getDimensionPixelSize(R.dimen.angleitem_size);
        mManager = new SwipeWindowManager(0, 0, context);
        mTitleFormat = getResources().getString(R.string.swipe_edit_header_title);
        mHeaderGridLayout = new GridLayout(context);
        mHeaderGridLayout.setColumnCount(4);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mHeaderTitle = (TextView) findViewById(R.id.swipe_edit_header_title);
        mContentLayout = (LinearLayout) findViewById(R.id.swipe_edit_content);
        mListView = (ListView) findViewById(R.id.swipe_edit_listview);
        mOkBtn = (Button) findViewById(R.id.swipe_edit_footer_ok);
        mCancelBtn = (Button) findViewById(R.id.swipe_edit_footer_cancel);
        mOkBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);
        mListView.addHeaderView(mHeaderGridLayout);
        mHeaderTitle.setText(String.format(mTitleFormat, "1", "2"));
    }

    public void show() {
        mManager.show(this);
    }

    public void hide() {
        mManager.hide(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            hide();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    /**
     * 按照首字符过滤分类Apps的数据并设置给ListView的设
     *
     * @param datalist apps数据
     */
    public void setData(ArrayList<ItemApplication> datalist) {
        mKeys = new ArrayList<>();
        mDataList = new HashMap<>();
        String language = Locale.getDefault().getLanguage();
        Pattern pattern = Pattern.compile("^[A-Za-z]+$");
        for (int i = 0; i < datalist.size(); i++) {
            ItemApplication app = datalist.get(i);
            CharSequence title = app.mTitle;
            if (!TextUtils.isEmpty(title)) {
                char keyChar[] = title.toString().trim().toUpperCase().substring(0, 1).toCharArray();
                char key = keyChar[0];
                if (Character.isDigit(key)) {

                    contains(String.valueOf(key), app);
                } else if (pattern.matcher(String.valueOf(key)).matches()) {

                    contains(String.valueOf(key), app);
                } else if (Locale.ENGLISH.getLanguage().equalsIgnoreCase(language)) {
                    if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(Character.UnicodeBlock.of(key))) {
                        contains(String.valueOf(key), app);
                    } else {
                    }
                } else if (Locale.CHINA.getLanguage().equalsIgnoreCase(language)) {
                    if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(Character.UnicodeBlock.of(key))) {
                        ArrayList<Pinyin.Token> tokens = Pinyin.getInstance().get(app.mTitle.toString());
                        if (tokens != null && tokens.size() > 0) {
                            Pinyin.Token token = tokens.get(0);
                            if (Pinyin.Token.PINYIN == token.type && !TextUtils.isEmpty(token.target)) {
                                String pinyin = token.target.trim().toUpperCase().substring(0, 1).charAt(0) + "";
                                contains(String.valueOf(pinyin), app);
                            } else {
                            }
                        } else {
                        }
                    } else {
                    }
                } else if (Locale.JAPAN.getLanguage().equalsIgnoreCase(language)) {
                    if (Character.UnicodeBlock.HIRAGANA.equals(Character.UnicodeBlock.of(key))
                            || (Character.UnicodeBlock.KATAKANA.equals(Character.UnicodeBlock.of(key)))) {
                        contains(String.valueOf(JING_INDEXER_SIGN), app);
                    } else if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(Character.UnicodeBlock.of(key))) {
                        contains(String.valueOf(ZI_SIGN), app);
                    }

                } else if (Locale.KOREA.getLanguage().equalsIgnoreCase(language)) {
                    if (Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS.equals(Character.UnicodeBlock.of(key))) {
                        contains(String.valueOf(ZI_SIGN), app);
                    }
                }

            }

        }
        Collections.sort(mKeys);

        mAdapter = new KeyAdapter(getContext(), mKeys);
        mListView.setAdapter(mAdapter);
    }

    /**
     * 判定key是否存在于List中
     *
     * @param key 当前的key
     * @param app 当前的app
     */
    private void contains(String key, ItemApplication app) {
        if (!mKeys.contains(key)) {
            mKeys.add(key);
            mApplist = new ArrayList<>();
            mDataList.put(key, mApplist);
        } else {
            mApplist = mDataList.get(key);
        }
        if (mApplist != null) {
            mApplist.add(app);
        }
    }

    /**
     * Header的数据
     *
     * @param appslist
     */
    public void setHeaderData(ArrayList<ItemApplication> appslist) {
        mHeaderDataList = new ArrayList<>();
        mFixedDataList = new ArrayList<>();
        mHeaderDataList.addAll(appslist);
        mFixedDataList.addAll(appslist);
        refreshHeader();
    }

    /**
     * 刷新ListView Header
     */
    public void refreshHeader() {
        mHeaderGridLayout.removeAllViews();
        if (mHeaderDataList != null && mHeaderDataList.size() > 0) {
            //final
            for (int i = 0; i < mHeaderDataList.size(); i++) {
                final GridLayoutItemView itemview = (GridLayoutItemView) LayoutInflater.from(getContext()).inflate(R.layout.gridlayout_item_layout, null);
                itemview.setItemIcon(new FastBitmapDrawable(mHeaderDataList.get(i).mIconBitmap));
                itemview.setTag(mHeaderDataList.get(i));
                itemview.setItemTitle(mHeaderDataList.get(i).mTitle.toString());
                itemview.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mHeaderGridLayout.removeView(itemview);
                        hideAnimation(v);
                    }
                });
                mHeaderGridLayout.addView(itemview, new LinearLayout.LayoutParams(mSize, mSize));
            }
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * 隐藏动画
     *
     * @param view
     */
    private void hideAnimation(final View view) {
        view.setPivotX(view.getWidth() / 2);
        view.setPivotY(view.getHeight() / 2);
        final ItemApplication itemapp = (ItemApplication) view.getTag();
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0f);
        valueAnimator.setDuration(300);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                view.setScaleX(values);
                view.setScaleY(values);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mHeaderDataList.remove(itemapp);
                refreshHeader();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.start();
    }

    public void setOnChangeListener(OnChangeListener listener) {
        mOnChangeListener = listener;
    }

    public ArrayList<ItemApplication> getHeaderDataList() {
        return mHeaderDataList;
    }

    public ArrayList<ItemApplication> getFixedDataListDataList() {
        return mFixedDataList;
    }

    @Override
    public void onClick(View v) {
        if (v == mCancelBtn) {
            mOnChangeListener.onChanged(false);
        } else if (v == mOkBtn) {
            mOnChangeListener.onChanged(true);
        } else if (v instanceof GridLayoutItemView) {
            ItemApplication itemapp = (ItemApplication) v.getTag();
            GridLayoutItemView itemview = (GridLayoutItemView) v;
            if (itemview.getCheckBox().isChecked() == true) {
                //delete
                int index = findAppInHeader(itemapp);
                if (index > 0) {
                    mHeaderDataList.remove(index);
                }
                refreshHeader();
            } else {
                if (mHeaderDataList.size() < 9) {
                    mHeaderDataList.add(itemapp);
                } else {
                    Toast.makeText(getContext(), getResources().getString(R.string.favorite_up_to_9),
                            Toast.LENGTH_SHORT).show();
                }
                refreshHeader();
            }
        }
    }

    public int findAppInHeader(ItemApplication app) {
        for (int i = 0; i < mHeaderDataList.size(); i++) {
            if (mHeaderDataList.get(i).mIntent.getComponent().getClassName().equals(app.mIntent.
                    getComponent().getClassName())) {
                return i;
            }
        }
        return -1;
    }

    class KeyAdapter extends BaseAdapter {

        private Context mContext;

        private ArrayList<String> mKeys;

        private HashMap<String, ArrayList<ItemApplication>> mLists;

        public KeyAdapter(Context context, ArrayList<String> keys) {
            mContext = context;
            mKeys = keys;
        }

        @Override
        public int getCount() {
            return mKeys.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.appsindexview_layout, null);
            }
            ((AppsIndexView) convertView).setKeyString(mKeys.get(position).toString());
            ((AppsIndexView) convertView).setSwipeEditLayout(SwipeEditLayout.this);
            //((AppsIndexView) convertView).setMeasure(mKeyItem.get(stringsArray.get(position)).size());
            ((AppsIndexView) convertView).setContent(mDataList.get(mKeys.get(position)), mHeaderDataList);
            return convertView;
        }
    }
}
