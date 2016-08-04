package com.well.swipe.tools;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by mingwei on 4/3/16.
 *
 *
 * 微博：     明伟小学生(http://weibo.com/u/2382477985)
 * Github:   https://github.com/gumingwei
 * CSDN:     http://blog.csdn.net/u013045971
 * QQ&WX：   721881283
 *
 *
 */
public class ClearMemory {

    public static final String TAG = "ClearMemory";

    private volatile static ClearMemory mInstance;

    private ClearMemory() {
    }

    public static ClearMemory getInstance() {
        if (mInstance == null) {
            synchronized (ClearMemory.class) {
                if (mInstance == null) {
                    mInstance = new ClearMemory();
                }
            }
        }
        return mInstance;
    }

    public float cleanMemory(Context context) {
        long beforeCleanMemory = getAvailMemory(context);
        Log.i(TAG, "beforeCleanMemory=" + beforeCleanMemory);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = null;
        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        for (int i = 0; i < runningAppProcessInfoList.size(); ++i) {
            runningAppProcessInfo = runningAppProcessInfoList.get(i);
            String processName = runningAppProcessInfo.processName;
            //调用杀掉进程的方法
            Log.i(TAG, "processName=" + processName);
            killProcessByRestartPackage(context, processName);
        }
        long afterCleanMemory = getAvailMemory(context);
        Log.i(TAG, "afterCleanMemory=" + afterCleanMemory);
        Log.i(TAG, "(afterCleanMemory - beforeCleanMemory)=" + (afterCleanMemory - beforeCleanMemory));
        //editText.setText("共清理:" + (afterCleanMemory - beforeCleanMemory) + "M");
        return (afterCleanMemory - beforeCleanMemory);
    }

    /**
     * 利用activityManager.restartPackage()方法杀死进程
     * 该方法实际调用了activityManager.killBackgroundProcesses()方法
     *
     * @param context
     * @param packageName
     */
    public void killProcessByRestartPackage(Context context, String packageName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.restartPackage(packageName);
        System.gc();
    }


    /**
     * 利用Process.killProcess(pid)杀死进程
     * 注意事项:
     * 1 该方式可自杀,即杀掉本进程
     * 2 该方式可杀掉其他普通应用进程
     * 3 该方式不可杀掉系统级应用即system/app应用
     *
     * @param pid
     */
    public void killProcessBykillProcess(int pid) {
        android.os.Process.killProcess(pid);
    }


    /**
     * 利用adb shell命令杀死进程
     *
     * @param pid
     */
    public void killProcessByAdbShell(int pid) {
        String cmd = "adb shell kill -9 " + pid;
        try {
            java.lang.Process process = Runtime.getRuntime().exec(cmd);
            InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                Log.i(TAG, "----> exec shell:" + line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 利用su进程的命令方式杀死进程
     * 1 得到su进程(super进程)
     * Runtime.getRuntime().exec("su");
     * 2 利用su进程执行命令
     * process.getOutputStream().write(cmd.getBytes());
     *
     * @param pid
     */
    public void killProcessBySu(int pid) {
        try {
            java.lang.Process process = Runtime.getRuntime().exec("su");
            String cmd = "kill -9 " + pid;
            Log.i(TAG, "-------> cmd = " + cmd);
            process.getOutputStream().write(cmd.getBytes());
            if ((process.waitFor() != 0)) {
                Log.i(TAG, "-------> su.waitFor()!= 0");
            } else {
                Log.i(TAG, "------->  su.waitFor()==0 ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //----------> 以上为杀掉进程的几种方式


    /**
     * 获取当前进程名
     *
     * @param context
     * @return
     */
    public String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : activityManager.getRunningAppProcesses()) {
            if (runningAppProcessInfo.pid == pid) {
                String processName = runningAppProcessInfo.processName;
                return processName;
            }
        }
        return null;
    }


    /**
     * 获取栈顶Activity名称
     *
     * @param context
     * @return
     */
    public String getTopActivityName(Context context) {
        String topActivityName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            String topActivityClassName = f.getClassName();
            String temp[] = topActivityClassName.split("\\.");
            topActivityName = temp[temp.length - 1];
        }
        return topActivityName;
    }


    /**
     * 获取栈顶Activity所属进程的名称
     *
     * @param context
     * @return
     */
    public String getTopActivityProcessName(Context context) {
        String processName = null;
        ActivityManager activityManager = (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName componentName = runningTaskInfos.get(0).topActivity;
            String topActivityClassName = componentName.getClassName();
            int index = topActivityClassName.lastIndexOf(".");
            processName = topActivityClassName.substring(0, index);
        }
        return processName;
    }


    /**
     * 获取内存总大小
     *
     * @return
     */
    public long getTotalMemory() {
        // 系统的内存信息文件
        String filePath = "/proc/meminfo";
        String lineString;
        String[] stringArray;
        long totalMemory = 0;
        try {
            FileReader fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader, 1024 * 8);
            // 读取meminfo第一行,获取系统总内存大小
            lineString = bufferedReader.readLine();
            // 按照空格拆分
            stringArray = lineString.split("\\s+");
            // 获得系统总内存,单位KB
            totalMemory = Integer.valueOf(stringArray[1]).intValue();
            bufferedReader.close();
        } catch (IOException e) {
        }
        return totalMemory / 1024;
    }


    /**
     * 获取可用内存大小
     *
     * @param context
     * @return
     */
    public long getAvailMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo.availMem / (1024 * 1024);
    }
}
