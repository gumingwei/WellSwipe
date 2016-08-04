package com.well.swipe.tools;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.widget.Toast;

import com.well.swipe.R;

/**
 * Created by mingwei on 3/27/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class FlashLight extends SwipeTools {

    private static FlashLight mInstance;

    private static Camera mCamera = null;

    private Parameters mCameraParameters;

    private static String previousFlashMode = null;

    private static boolean isOpen = false;

    private FlashLight() {
    }

    public static FlashLight getInstance() {
        if (mInstance == null) {
            synchronized (FlashLight.class) {
                if (mInstance == null) {
                    mInstance = new FlashLight();
                }
            }
        }
        return mInstance;
    }

    public synchronized void open(Context context) {
        //这里的判断证实是没有用的，不能用来判断手机是否有灯泡，已经加了我就再没删
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewTexture(new SurfaceTexture(0));//这一句话很重要，不加的话在nexus 5上灯泡不亮
            } catch (Exception e) {
                close();
                Toast.makeText(context.getApplicationContext(), "null",
                        Toast.LENGTH_LONG).show();

                //这里是打不开的情况，比如别人正在使用的灯泡，打不开用一个close关一次，下次就能打开了。
            }
        } else {
            Toast.makeText(context, "null", Toast.LENGTH_LONG).show();
            return;
        }
        if (mCamera != null) {
            mCameraParameters = mCamera.getParameters();
            previousFlashMode = mCameraParameters.getFlashMode();
        }
        if (previousFlashMode == null) {
            // could be null if no flash, i.e. emulator
            previousFlashMode = Camera.Parameters.FLASH_MODE_OFF;
        }
    }

    public synchronized void close() {//关灯，就是用完之后清除一下camera对象，不然会影响其他设备的正常使用
        if (mCamera != null) {
            mCameraParameters.setFlashMode(previousFlashMode);
            mCamera.setParameters(mCameraParameters);
            mCamera.release();
            mCamera = null;
            isOpen = false;
        }
    }

    public synchronized void onAndOff(Context context) {//开\关都在这里调

        try {
            if (isOpen) {
                off();
            } else if (!isOpen) {
                on(context);
            }
            // send broadcast for widget
            //调完之后可以通知界面更新
        } catch (RuntimeException e) {
            Toast.makeText(context, "null", Toast.LENGTH_SHORT).show();
        }

    }

    public synchronized void on(Context context) {
        if (mCamera == null) {
            open(context);
        }
        if (mCamera != null) {
            isOpen = true;
            mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            mCamera.setParameters(mCameraParameters);
            mCamera.startPreview();

        }

    }

    public synchronized void off() {
        if (mCamera != null) {
            isOpen = false;
            mCamera.stopPreview();
            mCameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(mCameraParameters);

        }
        close();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean flag) {
        isOpen = flag;
    }

    @Override
    public BitmapDrawable getDrawableState(Context context) {
        if (isOpen()) {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_flashlight_on);
        } else {
            return (BitmapDrawable) context.getResources().getDrawable(R.drawable.ic_flashlight_off);
        }
    }

    @Override
    public String getTitleState(Context context) {
        return null;
    }
}
