package com.well.swipe.view;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by mingwei on 2/25/16.
 */
public class AngleView extends ViewGroup {
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

    static int LEFT = 1;

    static int RIGHT = 2;
    /**
     * 容器在做右下角区分
     */
    public int POSITION = LEFT;
    /**
     * 顺时针/逆时针
     */
    public int ANGLE_STATE = ANGLE_STATE_REST;

    public static final int ANGLE_STATE_REST = 0;

    public static final int ANGLE_STATE_ALONG = 1;

    public static final int ANGLE_STATE_INVERSE = 2;

    public static final int DEGREES_360 = 360;
    /**
     * 单位度数
     */
    public static final int DEGREES_90 = 90;
    /**
     * 判定范围
     */
    public static final int OFFSET_DEGREES = 20;
    /**
     * 判定Fling动作的域
     */
    private static final int ALLOW_FLING = 2500;
    /**
     * 当前的限象
     */
    private int mIndex = 0;

    private static final int COUNT_4 = 4;

    private Map<Integer, ArrayList<TextView>> mMap = new HashMap<>();
    /**
     * 三部分数据的分割点值的数组，这在布局的时候用来根据此值来求索引
     */
    private int mIndexArray[] = new int[4];

    public AngleView(Context context) {
        this(context, null);
    }

    public AngleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ArrayList<TextView> list0 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            TextView view = new TextView(context);
            view.setGravity(Gravity.CENTER);
            view.setText("A=" + i);
            list0.add(view);
        }
        mMap.put(0, list0);

        ArrayList<TextView> list1 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            TextView view = new TextView(context);
            view.setGravity(Gravity.CENTER);
            view.setText("B=" + i);
            list1.add(view);
        }
        mMap.put(1, list1);

        ArrayList<TextView> list2 = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            TextView view = new TextView(context);
            view.setGravity(Gravity.CENTER);
            view.setText("C=" + i);
            list2.add(view);
        }
        mMap.put(2, list2);

        Iterator<Map.Entry<Integer, ArrayList<TextView>>> it = mMap.entrySet().iterator();
        int count = 0;
        int index = 0;
        while (it.hasNext()) {
            Map.Entry<Integer, ArrayList<TextView>> arraylist = it.next();
            ArrayList<TextView> views = arraylist.getValue();
            mIndexArray[count] = index;
            for (TextView view : views) {
                addView(view);
                index++;
            }
            count++;
        }
        mIndexArray[mIndexArray.length - 1] = getChildCount();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mPivotX = getMeasuredWidth();
        mPivotY = getMeasuredHeight();
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (POSITION == LEFT) {
            Iterator<Map.Entry<Integer, ArrayList<TextView>>> it = mMap.entrySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                Map.Entry<Integer, ArrayList<TextView>> arraylist = it.next();
                ArrayList<TextView> views = arraylist.getValue();
                if (arraylist.getKey() == 0) {
                    /**
                     * 把第0组数据放在第0限象
                     */
                    itemLayout(views, 0);

                    for (int i = 0; i < views.size(); i++) {
                        views.get(i).setRotationY(180);
                    }

                } else if (arraylist.getKey() == 1) {
                    /**
                     * 把第1组数据放在第1限象
                     */
                    itemLayout(views, 1);

                } else if (arraylist.getKey() == 2) {
                    /**
                     * 把第2组数据放在第3限象
                     */
                    itemLayout(views, 3);

                }

                count++;
            }
        } else {
            Iterator<Map.Entry<Integer, ArrayList<TextView>>> it = mMap.entrySet().iterator();
            int count = 0;
            while (it.hasNext()) {
                Map.Entry<Integer, ArrayList<TextView>> arraylist = it.next();
                ArrayList<TextView> views = arraylist.getValue();
                if (arraylist.getKey() == 0) {
                    /**
                     * 把第0组数据放在第0限象
                     */
                    itemLayout(views, 0);

                } else if (arraylist.getKey() == 1) {
                    /**
                     * 把第1组数据放在第1限象
                     */
                    itemLayout(views, 1);
                } else if (arraylist.getKey() == 2) {
                    /**
                     * 把第2组数据放在第3限象
                     */
                    itemLayout(views, 3);
                }

                count++;
            }
        }
    }

    /**
     * 布局子控价位置
     *
     * @param views  当前圈的views
     * @param radius 当前圈的半径值
     * @param index  子控件在当前数据组中的索引
     * @param group  子控件在当前圈中的索引
     * @param size   当前的数据组中的总长度，考虑到子控件要布局两圈，所以要区分当前所在的圈上的子控件数量
     */
    private void layouttest(ArrayList<TextView> views, int radius, int index, int group, int qua, int size) {
        float degree = (float) DEGREES_90 / (float) (size);
        float newdegree;
        if (index == 0) {
            newdegree = degree / 2;
        } else {
            newdegree = (int) ((group + 0.5) * degree);
        }
        double x = 0l;
        double y = 0l;
        if (qua == 0) {
            x = Math.sin(Math.toRadians(newdegree)) * radius;
            y = mHeight - Math.cos(Math.toRadians(newdegree)) * radius;
        } else if (qua == 1) {
            x = Math.cos(Math.toRadians(newdegree)) * radius;
            y = mHeight + Math.sin(Math.toRadians(newdegree)) * radius;
        }
        views.get(index).layout((int) (x - 60), (int) (y - 60), (int) (x + 60), (int) (y + 60));
    }

    /**
     * 把一组数据指定布局到一个限象
     *
     * @param views 需要布局的数据组
     * @param qua   限象
     */
    private void itemLayout(ArrayList<TextView> views, int qua) {
        for (int index = 0; index < views.size(); index++) {
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
                radius = 370;
            } else {
                if (index < COUNT_4) {
                    /**
                     * 总数大于4时内环正好是4
                     */
                    size = COUNT_4;
                    group = index;
                    radius = 370;
                } else {
                    /**
                     * 总数大于4时外环
                     * size＝总数－4
                     * group＝views(index)-4
                     */
                    size = views.size() - COUNT_4;
                    group = index - COUNT_4;
                    radius = 540;
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
             * 按照限象使用不同的三角函数计算所得x,y坐标
             * 子控件根据不同的呃限象旋转位置满足在第0限象的正常显示效果
             */
            double x = 0l;
            double y = 0l;
            if (qua == 0) {
                x = Math.sin(Math.toRadians(newdegree)) * radius;
                y = mHeight - Math.cos(Math.toRadians(newdegree)) * radius;
            } else if (qua == 1) {
                x = Math.cos(Math.toRadians(newdegree)) * radius;
                y = mHeight + Math.sin(Math.toRadians(newdegree)) * radius;
                /**
                 * 旋转一定的角度
                 */
                views.get(index).setRotation(DEGREES_90 * 1);
            } else if (qua == 2) {
                x = -Math.sin(Math.toRadians(newdegree)) * radius;
                y = mHeight + Math.cos(Math.toRadians(newdegree)) * radius;
                views.get(index).setRotation(DEGREES_90 * 2);
            } else if (qua == 3) {
                x = -Math.cos(Math.toRadians(newdegree)) * radius;
                y = mHeight - Math.sin(Math.toRadians(newdegree)) * radius;
                views.get(index).setRotation(DEGREES_90 * 3);
            }
            /**
             * 指定位置
             */
            views.get(index).layout((int) (x - 60), (int) (y - 60), (int) (x + 60), (int) (y + 60));
        }


    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.save();
        if (POSITION == LEFT) {
            canvas.rotate(mBaseAngle + mChangeAngle, 0, mPivotY);
        } else {
            canvas.rotate(mBaseAngle + mChangeAngle, mPivotX, mPivotY);
        }
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    /**
     * 手指按下时的初始角度
     */
    public void downAngle(float x, float y) {
        if (POSITION == LEFT) {
            mDownAngle = Math.toDegrees(Math.atan(x / y));
        } else {
            mDownAngle = DEGREES_360 - Math.toDegrees(Math.atan(x / y));
        }
        ANGLE_STATE = ANGLE_STATE_REST;

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
        if (POSITION == LEFT) {
            angle = Math.toDegrees(Math.atan(x / y));
        } else {
            angle = DEGREES_360 - Math.toDegrees(Math.atan(x / y));
        }
        diffAngle = angle - mDownAngle;
        if (diffAngle > 0) {
            ANGLE_STATE = AngleView.ANGLE_STATE_ALONG;
        } else {
            ANGLE_STATE = AngleView.ANGLE_STATE_INVERSE;
        }


        changeAngle(diffAngle);
    }

    /**
     * 改变mChangeAngle的值
     *
     * @param rotation
     */
    private void changeAngle(double rotation) {
        mChangeAngle = (float) rotation;
        invalidate();
    }

    /**
     * 松手后根据当前已经旋转的角，POSITION，顺逆时针来判定是留在当前限象还是旋转到上一个或者下一个限象
     */
    private void upAngle() {
        if (ANGLE_STATE == AngleView.ANGLE_STATE_ALONG) {
            along();
        } else {
            recover();
        }
    }

    /**
     * 松开手指后根据x,y方向上的速度来判定是留在当前限象还是旋转到上一个或者下一个限象
     *
     * @param vx
     * @param vy
     */
    public void flingAngle(float vx, float vy) {
        if (POSITION == AngleView.LEFT) {
            if (vy > ALLOW_FLING || vx > ALLOW_FLING) {
                flingNext();
            } else if (vx < -ALLOW_FLING || vy < -ALLOW_FLING) {
                flingCurrnet();
            } else {
                upAngle();
            }
        } else {
            if (vx > ALLOW_FLING || vy < -ALLOW_FLING) {
                flingNext();
            } else if (vx < -ALLOW_FLING || vy > ALLOW_FLING) {
                flingCurrnet();
            } else {
                upAngle();
            }
        }
    }

    /**
     * 顺时针旋转
     */
    private void along() {
        float diff = getAngleValues() % AngleView.DEGREES_90;
        if (diff > 0 && diff < AngleView.OFFSET_DEGREES) {
            flingCurrnet();
        } else if (diff > AngleView.OFFSET_DEGREES && diff < DEGREES_90) {
            flingNext();
        }
    }

    /**
     * 逆时针旋转
     */
    private void recover() {
        float diff = (DEGREES_360 - getAngleValues()) % AngleView.DEGREES_90;
        if (diff > 0 && diff < AngleView.OFFSET_DEGREES) {
            flingNext();
        } else if (diff > AngleView.OFFSET_DEGREES && diff < DEGREES_90) {
            flingCurrnet();
        }
    }

    /**
     * 顺时针到下一个90度
     */
    private void flingNext() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / AngleView.DEGREES_90) + 1) * AngleView.DEGREES_90);
    }

    /**
     * 回到当前的角度
     */
    private void flingCurrnet() {
        autoWhirling(getAngleValues(), ((int) (getAngleValues() / AngleView.DEGREES_90)) * AngleView.DEGREES_90);
    }

    private void autoWhirling(float start, float end) {
        mChangeAngle = 0;
        ValueAnimator animator = ValueAnimator.ofFloat(start, end);
        animator.setDuration(400);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                mBaseAngle = value;
                mBaseAngle = mBaseAngle % DEGREES_360;
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (POSITION == LEFT) {
                    mIndex = ((int) ((mBaseAngle) / DEGREES_90));
                }
                requestLayout();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    /**
     * 转动时候随时可以获取到的的转动角度
     *
     * @return
     */
    public float getAngleValues() {
        float newrotation = (mBaseAngle + mChangeAngle);
        return newrotation < 0 ? DEGREES_360 + (newrotation) : (newrotation);
    }


    public int getIndex(int index) {
        return index == 0 ? 0 : COUNT_4 - index;
    }

    public int getNullIndex(int index) {
        return (index + 2) % COUNT_4;
    }


}
