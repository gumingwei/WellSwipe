package com.well.swipe.activitys;


import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.well.swipe.R;
import com.well.swipe.preference.PreferenceCategory;
import com.well.swipe.preference.PreferenceTitle;
import com.well.swipe.preference.PreferenceTitleSummary;
import com.well.swipe.preference.SwipeAreaDialog;
import com.well.swipe.preference.SwipeDialog;
import com.well.swipe.preference.SwipeForDialog;
import com.well.swipe.preference.SwipeWhitelistDialog;
import com.well.swipe.service.SwipeService;
import com.well.swipe.tools.SwipeSetting;
import com.well.swipe.utils.SettingHelper;
import com.well.swipe.view.CheckItemLayout;

public class SwipeSettingActivity extends AppCompatActivity implements View.OnClickListener {

    TextView test;
    /**
     * 用来存SwipeService是否打开
     */
    PreferenceCategory mSwipeToggle;
    /**
     * 用来存滑出时机
     * 0仅桌面
     * 1桌面和其他App
     */
    PreferenceTitleSummary mSwipeFor;

    /**
     * 用来存滑出时机
     * 0左侧底部和右侧底部
     * 1仅左侧底部
     * 2仅右侧底部
     */
    PreferenceTitleSummary mSwipeArea;
    /**
     * Swipe划出的三种方式
     */
    private int mSwipAreaValue;
    /**
     * Swipe划出范围百分数
     */
    private int mSeekBarProgress;
    /**
     * 白名单
     */
    PreferenceTitleSummary mSwipeWhitelist;
    /**
     * 关于
     */
    PreferenceCategory mAboutCategory;
    /**
     * 评分
     */
    PreferenceTitle mRoter5Star;
    /**
     * 反馈
     */
    PreferenceTitle mFeedback;
    /**
     * 版本
     */
    PreferenceTitle mVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = (TextView) findViewById(R.id.test_text);
        test.setText("density=" + this.getResources().getDisplayMetrics().density + ",swipe_dialog_for=" +
                getResources().getDimensionPixelSize(R.dimen.test));
        findViewById(R.id.test_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mSwipeToggle = (PreferenceCategory) findViewById(R.id.swipe_toggle);
        mSwipeToggle.setTitle(getResources().getString(R.string.swipe_toggle));
        mSwipeToggle.setKey(SwipeSetting.SWIPE_TOGGLE);
        mSwipeToggle.getSwitchBtn().setChecked(mSwipeToggle.getBooleanValue());
        if (mSwipeToggle.getBooleanValue()) {
            startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
        }
        mSwipeToggle.getSwitchBtn().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipeToggle.setValues(isChecked);
                if (isChecked) {
                    startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
                } else {
                    stopService(new Intent(SwipeSettingActivity.this, SwipeService.class));
                }
            }
        });

        mSwipeFor = (PreferenceTitleSummary) findViewById(R.id.swipe_for);
        mSwipeFor.setKey(SwipeSetting.SWIPE_FOR);
        mSwipeFor.setTitle(getResources().getString(R.string.swipe_for));
        mSwipeFor.setSummaryArray(getResources().getStringArray(R.array.swipe_for_type));
        mSwipeFor.refreshSummary();
        mSwipeFor.setOnClickListener(this);

        mSwipeArea = (PreferenceTitleSummary) findViewById(R.id.swipe_area);
        mSwipeArea.setKey(SwipeSetting.SWIPE_AREA);
        mSwipeArea.setTitle(getResources().getString(R.string.swipe_active_area));
        mSwipeArea.setSummaryArray(getResources().getStringArray(R.array.swipe_area_type));
        mSwipeArea.refreshSummary();
        mSwipeArea.setOnClickListener(this);

        mSwipeWhitelist = (PreferenceTitleSummary) findViewById(R.id.swipe_whitelist);
        mSwipeWhitelist.setKey(SwipeSetting.SWIPE_WHITELIST);
        mSwipeWhitelist.setTitle(getResources().getString(R.string.swipe_whitelist));
        mSwipeWhitelist.setSummary(getResources().getString(R.string.swipe_whitelist_des));
        mSwipeWhitelist.setOnClickListener(this);

        mAboutCategory = (PreferenceCategory) findViewById(R.id.swipe_about);
        mAboutCategory.setTitle(getResources().getString(R.string.swipe_about));
        mAboutCategory.setToggleVisiable(View.GONE);

        mRoter5Star = (PreferenceTitle) findViewById(R.id.swipe_rate);
        mRoter5Star.setTitle(getResources().getString(R.string.swipe_about_rate5start));
        mRoter5Star.setOnClickListener(this);

        mFeedback = (PreferenceTitle) findViewById(R.id.swipe_feedback);
        mFeedback.setTitle(getResources().getString(R.string.swipe_about_feedback));
        mFeedback.setOnClickListener(this);

        mVersion = (PreferenceTitle) findViewById(R.id.swipe_version);
        mVersion.setTitle(getResources().getString(R.string.swipe_about_version));
        mVersion.setOnClickListener(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        //startService(new Intent(SwipeSettingActivity.this, SwipeService.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View v) {
        if (v == mSwipeFor) {
            final SwipeForDialog dialog = new SwipeForDialog(this);
            dialog.setTitle(getString(R.string.swipe_for)).
                    addItem(mSwipeFor.getSummaryArray()[0], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeFor.setValues(0);
                            mSwipeFor.refreshSummary();
                            dialog.dissmis();
                        }
                    }, mSwipeFor.getIntValue() == 0).
                    addItem(mSwipeFor.getSummaryArray()[1], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeFor.setValues(1);
                            mSwipeFor.refreshSummary();
                            dialog.dissmis();
                        }
                    }, mSwipeFor.getIntValue() == 1).show();

        } else if (v == mSwipeArea) {
            final SwipeAreaDialog dialog = new SwipeAreaDialog(this);
            mSwipAreaValue = mSwipeArea.getIntValue();
            mSeekBarProgress = SettingHelper.getInstance(this).getInt(SwipeSetting.SWIPE_AREA_PROGRESS, 50);


            dialog.setTitle(getString(R.string.swipe_active_area)).
                    addItem(mSwipeArea.getSummaryArray()[1], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckItemLayout view = (CheckItemLayout) v;
                            if (mSwipAreaValue == 0) {
                                if (view.isChecked()) {
                                    view.setChecked(false);
                                    mSwipAreaValue = 2;
                                }
                            } else if (mSwipAreaValue == 2) {
                                if (!view.isChecked()) {
                                    view.setChecked(true);
                                    mSwipAreaValue = 0;
                                }
                            }
                        }
                    }, mSwipeArea.getIntValue() == 0 || mSwipeArea.getIntValue() == 1).
                    addItem(mSwipeArea.getSummaryArray()[2], new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CheckItemLayout view = (CheckItemLayout) v;
                            if (mSwipAreaValue == 0) {
                                if (view.isChecked()) {
                                    view.setChecked(false);
                                    mSwipAreaValue = 1;
                                }
                            } else if (mSwipAreaValue == 1) {
                                if (!view.isChecked()) {
                                    view.setChecked(true);
                                    mSwipAreaValue = 0;
                                }
                            }
                        }
                    }, mSwipeArea.getIntValue() == 0 || mSwipeArea.getIntValue() == 2).
                    setProgress(mSeekBarProgress).
                    setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            mSeekBarProgress = progress;
                        }

                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {

                        }

                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {

                        }
                    }).
                    onPositive(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mSwipeArea.setValues(mSwipAreaValue);
                            mSwipeArea.refreshSummary();
                            SettingHelper.getInstance(getBaseContext()).putInt(SwipeSetting.SWIPE_AREA_PROGRESS, mSeekBarProgress);
                            dialog.dissmis();
                        }
                    }).
                    onNegative(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dissmis();
                        }
                    }).show();

        } else if (v == mSwipeWhitelist) {
            final SwipeWhitelistDialog dialog = new SwipeWhitelistDialog(this);
            dialog.setTitle(getResources().getString(R.string.swipe_whitelist_title)).
                    onPositive(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dissmis();
                        }
                    }).
                    onNegative(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dissmis();
                        }
                    }).show();
        } else if (v == mRoter5Star) {

        } else if (v == mFeedback) {

        } else if (v == mVersion) {

        }
    }
}
