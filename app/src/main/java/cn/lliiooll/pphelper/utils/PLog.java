package cn.lliiooll.pphelper.utils;

import android.util.Log;
import cn.lliiooll.pphelper.BuildConfig;
import de.robv.android.xposed.XposedBridge;

/**
 * 日志输出
 */
public class PLog {
    private final static String TAG = "PPHelper";

    public static void e(String msg) {
        String m = "[DEBUG]>>> " + msg;
        Log.e(TAG, m);
        //XposedBridge.log("[" + TAG + "][ERROR]  " + m);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void d(String msg) {
        if (BuildConfig.DEBUG){
            String m = "[DEBUG]>>> " + msg;
            Log.i(TAG, m);
        }
        //XposedBridge.log("[" + TAG + "]  " + m);
    }

    public static void e(Throwable t) {
        e("发生了一个错误: " + t.getMessage());
        e("详细信息: " + t.getLocalizedMessage());
        t.printStackTrace();
    }

    public static void printStacks() {
        for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
            d("    " + stack.toString());
        }
    }
}
