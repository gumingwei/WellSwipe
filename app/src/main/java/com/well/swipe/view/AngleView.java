package com.well.swipe.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Canvas;

import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.well.swipe.ItemApplication;
import com.well.swipe.R;
import com.well.swipe.ItemSwipeTools;
import com.well.swipe.tools.ToolsStrategy;
import com.well.swipecomm.view.PositionStateViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by mingwei on 2/25/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 * 旋转的扇形View
 * 1.扇形可以放在左边或者右边
 * 2.根据手指在上一层容器的touch事件来处理AngleView的转动，松开手指自动转动到指定位置
 * 3.通过捕捉飞快的手指滑动来转动容器
 * 4.给子控件设置扇形的布局坐标
 * 5.长按进入编辑模式
 * 6.编辑模式下拖拽，拖拽交换位置
 * 7.拖拽到垃圾箱
 */
public class AngleView extends PositionStateViewGroup {
    /**
     * 旋转的基数角度
     */
    private float mBaseAngle = 0;
    /**
     * 跟随手指转动的是的变量
     */
    private float mChangeAngle = 0;
    /**
     * 按下时的角度
     */
    private double mDownAngle;
    /**
     * 转轴PivotX,PivotY
     */
    private int mPivotX = 0;

    private int mPivotY = 0;
    /**
     * 控件的宽高
     */
    private int mHeight;

    private int mWidth;

    /**
     * 用于计算单击时间的X&Y
     */
    private float mMotionX;

    private float mMotionY;
    /**
     * 单击事件的第一个时间
     */
    private long mClickTime1;
    /**
     * 用来区分点击事件的类型
     */
    private int mClickType = -1;

    private static final int TYPE_CLICK = 0;

    private static final int TYPE_DELCLICK = 1;

    private static final int TYPE_ADDCLICK = 2;

    private static final int TYPE_LONGCLICK = 3;

    private static final int TYPE_RECENT = 0;

    private static final int TYPE_TOOLS = 1;

    private static final int TYPE_FAVORITE = 2;
    /**
     * AngleView点击时候找到的ItemView
     */
    AngleItemCommon mTargetItem;
    /**
     * 用于点击或者长按的时候计算得到的当前点击的Item的四个坐标
     */
    private float mDownLeft;

    private float mDownTop;

    private float mDownRight;

    private float mDownBottom;

    private int mDragIndex;
    /**
     * 顺时针/逆时针
     */
    private int ANGLE_STATE = ANGLE_STATE_REST;

    public static final int ANGLE_STATE_REST = 0;

    public static final int ANGLE_STATE_ALONG = 1;

    public static final int ANGLE_STATE_INVERSE = 2;

    public static final int DEGREES_360 = 360;

    public static final int DEGREES_1080 = DEGREES_360 * 3;

    private int mChildHalfSize;

    public int mInnerRadius;

    public int mOuterRadius;

    public int mDeleteBtnSize;
    /**
     * 单位度数
     */
    public static final int DEGREES_90 = 90;
    /**
     * 判定范围
     */
    public static final int DEGREES_OFFSET = 15;
    /**
     * 判定Fling动作的速度范围
     */
    private static final int ALLOW_FLING = 2500;

    private static final int COUNT_4 = 4;

    private static final int COUNT_3 = 3;

    private static final int COUNT_12 = COUNT_4 * COUNT_3;

    private int mCurrentIndex;

    private ValueAnimator mAngleAnimator;

    private Map<Integer, ArrayList<AngleItemCommon>> mMap = new HashMap<>();

    private ArrayList<AngleItemCommon> mRecentAppList = new ArrayList<>();

    private ArrayList<AngleItemCommon> mSwitchList = new ArrayList<>();

    private ArrayList<AngleItemCommon> mFavoriteAppList = new ArrayList<>();

    /**
     * 删除前临时保存数据
     */
    private ArrayList<AngleItemCommon> mDelPre = new ArrayList<>();

    /**
     * 删除后临时保存数据
     */
    private ArrayList<Coordinate> mDelNext = new ArrayList<>();

    /**
     * 交换前
     */
    private ArrayList<Coordinate> mExchangePre = new ArrayList<>();

    private OnAngleChangeListener mAngleListener;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    /**
     * 长按处理
     */
    private LongClickRunable mLongRunable = new LongClickRunable();
    /**
     * 震动
     */
    private Vibrator mVibrator;

    public interface OnAngleChangeListener {

        /**
         * 角度发生变化时传递当前的所显示的数据索引值&当前的百分比
         * 用于改变Indicator的选中状态，百分比则用来渲染过渡效果
         *
         * @param cur 正在显示的是数据index
         * @param p   百分比
         */
        void onAngleChanged(int cur, float p);

    }

    OnClickListener mOnClickListener;

    public interface OnClickListener {
        /**
         * 本身的OnClick无效，所以自己根据在onTouch中定义一个Onclick
         */
        void onAngleClick(View view);

        /**
         * 位于布局左上角的删除按钮，点击的时候直接在控件AngleView内部处理了数据
         *
         * @param view
         */
        void onDeleteClick(View view);

        /**
         * 在每一个扇区最后有一个添加按钮，扇区满的时候这个按钮没有显示出来，
         * 在扇区显示的时候，点击添加按钮，吧参数回调到SwipLayout
         * 然后添加新的数据进来
         */
        void onAddClick(int index);
    }

    OnEditModeChangeListener mOnEditModeChangeListener;

    public interface OnEditModeChangeListener {
        /**
         * 进入编辑模式
         *
         * @param view
         */
        void onEnterEditMode(View view);

        /**
         * 退出编辑模式
         */
        void onExitEditMode();

        /**
         * 进入拖拽模式
         *
         * @param view       判定进行拖拽的当前view
         * @param left       在父控件中的left值
         * @param top        在父控件中的top值
         * @param offsetLeft 触摸点在当前进行拖拽的view的left距离
         * @param offsetTop  触摸点在当前进行拖拽的view的top距离
         */
        void onStartDrag(AngleItemCommon view, float left, float top, float offsetLeft, float offsetTop);

        /**
         * 拖拽取消
         */
        void onCancelDrag();
    }

    public OnBindListener mOnBindListener;

    interface OnBindListener {

        /**
         * 数据绑定完成
         */
        void bindComplete();
    }


    /**
     * 临时坐标信息
     */
    class Coordinate {

        public float x;

        public float y;

        public Coordinate(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    public AngleView(Context context) {
        this(context, null);
    }

    public AngleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mChildHalfSize = getResources().getDimensionPixelSize(R.dimen.angleitem_half_size);
        mInnerRadius = getResources().getDimensionPixelSize(R.dimen.angleview_inner_radius);
        mOuterRadius = getResources().getDimensionPixelSize(R.dimen.angleview_outer_radius);
        mDeleteBtnSize = getResources().getDimensionPixelSize(R.dimen.angleview_item_delete_size);

        mMap.put(TYPE_RECENT, mRecentAppList);
        mMap.put(TYPE_TOOLS, mSwitchList);
        mMap.put(TYPE_FAVORITE, mFavoriteAppList);

    }

    /**
     * 刷新AngleView数据
     */
    public void refresh() {
        removeAllViews();
        Iterator<Map.Entry<Integer, ArrayList<AngleItemCommon>>> it = mMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, ArrayList<AngleItemCommon>> arraylist = it.next();
            ArrayList<AngleItemCommon> views = arraylist.getValue();
            for (View view : views) {
                if (view.getParent() == null) {
                    addView(view);
                }
            }
        }
        requestLayout();
    }

    public void putItemApplications(ArrayList<ItemApplication> itemlist) {
        if (mMap.get(TYPE_FAVORITE).size() > 0) {
            mMap.get(TYPE_FAVORITE).clear();
        }
        AngleItemStartUp itemview;
        for (ItemApplication appitem : itemlist) {
            itemview = (AngleItemStartUp) LayoutInflater.from(getContext()).inflate(R.layout.angle_item_startup, null);
            itemview.setTitle(appitem.mTitle.toString());
            itemview.setItemIcon(appitem.mIconBitmap);
            itemview.setTag(appitem);
            mMap.get(TYPE_FAVORITE).add(itemview);
        }
        /**
         * 添加额外的add按钮
         */
        AngleItemAddTo targetItem = (AngleItemAddTo) LayoutInflater.from(getContext()).inflate(R.layout.angle_item_addto, null);
        mMap.get(TYPE_FAVORITE).add(targetItem);
    }

    /**
     * 从Tag中拿出所有的Apps数据
     *
     * @return
     */
    public ArrayList<ItemApplication> getItemApplications() {
        if (getViewsIndex() == 2) {
            ArrayList<AngleItemCommon> views = getData();
            if (views != null) {
                ArrayList<ItemApplication> itemApplications = new ArrayList<>();
                for (int i = 0; i < views.size() - 1; i++) {
                    itemApplications.add((ItemApplication) views.get(i).getTag());
                }
                return itemApplications;
            }
        }
        return null;
    }

    public void putItemQuickSwitch(ArrayList<ItemSwipeTools> itemlist) {
        if (mMap.get(TYPE_TOOLS).size() > 0) {
            mMap.get(TYPE_TOOLS).clear();
        }
        AngleItemStartUp itemview;
        for (ItemSwipeTools appitem : itemlist) {
            itemview = (AngleItemStartUp) LayoutInflater.from(getContext()).inflate(R.layout.angle_item_startup, null);
            itemview.setTitle(appitem.mTitle.toString());
            itemview.setItemIconBackground(getResources().getDrawable(R.drawable.angle_item_bg));
            ToolsStrategy.getInstance().initView(getContext(), itemview, appitem);
            itemview.setTag(appitem);
            mMap.get(TYPE_TOOLS).add(itemview);
        }
        /**
         * 添加额外的add按钮
         */
        AngleItemAddTo targetItem = (AngleItemAddTo) LayoutInflater.from(getContext()).inflate(R.layout.angle_item_addto, null);
        mMap.get(TYPE_TOOLS).add(targetItem);
    }

    /**
     * 从Tag中拿出所有的Tools数据
     *
     * @return
     */
    public ArrayList<ItemSwipeTools> getToolsArrayList() {
        if (getViewsIndex() == 1) {
            ArrayList<AngleItemCommon> views = getData();
            if (views != null) {
                ArrayList<ItemSwipeTools> toolsArrayList = new ArrayList<>();
                for (int i = 0; i < views.size() - 1; i++) {
                    toolsArrayList.add((ItemSwipeTools) views.get(i).getTag());
                }
                return toolsArrayList;
            }
        }
        return null;
    }


    public void refreshToolsView() {
        int index = getViewsIndex();
        for (int i = 0; i < mMap.get(index).size(); i++) {
            AngleItemCommon itemview = mMap.get(index).get(i);
            if (itemview instanceof AngleItemStartUp && itemview.getTag() instanceof ItemSwipeTools) {
                ItemSwipeTools item = (ItemSwipeTools) itemview.getTag();
                ToolsStrategy.getInstance().initView(getContext(), itemview, item);
            }
        }
    }

    /**
     * 设置RecentTask数据
     *
     * @param activityInfoList
     */
    public void putRecentTask(List<ActivityManager.RecentTaskInfo> activityInfoList, ArrayList<ItemApplication> extralist) {
        if (mRecentAppList.size() > 0) {
            mRecentAppList.clear();
        }
        PackageManager manager = getContext().getPackageManager();
        ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(manager, 0);
        for (int i = 0; i < activityInfoList.size(); i++) {
            ActivityManager.RecentTaskInfo info = activityInfoList.get(i);
            Intent intent = new Intent(info.baseIntent);
            if (info.origActivity != null) {
                intent.setComponent(info.origActivity);
            }
            if (homeInfo != null) {
                if (homeInfo.packageName.equals(intent.getComponent().getPackageName()) &&
                        homeInfo.name.equals(intent.getComponent().getClassName())) {
                    continue;

                }
            }
            intent.setFlags((intent.getFlags() & ~Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                    | Intent.FLAG_ACTIVITY_NEW_TASK);
            final ResolveInfo resolveInfo = manager.resolveActivity(intent, 0);
            if (resolveInfo != null) {
                ActivityInfo activityInfo = resolveInfo.activityInfo;
                String title = activityInfo.loadLabel(manager).toString();
                Drawable icon = activityInfo.loadIcon(manager);
                BitmapDrawable bd = (BitmapDrawable) icon;
                if (title != null && title.length() > 0 && icon != null) {
                    AngleItemStartUp itemview = (AngleItemStartUp) LayoutInflater.from(getContext())
                            .inflate(R.layout.angle_item_startup, null);
                    itemview.setTag(activityInfoList.get(i));
                    AngleItemStartUp.RecentTag recenttag = new AngleItemStartUp.RecentTag();
                    recenttag.info = info;
                    recenttag.intent = intent;
                    itemview.mRecentTag = recenttag;
                    itemview.setTitle(title);
                    itemview.setItemIcon(bd.getBitmap());
                    mRecentAppList.add(itemview);
                }
            }
        }
        if (mRecentAppList.size() < 10) {
            /**
             * 需要补充的额外数目
             */
            if (extralist.size() > 10) {
                for (int i = 0; i < 10; i++) {
                    AngleItemStartUp itemview = (AngleItemStartUp) LayoutInflater.from(getContext())
                            .inflate(R.layout.angle_item_startup, null);
                    itemview.setTitle(extralist.get(i).mTitle.toString());
                    itemview.setItemIcon(extralist.get(i).mIconBitmap);
                    itemview.setTag(extralist.get(i));
                    if (contains(activityInfoList, extralist.get(i))) {
                        break;
                    } else {
                        mRecentAppList.add(itemview);
                    }
                }
            } else {
                for (int i = 0; i < extralist.size(); i++) {
                    AngleItemStartUp itemview = (AngleItemStartUp) LayoutInflater.from(getContext())
                            .inflate(R.layout.angle_item_startup, null);
                    itemview.setTitle(extralist.get(i).mTitle.toString());
                    itemview.setItemIcon(extralist.get(i).mIconBitmap);
                    itemview.setTag(extralist.get(i));
                    if (contains(activityInfoList, extralist.get(i))) {
                        break;
                    } else {
                        mRecentAppList.add(itemview);
                    }

                }
            }
        }
        refresh();

        /**
         * 绑定万Recent之后调用
         */
        mOnBindListener.bindComplete();
    }

    public boolean contains(List<ActivityManager.RecentTaskInfo> activityInfoList, ItemApplication app) {
        for (int i = 0; i < activityInfoList.size(); i++) {
            ActivityManager.RecentTaskInfo info = activityInfoList.get(i);
            Intent intent = new Intent(info.baseIntent);
            if (info.origActivity != null) {
                intent.setComponent(info.origActivity);
            }
            if (intent.getComponent().getPackageName().equals(app.mIntent.getComponent().getPackageName()) &&
                    intent.getComponent().getClassName().equals(app.mIntent.getComponent().getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPivotX = getMeasuredWidth();
        mPivotY = getMeasuredHeight();
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
        //setBaseAngle(90);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).measure(mChildHalfSize * 2, mChildHalfSize * 2);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int index = (int) ((mBaseAngle) / DEGREES_90);
        if (isLeft()) {
            itemLayout(index);
        } else if (isRight()) {
            itemLayout2(index);
        }
    }

    /**
     * 布局子控件
     * 通过一个当前值，计算上一个，下一个值
     *
     * @param index
     */
    private void itemLayout(int index) {
        mCurrentIndex = getRealIndex(index);
        itemLayout(mMap.get(getPreViewsIndex(getViewsIndex(index))), getPreQuaIndex(getQuaIndex(index)));
        itemLayout(mMap.get(getViewsIndex(index)), getQuaIndex(index));
        itemLayout(mMap.get(getNextViewsIndex(getViewsIndex(index))), getNextQuaIndex(getQuaIndex(index)));
    }

    /**
     * mPositionState＝Right的时候
     *
     * @param index
     */
    private void itemLayout2(int index) {
        mCurrentIndex = index;
        itemLayout(mMap.get(getPreViewsIndex(getViewsIndex2(index))), getPreQuaIndex(getQuaIndex2(index)));
        itemLayout(mMap.get(getViewsIndex2(index)), getQuaIndex2(index));
        itemLayout(mMap.get(getNextViewsIndex(getViewsIndex2(index))), getNextQuaIndex(getQuaIndex2(index)));
    }

    /**
     * 把一组数据指定布局到一个限象
     *
     * @param views 需要布局的数据组
     * @param qua   限象
     */
    private void itemLayout(ArrayList<AngleItemCommon> views, int qua) {
        if (views != null) {
            for (int index = 0; index < views.size(); index++) {
                /**
                 * 计算坐标
                 */
                Coordinate coordinate = coordinate(views, index, qua);
                /**
                 * 矫正子view
                 * 旋转一定的角度,以保证旋转至第0限象时方向是正的
                 */
                if (isLeft()) {
                    views.get(index).setRotation(DEGREES_90 * qua);
                } else if (isRight()) {
                    views.get(index).setRotation(-DEGREES_90 * qua);
                }
                /**
                 * 指定位置
                 */
                if (index < 9) {
                    views.get(index).setParentX(coordinate.x);
                    views.get(index).setParentY(coordinate.y);
                    views.get(index).layout((int) (coordinate.x - mChildHalfSize), (int) (coordinate.y - mChildHalfSize),
                            (int) (coordinate.x + mChildHalfSize), (int) (coordinate.y + mChildHalfSize));
                }
            }
        }

    }

    /**
     * 根据所有控件的索引Index计算坐标
     *
     * @param views 控件集合
     * @param index 遍历控件时的索引
     * @param qua   限象值
     * @return 返回一个包含坐标(x, y)
     */
    public Coordinate coordinate(ArrayList<AngleItemCommon> views, int index, int qua) {
        int size = 0;
        /**
         * group可认为是跟随环数而变化的一个值，用来计算index非0时的子控件的角度增长
         * 角度增为只有一个子控件的时候：90/1=45；
         * index非0的时候：(group＋0.5)*newdegree(按照当前环中子控件的总数平分90的值)
         * 微博：     明伟小学生(http://weibo.com/u/2382477985)
         * Github:   https://github.com/gumingwei
         * CSDN:     http://blog.csdn.net/u013045971
         * QQ：      721881283
         */
        int group = 0;
        /**
         * 半径变化，环数增加，半径增加
         */
        int radius = 0;
        if (views.size() <= COUNT_4) {
            size = views.size();
            group = index;
            radius = mInnerRadius;
        } else if (views.size() <= 9) {
            if (index < COUNT_4) {
                /**
                 * 总数大于4时内环正好是4
                 */
                size = COUNT_4;
                group = index;
                radius = mInnerRadius;
            } else if (index < 9) {
                /**
                 * 总数大于4时外环
                 * size＝总数－4
                 * group＝views(index)-4
                 */
                size = views.size() - COUNT_4;
                group = index - COUNT_4;
                radius = mOuterRadius;
            }
        } else {
            if (index < COUNT_4) {
                /**
                 * 总数大于4时内环正好是4
                 */
                size = COUNT_4;
                group = index;
                radius = mInnerRadius;
            } else if (index < 9) {
                /**
                 * 总数大于4时外环
                 * size＝总数－4
                 * group＝views(index)-4
                 */
                size = COUNT_4 + 1;
                group = index - COUNT_4;
                radius = mOuterRadius;
            }
        }
        /**
         * 按照views(index)所在的当前环的个数平分90度
         */
        float degree = (float) DEGREES_90 / (float) (size);
        /**
         * 得出一个新的递增的角度，用来后面按照三角函数计算子控的位置
         */
        float newdegree;
        if (index == 0) {
            newdegree = degree / 2;
        } else {
            newdegree = (int) ((group + 0.5) * degree);
        }
        /**
         * 1.按照限象使用不同的三角函数计算所得x,y坐标
         * 2.子控件根据不同的呃限象旋转位置满足在第0限象的正常显示效果
         * 3.当整个控件的容器反转之后，为保证显示效果，要做一定的反转
         */
        float x = 0l;
        float y = 0l;
        if (isLeft()) {
            if (qua == 0) {
                x = (float) Math.sin(Math.toRadians(newdegree)) * radius;
                y = (float) (mHeight - Math.cos(Math.toRadians(newdegree)) * radius);
            } else if (qua == 1) {
                x = (float) Math.cos(Math.toRadians(newdegree)) * radius;
                y = (float) (mHeight + Math.sin(Math.toRadians(newdegree)) * radius);
            } else if (qua == 2) {
                x = (float) -Math.sin(Math.toRadians(newdegree)) * radius;
                y = (float) (mHeight + Math.cos(Math.toRadians(newdegree)) * radius);
            } else if (qua == 3) {
                x = (float) -Math.cos(Math.toRadians(newdegree)) * radius;
                y = (float) (mHeight - Math.sin(Math.toRadians(newdegree)) * radius);
            }
        } else if (isRight()) {
            if (qua == 0) {
                x = (float) (mWidth - Math.sin(Math.toRadians(newdegree)) * radius);
                y = (float) (mHeight - Math.cos(Math.toRadians(newdegree)) * radius);
            } else if (qua == 1) {
                x = (float) (mWidth - Math.cos(Math.toRadians(newdegree)) * radius);
                y = (float) (mHeight + Math.sin(Math.toRadians(newdegree)) * radius);
            } else if (qua == 2) {
                x = (float) (mWidth + Math.sin(Math.toRadians(newdegree)) * radius);
                y = (float) (mHeight + Math.cos(Math.toRadians(newdegree)) * radius);
            } else if (qua == 3) {
                x = (float) (mWidth + Math.cos(Math.toRadians(newdegree)) * radius);
                y = (float) (mHeight - Math.sin(Math.toRadians(newdegree)) * radius);
            }
        }

        return new Coordinate(x, y);
    }

    /**
     * 计算当前的数据的坐标不考虑限象
     *
     * @param views 当前的视图
     * @param index 索引
     * @return 返回(x, y)坐标
     */
    public Coordinate coordinate2(ArrayList<AngleItemCommon> views, int index) {
        /**
         * size按照当前views的总数，以4为区分，分别计算出<4,=4,超出4的部分剪掉4即从1，2，3重新开始计数
         */
        int size = 0;
        /**
         * group可认为是跟随环数而变化的一个值，用来计算index非0时的子控件的角度增长
         * 角度增为只有一个子控件的时候：90/1=45；
         * index非0的时候：(group＋0.5)*newdegree(按照当前环中子控件的总数平分90的值)
         */
        int group = 0;
        /**
         * 半径变化，环数增加，半径增加
         */
        int radius = 0;
        if (views.size() <= COUNT_4) {
            size = views.size();
            group = index;
            radius = mInnerRadius;
        } else if (views.size() <= 9) {
            if (index < COUNT_4) {
                /**
                 * 总数大于4时内环正好是4
                 */
                size = COUNT_4;
                group = index;
                radius = mInnerRadius;
            } else if (index < 9) {
                /**
                 * 总数大于4时外环
                 * size＝总数－4
                 * group＝views(index)-4
                 */
                size = views.size() - COUNT_4;
                group = index - COUNT_4;
                radius = mOuterRadius;
            }
        } else {
            if (index < COUNT_4) {
                /**
                 * 总数大于4时内环正好是4
                 */
                size = COUNT_4;
                group = index;
                radius = mInnerRadius;
            } else if (index < 9) {
                /**
                 * 总数大于4时外环
                 * size＝总数－4
                 * group＝views(index)-4
                 */
                size = COUNT_4 + 1;
                group = index - COUNT_4;
                radius = mOuterRadius;
            } else if (index >= 9) {
                size = COUNT_4 + 1;
                group = 8 - COUNT_4;
                radius = mOuterRadius;
            }
        }
        /**
         * 按照views(index)所在的当前环的个数平分90度
         */
        float degree = (float) DEGREES_90 / (float) (size);
        /**
         * 得出一个新的递增的角度，用来后面按照三角函数计算子控的位置
         */
        float newdegree;
        if (index == 0) {
            newdegree = degree / 2;
        } else {
            newdegree = (int) ((group + 0.5) * degree);
        }
        /**
         * 1.按照限象使用不同的三角函数计算所得x,y坐标
         * 2.子控件根据不同的呃限象旋转位置满足在第0限象的正常显示效果
         * 3.当整个控件的容器反转之后，为保证显示效果，要做一定的反转
         */
        float x = 0l;
        float y = 0l;
        if (isLeft()) {
            x = (float) Math.sin(Math.toRadians(newdegree)) * radius;
            y = (float) (mHeight - Math.cos(Math.toRadians(newdegree)) * radius);
        } else if (isRight()) {
            x = (float) (mWidth - Math.sin(Math.toRadians(newdegree)) * radius);
            y = (float) (mHeight - Math.cos(Math.toRadians(newdegree)) * radius);
        }
        return new Coordinate(x, y);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (isLeft()) {
            canvas.rotate(mBaseAngle + mChangeAngle, 0, mPivotY);
        } else if (isRight()) {
            canvas.rotate(mBaseAngle + mChangeAngle, mPivotX, mPivotY);
        }
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    private boolean isMoveDrag = true;

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mMotionX = event.getX();
                mMotionY = event.getY();
                ArrayList<AngleItemCommon> views = getData();
                /**
                 * isRemoveFinish移除动画一处之前不允许再拖动
                 */
                if (views != null && isRemoveFinish) {
                    for (int index = 0; index < views.size(); index++) {

                        Coordinate coordinate = coordinate2(views, index);

                        mDownLeft = (coordinate.x - mChildHalfSize);
                        mDownTop = (coordinate.y - mChildHalfSize);
                        mDownRight = (coordinate.x + mChildHalfSize);
                        mDownBottom = (coordinate.y + mChildHalfSize);

                        if (mMotionX > mDownLeft && mMotionX < mDownRight && mMotionY > mDownTop && mMotionY < mDownBottom) {
                            mClickTime1 = System.currentTimeMillis();
                            /**
                             * 找到当前点击的那个item
                             * 判断isDeletedAnimCompleted的目的是，连续快速点击删除按钮的时候，
                             * 上一个刚点击正在执行删除的时候再点击的话会错误的判断删除的是后点击的Item
                             */
                            mTargetItem = views.get(index);
                            mTargetItem.setIndex(index);

                            /**
                             *
                             */
                            if (mTargetItem instanceof AngleItemStartUp) {
                                if (((AngleItemStartUp) mTargetItem).getDelBtn().getVisibility() == View.GONE) {
                                    mClickType = TYPE_CLICK;
                                } else if (mMotionX > mDownLeft && mMotionX < (mDownLeft + mDeleteBtnSize) && mMotionY > mDownTop &&
                                        mMotionY < (mDownTop + mDeleteBtnSize) && ((AngleItemStartUp) mTargetItem).getDelBtn().
                                        getVisibility() == View.VISIBLE) {
                                    mClickType = TYPE_DELCLICK;

                                } else if (((AngleItemStartUp) mTargetItem).getDelBtn().getVisibility() == View.VISIBLE) {
                                    /**
                                     * 最后一个view禁止拖动&在交换动画完成后才有效
                                     */
                                    if (index != views.size() - 1 && isRestoreFinish) {
                                        mOnEditModeChangeListener.onStartDrag(mTargetItem, mDownLeft, mDownTop, mMotionX - mDownLeft, mMotionY - mDownTop);
                                        /**
                                         * 开始出发拖动的记住一个Index,用作复原动画的时候
                                         */
                                        mDragTargetIndex = index;
                                        mTargetItem.setVisibility(GONE);
                                    }
                                }
                                if (getViewsIndex() != 0 && ((AngleItemStartUp) mTargetItem).getDelBtn().getVisibility() == View.GONE) {
                                    handler.postDelayed(mLongRunable, 600);
                                }
                            } else if (mTargetItem instanceof AngleItemAddTo) {
                                mClickType = TYPE_ADDCLICK;
                            }
                            return true;
                        }
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float movenewx = event.getX();
                float movenewy = event.getY();
                if (Math.abs(movenewx - mMotionX) > 10 || Math.abs(movenewy - mMotionY) > 10) {
                    handler.removeCallbacks(mLongRunable);
                }

                if (isMoveDrag) {
                    isMoveDrag = false;
                    ArrayList<AngleItemCommon> views2 = getData();
                    if (mTargetItem != null) {
                        if (mTargetItem instanceof AngleItemStartUp) {
                            if (((AngleItemStartUp) mTargetItem).getDelBtn().getVisibility() == View.VISIBLE) {
                                if (mTargetItem.getIndex() != views2.size() - 1 && isRestoreFinish && isRemoveFinish) {
                                    mOnEditModeChangeListener.onStartDrag(mTargetItem, mDownLeft, mDownTop, mMotionX - mDownLeft, mMotionY - mDownTop);
                                    /**
                                     * 开始出发拖动的记住一个Index,用作复原动画的时候
                                     */
                                    mDragTargetIndex = mTargetItem.getIndex();
                                    mTargetItem.setVisibility(GONE);
                                }
                            }
                        }
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                /**
                 * 这里在拖拽取消是执行，即拖拽还没有发生时抬起了手指，因为一旦拖拽发生，
                 * 触摸事件就被AngleLayout拦截下来了，AngleView就接受不到触摸事件了，
                 * So这里叫做取消拖拽并不是结束拖拽，结束拖拽要在AngleLayout里执行
                 */
                if (mTargetItem != null) {
                    if ((mTargetItem).getVisibility() == View.GONE) {
                        mOnEditModeChangeListener.onCancelDrag();
                    }
                }

                float clicknewx = event.getX();
                float clicknewy = event.getY();
                long clicktime = System.currentTimeMillis();
                if (Math.abs(mMotionX - clicknewx) < 10 && Math.abs(mMotionY - clicknewy) < 10) {
                    long time = Math.abs(mClickTime1 - clicktime);
                    if (time < 300) {
                        if (mClickType == TYPE_DELCLICK) {
                            /**
                             * 删除时直接掉用删掉当前的数据，回调接口暂时没有用
                             */
                            if (mOnClickListener != null) {
                                mOnClickListener.onDeleteClick(mTargetItem);
                            }
                        } else if (mClickType == TYPE_CLICK) {
                            /**
                             * 正常的点击事件
                             */
                            shake(mTargetItem);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (mOnClickListener != null) {
                                        mOnClickListener.onAngleClick(mTargetItem);

                                    }
                                }
                            }, 140);

                        } else if (mClickType == TYPE_ADDCLICK) {
                            /**
                             * 点击最后一个AddBtn时的点击事件
                             */
                            shake(mTargetItem);
                            postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mOnClickListener.onAddClick(getViewsIndex());
                                }
                            }, 140);
                        }
                        handler.removeCallbacks(mLongRunable);
                    }
                }
                mClickType = -1;
                break;
            case MotionEvent.ACTION_CANCEL:
                mClickType = -1;
                break;
        }
        return super.onTouchEvent(event);

    }

    /**
     * 准备拖动时临时生成一个位置数组
     */
    public void exchangePre() {
        ArrayList<AngleItemCommon> views = getData();
        if (views != null) {
            mExchangePre.clear();
            for (int index = 0; index < views.size(); index++) {
                /**
                 * size按照当前views的总数，以4为区分，分别计算出<4,=4,超出4的部分剪掉4即从1，2，3重新开始计数
                 */
                mExchangePre.add(coordinate2(views, index));
            }
        }
    }


    /**
     * 用来锁定复原动画，在动画结束前，不允许动画再次启动
     */
    private boolean isRestoreFinish = true;

    private int mDragTargetIndex;

    /**
     * 拖动的时候根据坐标判断进入那个范围了
     *
     * @param x
     * @param y
     */
    public void checkAndChange(float x, float y) {
        float centerX = x + mChildHalfSize;
        float centerY = y + mChildHalfSize;
        if (mTargetItem.getVisibility() == GONE && isExChangeFinish) {
            for (int index = 0; index < mExchangePre.size(); index++) {
                if (index != mExchangePre.size() - 1) {
                    Coordinate coordinate = mExchangePre.get(index);
                    float newleft = (coordinate.x - mChildHalfSize);
                    float newtop = (coordinate.y - mChildHalfSize);
                    float newright = (coordinate.x + mChildHalfSize);
                    float newbottom = (coordinate.y + mChildHalfSize);
                    if (centerX > newleft && centerY > newtop && centerX < newright && centerY < newbottom) {

                        if (index != mTargetItem.getIndex()) {
                            isExChangeFinish = false;
                            /**
                             * 触发交换的时候更新index值
                             */
                            mDragTargetIndex = index;
                            ArrayList<AngleItemCommon> arrayList = new ArrayList<>();
                            arrayList.clear();
                            arrayList.addAll(getData());
                            /**
                             * 交换数据
                             */

                            AngleItemCommon temp = arrayList.get(index);
                            arrayList.set(index, arrayList.get(mTargetItem.getIndex()));
                            arrayList.set(mTargetItem.getIndex(), temp);

                            int targetIndex = mTargetItem.getIndex();
                            /**
                             * 重置angleItem的Index
                             */
                            for (int i = 0; i < arrayList.size(); i++) {
                                arrayList.get(i).setIndex(i);
                            }

                            ArrayList<AngleItemCommon> views = arrayList;
                            Coordinate coordinateTest = null;
                            if (views != null) {
                                //mExchangeNext.clear();
                                for (int m = 0; m < views.size(); m++) {
                                    /**
                                     * size按照当前views的总数，以4为区分，分别计算出<4,=4,超出4的部分剪掉4即从1，2，3重新开始计数
                                     */
                                    //mExchangeNext.add(coordinate(views, m, getQuaIndex()));
                                    if (m == targetIndex) {
                                        coordinateTest = coordinate(views, m, getQuaIndex());
                                        break;
                                    }
                                }
                            }

                            exchangeAnimator(coordinateTest, arrayList, targetIndex);

                        }
                    }
                }
            }
        }
    }

    public boolean isRestoreFinish() {
        return isRestoreFinish;
    }

    public void setIsRestoreFinish(boolean isResore) {
        isRestoreFinish = isResore;
    }

    /**
     * 找view.visiable==gong 的view的坐标
     *
     * @return 返回(x, y)坐标
     */
    public Coordinate findEmpty() {
        ArrayList<AngleItemCommon> views = getData();
        if (views != null) {
            for (int index = 0; index < views.size(); index++) {
                if (index == mDragTargetIndex) {
                    return coordinate2(views, index);
                }
            }
        }
        return null;
    }

    private boolean isRemoveFinish = true;

    /**
     * 移除动画
     */
    public void removeItem() {
        if (isRemoveFinish) {
            isRemoveFinish = false;
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0f);
            valueAnimator.setDuration(150);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float values = (float) animation.getAnimatedValue();
                    mTargetItem.setScaleX(values);
                    mTargetItem.setScaleY(values);
                    requestLayout();
                }
            });
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //getData().remove(mTargetItem);
                    mDelPre.clear();
                    mDelPre.addAll(getData());
                    mDelPre.remove(mTargetItem);

                    ArrayList<AngleItemCommon> views = mDelPre;
                    if (views != null) {
                        mDelNext.clear();
                        for (int index = 0; index < views.size(); index++) {
                            /**
                             * size按照当前views的总数，以4为区分，分别计算出<4,=4,超出4的部分剪掉4即从1，2，3重新开始计数
                             */
                            mDelNext.add(coordinate(views, index, getQuaIndex()));
                        }
                    }
                    transAnimator(mDelNext, mDelPre);

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
    }


    /**
     * 移除item之后的过渡动画
     * 从原始坐标移动到新坐标
     *
     * @param resource   移除控件之后计算产生的新的item坐标
     * @param targetView 原始坐标
     */
    public void transAnimator(final ArrayList<Coordinate> resource, final ArrayList<AngleItemCommon> targetView) {
        ValueAnimator translation = ValueAnimator.ofFloat(0f, 1f);
        translation.setDuration(250);
        translation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                for (int i = 0; i < targetView.size(); i++) {
                    float x = (float) (resource.get(i).x - targetView.get(i).getParentX()) * values;
                    float y = (float) (resource.get(i).y - targetView.get(i).getParentY()) * values;
                    targetView.get(i).setTranslationX(x);
                    targetView.get(i).setTranslationY(y);
                    requestLayout();
                }
            }
        });
        translation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < targetView.size(); i++) {
                    targetView.get(i).setTranslationX(0);
                    targetView.get(i).setTranslationY(0);
                }
                getData().remove(mTargetItem);
                /**
                 * 判断最后一个Item，也就是只剩加号的时候退出编辑状态
                 */
                if (getData().size() == 1) {
                    mOnEditModeChangeListener.onExitEditMode();
                }

                isRemoveFinish = true;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translation.start();
    }

    public void exchange(final Coordinate recource, AngleItemCommon targetview, final int index) {

    }

    boolean isExChangeFinish = true;

    /**
     * 交换动画
     *
     * @param resource   源坐标，也就是动画的起始坐标
     * @param targetView 目标坐标，也就是动画的终点坐标
     * @param index      当前view交换之后的目标位置的index索引，主要用来屏蔽动画，因为松手之后有动画，不需要这这里再加动画了
     */
    public void exchangeAnimator(final Coordinate resource, final ArrayList<AngleItemCommon> targetView, final int index) {
        ValueAnimator translation = ValueAnimator.ofFloat(0f, 1f);
        translation.setDuration(250);
        translation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float values = (float) animation.getAnimatedValue();
                float x = (float) (resource.x - targetView.get(index).getParentX()) * values;
                float y = (float) (resource.y - targetView.get(index).getParentY()) * values;
                targetView.get(index).setTranslationX(x);
                targetView.get(index).setTranslationY(y);
                requestLayout();
            }
        });
        translation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                for (int i = 0; i < targetView.size(); i++) {
                    targetView.get(i).setTranslationX(0);
                    targetView.get(i).setTranslationY(0);
                }
                putData(targetView);
                isExChangeFinish = true;

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        translation.start();
    }

    public void setOnAngleChangeListener(OnAngleChangeListener listener) {
        mAngleListener = listener;
    }

    public void setOnAngleClickListener(OnClickListener listener) {
        mOnClickListener = listener;
    }

    public void setOnAngleLongClickListener(OnEditModeChangeListener listener) {
        mOnEditModeChangeListener = listener;
    }

    public void setOnBindListener(OnBindListener listener) {
        mOnBindListener = listener;
    }

    /**
     * 手指按下时的初始角度
     */
    public void downAngle(float x, float y) {
        mDownAngle = Math.toDegrees(Math.atan(x / y));
    }

    /**
     * 手指滑动时
     * 动态的计算一个变化量diff，在根据条件合理的计算
     *
     * @param x
     * @param y
     */
    public void changeAngle(float x, float y) {
        double diffAngle;
        double angle;
        angle = Math.toDegrees(Math.atan(x / y));
        diffAngle = angle - mDownAngle;
        if (diffAngle > 0) {
            ANGLE_STATE = ANGLE_STATE_ALONG;
        } else {
            ANGLE_STATE = ANGLE_STATE_INVERSE;
        }
        if (isLeft()) {
            changeAngle(diffAngle);
        } else if (isRight()) {
            changeAngle(-diffAngle);
        }
    }

    /**
     * 改变mChangeAngle的值
     *
     * @param rotation
     */
    private void changeAngle(double rotation) {
        mChangeAngle = (float) rotation;
        angleChange();
    }

    private void angleChange() {
        /**
         * 转动的时候回传当前限象index
         */
        mAngleListener.onAngleChanged(getViewsIndex((int) (getAngleValues() / DEGREES_90)), ((getAngleValues() % DEGREES_90) / DEGREES_90));
        invalidate();
    }

    /**
     * 松开手指后根据x,y方向上的速度来判定是留在当前限象还是旋转到上一个或者下一个限象
     *
     * @param vx
     * @param vy
     */
    public void fling(float vx, float vy) {
        if (isLeft()) {
            if (vy > ALLOW_FLING || vx > ALLOW_FLING) {
                flingForward();
            } else if (vx < -ALLOW_FLING || vy < -ALLOW_FLING) {
                flingCurrnet();
            } else {
                upAngle();
            }
        } else if (isRight()) {
            if (vy > ALLOW_FLING || vx < -ALLOW_FLING) {
                flingCurrnet();
            } else if (vy < -ALLOW_FLING || vx > ALLOW_FLING) {
                flingForward();
            } else {
                upAngle();
            }
        }

    }

    /**
     * 松手后根据当前已经旋转的角，POSITION，顺逆时针来判定是留在当前限象还是旋转到上一个或者下一个限象
     */
    private void upAngle() {
        if (ANGLE_STATE == ANGLE_STATE_ALONG) {
            forward();
        } else if (ANGLE_STATE == ANGLE_STATE_INVERSE) {
            reverse();
        }
    }

    /**
     * 顺时针旋转
     */
    private void forward() {

        float diff = 0;
        if (isLeft()) {
            diff = getAngleValues() % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingCurrnet();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                /**
                 * 转到下一个
                 */
                flingForward();
            }
        } else if (isRight()) {
            diff = DEGREES_90 - getAngleValues() % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingForward();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                /**
                 * 转到下一个
                 */
                flingCurrnet();
            }
        }
    }

    /**
     * 逆时针旋转
     */
    private void reverse() {
        float diff = 0;
        if (isLeft()) {
            diff = (DEGREES_1080 - getAngleValues()) % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingForward();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                /**
                 * 转到下一个
                 */
                flingCurrnet();
            }
        } else if (isRight()) {
            diff = getAngleValues() % DEGREES_90;
            if (diff > 0 && diff < DEGREES_OFFSET) {
                flingCurrnet();
            } else if (diff > DEGREES_OFFSET && diff < DEGREES_90) {
                flingForward();
            }
        }

    }

    /**
     * 顺时针到下一个90度
     */
    private void flingForward() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / DEGREES_90) + 1) * DEGREES_90);
    }

    /**
     * 回到当前的角度
     */
    private void flingCurrnet() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / DEGREES_90)) * DEGREES_90);
    }

    /**
     * 逆时针到上一个90度
     */
    private void flingReveser() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / DEGREES_90) - 1) * DEGREES_90);
    }

    /**
     * 根据当前的数据组和点击情况来切换AngleIndicator
     *
     * @param cur 当前被点击的指示器索引
     */
    public void setViewsIndex(int cur) {
        if (isLeft()) {
            int index = getViewsIndex((int) (getAngleValues() / DEGREES_90));
            if (index == 0) {
                if (cur == 1) {
                    flingReveser();
                } else if (cur == 2) {
                    flingForward();
                }
            } else if (index == 1) {
                if (cur == 0) {
                    flingForward();
                } else if (cur == 2) {
                    flingReveser();
                }
            } else if (index == 2) {
                if (cur == 0) {
                    flingReveser();
                } else if (cur == 1) {
                    flingForward();
                }
            }
        } else if (isRight()) {
            int index = getViewsIndex2((int) (getAngleValues() / DEGREES_90));
            if (index == 0) {
                if (cur == 1) {
                    flingForward();
                } else if (cur == 2) {
                    flingReveser();
                }
            } else if (index == 1) {
                if (cur == 2) {
                    flingForward();
                } else if (cur == 0) {
                    flingReveser();
                }
            } else if (index == 2) {
                if (cur == 0) {
                    flingForward();
                } else if (cur == 1) {
                    flingReveser();
                }
            }
        }

    }

    /**
     * 自动旋转
     *
     * @param start 起始位置
     * @param end   结束为止
     */
    private void autoWhirling(float start, float end) {
        mChangeAngle = 0;
        mAngleAnimator = ValueAnimator.ofFloat(start, end);
        mAngleAnimator.setDuration(500);
        mAngleAnimator.setInterpolator(new DecelerateInterpolator());
        mAngleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBaseAngle = value;
                mBaseAngle = mBaseAngle % DEGREES_1080;
                angleChange();
            }
        });
        mAngleAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                int mIndex = ((int) ((mBaseAngle) / DEGREES_90));
                if (isLeft()) {
                    /**
                     * getQuaIndex()
                     */
                    itemLayout(mIndex);

                } else if (isRight()) {
                    itemLayout2(mIndex);

                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        mAngleAnimator.start();

    }

    public void setPositionState(int position) {
        super.setPositionState(position);
    }

    /**
     * 转动时候随时可以获取到的的转动角度
     *
     * @return
     */
    public float getAngleValues() {
        float newrotation = (mBaseAngle + mChangeAngle);
        return newrotation < 0 ? DEGREES_1080 + (newrotation) : (newrotation);
    }

    public float getBaseAngle() {
        return mBaseAngle;
    }

    public void setBaseAngle(float angle) {
        mBaseAngle = angle;
        angleChange();
    }

    public int getCurrentIndex() {
        return mCurrentIndex;
    }

    /**
     * 根据当前的index获取当前显示限象index
     * 比如11->1,10->2,9->3,8->0
     *
     * @param index 转动结束后根据BaseAngle的值除以90得出的范围0-11
     *              3,4的最小公倍数的是12
     * @return
     */
    public int getQuaIndex(int index) {
        return index == 0 ? 0 : (COUNT_12 - index) % COUNT_4;
    }

    /**
     * 获取真实的限象Index
     *
     * @param index
     * @return
     */
    public int getRealIndex(int index) {
        return index == 0 ? 0 : (COUNT_12 - index);
    }

    /**
     * STATE=RIGHT时
     *
     * @param index
     * @return
     */
    public int getQuaIndex2(int index) {
        return index < 0 ? COUNT_4 + index : index % COUNT_4;
    }

    /**
     * 获取当前index的上一个index
     *
     * @param index 传入的是getIndex()的返回值
     * @return 得到上一个index
     */
    public int getPreQuaIndex(int index) {
        return index == 0 ? COUNT_3 : (index - 1);
    }

    /**
     * 获取当前index的下一个index
     *
     * @param index 传入的是getIndex()的返回值
     * @return 得到下一个index
     */
    private int getNextQuaIndex(int index) {
        return index == COUNT_3 ? 0 : (index + 1);
    }


    /**
     * 根据index获取当先index所需要的数据索引
     * 比如: 11->1,10->2,9->0,8->1,7->2,6->0  像这样一直循环
     *
     * @param index 转动结束后根据BaseAngle的值除以90得出的范围0-11
     *              3,4的最小公倍数的是12
     * @return
     */
    private int getViewsIndex(int index) {
        return (COUNT_12 - index) % COUNT_3;
    }

    /**
     * getViewsIndex(int index)重载方法
     *
     * @return 直接返回当前显示数据的Index索引值
     */
    public int getViewsIndex() {
        if (isLeft()) {
            return getViewsIndex(((int) ((mBaseAngle) / DEGREES_90)));
        } else {
            return getViewsIndex2(((int) ((mBaseAngle) / DEGREES_90)));
        }
    }

    public int getQuaIndex() {
        int index = (int) ((mBaseAngle) / DEGREES_90);
        if (isLeft()) {
            return getQuaIndex(index);
        } else {
            return getQuaIndex2(index);
        }
    }

    /**
     * 根据当前的显示数据的Index来从HashMap中的取出数据
     *
     * @return 当前显示在第0限象位置的数据
     */
    public ArrayList<AngleItemCommon> getData() {
        return mMap.get(getViewsIndex());
    }

    public void putData(ArrayList<AngleItemCommon> arrayList) {
        mMap.put(getViewsIndex(), arrayList);
        refresh();
    }


    /**
     * mPositionState=Right
     *
     * @param index 根据BaseAngle求出的实际限象数
     * @return 转换为右侧的实际Index
     */
    private int getViewsIndex2(int index) {
        return index < 0 ? COUNT_3 + index : index % COUNT_3;
    }

    /**
     * 上一个数据索引
     *
     * @param index 传入的是getViews()的返回值
     * @return
     */
    public int getPreViewsIndex(int index) {
        return index == 0 ? 2 : (index - 1);
    }

    /**
     * 下一个数据索引
     *
     * @param index 传入的是getViews()的返回值
     * @return
     */
    public int getNextViewsIndex(int index) {
        return index == 2 ? 0 : (index + 1);
    }

    /**
     * 进入编辑模式，遍历当前的Views集合，显示删除按钮
     */
    public void startEditMode() {
        int index = getViewsIndex();

        for (int i = 0; i < mMap.get(index).size(); i++) {
            AngleItemCommon item = mMap.get(index).get(i);
            if (item instanceof AngleItemStartUp) {
                ((AngleItemStartUp) item).showDelBtn();
            }
        }
    }

    /**
     * 退出当前编辑模式，遍历当前的Views集合，隐藏编辑按钮
     */
    public void endEditMode() {
        isMoveDrag = true;
        int index = getViewsIndex();
        for (int i = 0; i < mMap.get(index).size(); i++) {
            AngleItemCommon item = mMap.get(index).get(i);
            if (item instanceof AngleItemStartUp) {
                ((AngleItemStartUp) item).hideDelBtn();
            }
        }
        /**
         * 退出编辑模式时把isMoveDrag置空，目的是再次出发编辑模式是Move可以出发拖动
         */


    }

    public float getChildHalfSize() {
        return mChildHalfSize;
    }

    public AngleItemCommon getTargetItem() {
        return mTargetItem;
    }

    /**
     * 抖一下
     *
     * @param view
     */
    public void shake(final View view) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1.0f, 0.85f);
        valueAnimator.setRepeatCount(1);
        valueAnimator.setDuration(60);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float scale = (float) animation.getAnimatedValue();
                view.setScaleX(scale);
                view.setScaleY(scale);
                requestLayout();
            }
        });
        valueAnimator.start();
    }

    public class LongClickRunable implements Runnable {
        @Override
        public void run() {
            if (mOnEditModeChangeListener != null) {
                if (getAngleValues() % DEGREES_90 == 0 && getViewsIndex() != 0) {
                    mVibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                    long[] pattern = {0, 35};
                    mVibrator.vibrate(pattern, -1);
                    mOnEditModeChangeListener.onEnterEditMode(mTargetItem);
                    startEditMode();
                }
            } else {
                throw new IllegalArgumentException("AngleView.OnClickListener is null(AngleView的Click监听接口对象为空)");
            }
        }
    }

}
