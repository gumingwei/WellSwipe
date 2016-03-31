package com.well.swipe.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.support.annotation.IntRange;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.SeekBar;

import com.well.swipe.R;

import java.util.concurrent.Callable;

/**
 * Created by mingwei on 3/31/16.
 */
public class SeekBarCompat extends SeekBar implements View.OnTouchListener {

    private static final String TAG = "SeekBarCompat";

    private int mActualBackgroundColor;
    /***
     * Thumb and Progress colors
     */
    int mThumbColor, mProgressColor, mProgressBackgroundColor;
    /***
     * Thumb drawable
     */
    Drawable mThumb;
    /***
     * States for Lollipop ColorStateList
     */
    int[][] states = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{android.R.attr.state_pressed},  // pressed
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{} //everything else
    };

    /***
     * Default colors to be black for Thumb ColorStateList
     */
    int[] colorsThumb = new int[]{
            Color.BLACK,
            Color.BLACK,
            Color.LTGRAY,
            Color.BLACK
    };

    /***
     * Default colors to be black for Progress ColorStateList
     */
    int[] colorsProgress = new int[]{
            Color.BLACK,
            Color.BLACK,
            Color.LTGRAY,
            Color.BLACK
    };

    /***
     * Default colors to be black for Progress ColorStateList
     */
    int[] colorsProgressBackground = new int[]{
            Color.BLACK,
            Color.BLACK,
            Color.LTGRAY,
            Color.BLACK

    };

    /***
     * ColorStateList objects
     */
    ColorStateList mColorStateListThumb, mColorStateListProgress, mColorStateListProgressBackground;

    /***
     * Used for APIs below 21 to determine height of the seekBar as well as the new thumb drawable
     */
    private int mOriginalThumbHeight;
    private int mThumbAlpha = 255;
    private boolean mIsEnabled = true;

    /***
     * Updates the thumbColor dynamically
     *
     * @param thumbColor Color representing thumb drawable
     */
    public void setThumbColor(final int thumbColor) {
        mThumbColor = thumbColor;
        if (lollipopAndAbove()) {
            setupThumbColorLollipop();
        } else {
            gradientDrawable.setColor(mIsEnabled ? thumbColor : Color.LTGRAY);
        }
        invalidate();
        requestLayout();
    }

    /***
     * Method called for APIs 21 and above to setup thumb Color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupThumbColorLollipop() {
        if (lollipopAndAbove()) {
            colorsThumb[0] = mThumbColor;
            colorsThumb[1] = mThumbColor;
            colorsThumb[2] = Color.LTGRAY;
            mColorStateListThumb = new ColorStateList(states, colorsThumb);
            setThumbTintList(mColorStateListThumb);
        } else {

        }
    }

    /***
     * Updates the progressColor dynamically
     *
     * @param progressColor Color representing progress drawable
     */
    public void setProgressColor(final int progressColor) {
        mProgressColor = progressColor;
        if (lollipopAndAbove()) {
            setupProgressColorLollipop();
        } else {
            setupProgressColor();
        }

        invalidate();
        requestLayout();
    }

    /***
     * Checks if the device is running API greater than 21
     *
     * @return true if lollipop and above
     */
    private boolean lollipopAndAbove() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /***
     * Method called from APIs below 21 to setup Progress Color
     */
    private void setupProgressColor() {
        try {
            //load up the drawable and apply color
            LayerDrawable ld = (LayerDrawable) getProgressDrawable();
            ScaleDrawable shape = (ScaleDrawable) (ld.findDrawableByLayerId(android.R.id.progress));
            shape.setColorFilter(mProgressColor, PorterDuff.Mode.SRC_IN);

            //set the background to transparent
            NinePatchDrawable ninePatchDrawable = (NinePatchDrawable) (ld.findDrawableByLayerId(android.R.id.background));
            ninePatchDrawable.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
        } catch (NullPointerException e) {
            //TODO: Handle exception
        }
    }

    /***
     * Method called from APIs >= 21 to setup Progress Color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupProgressColorLollipop() {
        colorsProgress[0] = mProgressColor;
        colorsProgress[1] = mProgressColor;
        mColorStateListProgress = new ColorStateList(states, colorsProgress);
        setProgressTintList(mColorStateListProgress);
    }

    /***
     * Updates the progressBackgroundColor dynamically
     *
     * @param progressBackgroundColor Color representing progress drawable
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setProgressBackgroundColor(final int progressBackgroundColor) {
        mProgressBackgroundColor = progressBackgroundColor;
        if (lollipopAndAbove()) {
            setupProgressBackgroundLollipop();
        } else {
            setupProgressBackground();
        }
        invalidate();
        requestLayout();
    }

    /***
     * Method called from APIs 21 and above to setup the Progress-background-line Color
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupProgressBackgroundLollipop() {
        colorsProgressBackground[0] = mProgressBackgroundColor;
        colorsProgressBackground[1] = mProgressBackgroundColor;
        mColorStateListProgressBackground = new ColorStateList(states, colorsProgressBackground);
        setProgressBackgroundTintList(mColorStateListProgressBackground);
    }

    /***
     * Method called from APIs below 21 to setup the Progress-background-line Color
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setupProgressBackground() {
        //load up the drawable and apply color
        SeekBarBackgroundDrawable seekBarBackgroundDrawable = new SeekBarBackgroundDrawable(getContext(),
                mProgressBackgroundColor, mActualBackgroundColor, getPaddingLeft(), getPaddingRight());
        if (belowJellybean())
            setBackgroundDrawable(seekBarBackgroundDrawable);
        else
            setBackground(seekBarBackgroundDrawable);
    }

    /***
     * Constructor for creating SeekBarCompat through code
     *
     * @param context Context object
     */
    public SeekBarCompat(final Context context) {
        super(context);
    }

    GradientDrawable gradientDrawable = new GradientDrawable();

    /***
     * Constructor for creating SeekBarCompat through XML
     *
     * @param context Context Object
     * @param attrs   Attributes passed through XML
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SeekBarCompat(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme()
                .obtainStyledAttributes(
                        attrs,
                        R.styleable.SeekBarCompat,
                        0, 0);
        int array[] = {android.R.attr.background, android.R.attr.enabled};
        TypedArray b = context.getTheme()
                .obtainStyledAttributes(attrs, array, 0, 0);
        try {
            mThumbColor = a.getColor(R.styleable.SeekBarCompat_thumbColor, getPrimaryColorFromSelectedTheme(context));
            mProgressColor = a.getColor(R.styleable.SeekBarCompat_progressColor, getPrimaryColorFromSelectedTheme(context));
            mProgressBackgroundColor = a.getColor(R.styleable.SeekBarCompat_progressBackgroundColor, Color.BLACK);
            mThumbAlpha = (int) (a.getFloat(R.styleable.SeekBarCompat_thumbAlpha, 1) * 255);
            mActualBackgroundColor = b.getColor(0, Color.TRANSPARENT);
            mIsEnabled = b.getBoolean(1, true);
            if (lollipopAndAbove()) {
                setSplitTrack(false);
                setupThumbColorLollipop();
                setupProgressColorLollipop();
                setupProgressBackgroundLollipop();
                getThumb().setAlpha(mThumbAlpha);
            } else {
                Log.e(TAG, "SeekBarCompat isEnabled? " + mIsEnabled);
                setupProgressColor();
                setOnTouchListener(this);
                gradientDrawable.setShape(GradientDrawable.OVAL);
                gradientDrawable.setSize(50, 50);
                gradientDrawable.setColor(mIsEnabled ? mThumbColor : Color.LTGRAY);
                triggerMethodOnceViewIsDisplayed(this, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        ViewGroup.LayoutParams layoutParams = getLayoutParams();
                        mOriginalThumbHeight = mThumb.getIntrinsicHeight();
                        gradientDrawable.setSize(mOriginalThumbHeight / 3, mOriginalThumbHeight / 3);
                        gradientDrawable.setAlpha(mThumbAlpha);
                        setThumb(gradientDrawable);
                        if (layoutParams.height < mOriginalThumbHeight)
                            layoutParams.height = mOriginalThumbHeight;
                        setupProgressBackground();
                        return null;
                    }
                });
            }
        } finally {
            a.recycle();
            b.recycle();
        }
    }

    private boolean belowJellybean() {
        return Build.VERSION.SDK_INT < 16;
    }

    /***
     * Gets the Primary Color from theme
     *
     * @param context Context Object
     * @return Primary Color
     */
    public static int getPrimaryColorFromSelectedTheme(Context context) {
        int[] attrs = {R.attr.colorPrimary, R.attr.colorPrimaryDark};
        TypedArray ta = context.getTheme()
                .obtainStyledAttributes(attrs);
        int primaryColor = ta.getColor(0, Color.BLACK); //1 index for primaryColorDark
        //default value for primaryColor is set to black if primaryColor not found
        ta.recycle();
        return primaryColor;
    }

    /***
     * Utility method for ViewTreeObserver
     *
     * @param view   View object
     * @param method Method to be called once View is displayed
     */
    public static void triggerMethodOnceViewIsDisplayed(final View view, final Callable<Void> method) {
        final ViewTreeObserver observer = view.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver()
                            .removeGlobalOnLayoutListener(this);
                } else view.getViewTreeObserver()
                        .removeOnGlobalLayoutListener(this);
                try {
                    method.call();
                } catch (Exception e) {
                    Log.e(TAG, "onGlobalLayout " + e.toString());
                }
            }
        });
    }

    /***
     * Touch listener for changing Thumb Drawable
     *
     * @param v     View Object
     * @param event Motion Event
     * @return
     */
    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (!lollipopAndAbove())
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.OVAL);
                    gradientDrawable.setSize(mOriginalThumbHeight / 2, mOriginalThumbHeight / 2);
                    gradientDrawable.setColor(mIsEnabled ? mThumbColor : Color.LTGRAY);
                    gradientDrawable.setDither(true);
                    gradientDrawable.setAlpha(mThumbAlpha);
                    setThumb(gradientDrawable);
                    break;
                case MotionEvent.ACTION_UP:
                    gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.OVAL);
                    gradientDrawable.setSize(mOriginalThumbHeight / 3, mOriginalThumbHeight / 3);
                    gradientDrawable.setColor(mIsEnabled ? mThumbColor : Color.LTGRAY);
                    gradientDrawable.setDither(true);
                    gradientDrawable.setAlpha(mThumbAlpha);
                    setThumb(gradientDrawable);
                    break;
            }
        return false;
    }


    /***
     * Called to substitute getThumb() for APIs below 16
     *
     * @param thumb
     */
    @Override
    public void setThumb(final Drawable thumb) {
        super.setThumb(thumb);
        mThumb = thumb;
    }

    /***
     * Sets the thumb alpha (Obviously)
     *
     * @param alpha
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setThumbAlpha(@IntRange(from = 0, to = 255) int alpha) {
        mThumbAlpha = alpha;
        if (!belowJellybean())
            getThumb().setAlpha(mThumbAlpha);
        setLayoutParams(getLayoutParams());
    }

    /***
     * Enables or disables the whole seekBar!
     *
     * @param enabled
     */
    @Override
    public void setEnabled(final boolean enabled) {
        mIsEnabled = enabled;
        triggerMethodOnceViewIsDisplayed(this, new Callable<Void>() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public Void call() throws Exception {
                if (!lollipopAndAbove()) {
                    gradientDrawable = new GradientDrawable();
                    gradientDrawable.setShape(GradientDrawable.OVAL);
                    gradientDrawable.setSize(mOriginalThumbHeight / 3, mOriginalThumbHeight / 3);
                    gradientDrawable.setColor(mIsEnabled ? mThumbColor : Color.LTGRAY);
                    gradientDrawable.setDither(true);
                    gradientDrawable.setAlpha(mThumbAlpha);
                    setThumb(gradientDrawable);
                    //load up the drawable and apply color
                    LayerDrawable ld = (LayerDrawable) getProgressDrawable();
                    ScaleDrawable shape = (ScaleDrawable) (ld.findDrawableByLayerId(android.R.id.progress));
                    shape.setColorFilter(mIsEnabled ? mProgressColor : Color.LTGRAY, PorterDuff.Mode.SRC_IN);
                    //set the background to transparent
                    NinePatchDrawable ninePatchDrawable = (NinePatchDrawable) (ld.findDrawableByLayerId(android.R.id.background));
                    ninePatchDrawable.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.SRC_IN);
                    //background
                    //load up the drawable and apply color
                    SeekBarBackgroundDrawable seekBarBackgroundDrawable = new SeekBarBackgroundDrawable(getContext(),
                            mIsEnabled ? mProgressBackgroundColor : Color.LTGRAY, mActualBackgroundColor, getPaddingLeft(), getPaddingRight());
                    if (belowJellybean())
                        setBackgroundDrawable(seekBarBackgroundDrawable);
                    else
                        setBackground(seekBarBackgroundDrawable);
                }
                SeekBarCompat.super.setEnabled(enabled);
                return null;
            }
        });

    }
}