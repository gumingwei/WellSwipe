package com.well.swipe.tools;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/27/16.
 */
public class SwipeAudio {


    private volatile static SwipeAudio mInstance;

    private AudioManager mAudioManager;

    int mRingerVolume;

    private SwipeAudio(Context context) {
        mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mRingerVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_SYSTEM);
    }

    public static SwipeAudio getInstance(Context context) {
        if (mInstance == null) {
            synchronized (ToolsStrategy.class) {
                if (mInstance == null) {
                    mInstance = new SwipeAudio(context);
                }
            }
        }
        return mInstance;
    }

    public void changeState() {
        switch (getState()) {
            case AudioManager.RINGER_MODE_SILENT:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
        }
    }

    public int getState() {
        return mAudioManager.getRingerMode();
    }

    public BitmapDrawable getDrawableState(Context context) {
        switch (getState()) {
            case AudioManager.RINGER_MODE_VIBRATE:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_selfile);
            case AudioManager.RINGER_MODE_SILENT:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_ringer_silent);
            case AudioManager.RINGER_MODE_NORMAL:
                return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_ringer_normal);
        }
        return null;
    }

    public String getTitleState(Context context) {
        switch (getState()) {
            case AudioManager.RINGER_MODE_VIBRATE:
                return context.getResources().getString(R.string.scene_mode_0);
            case AudioManager.RINGER_MODE_SILENT:
                return context.getResources().getString(R.string.scene_mode_1);
            case AudioManager.RINGER_MODE_NORMAL:
                return context.getResources().getString(R.string.scene_mode_2);
        }
        return "";
    }
}
