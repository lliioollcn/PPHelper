package cn.lliiooll.pphelper.utils;

import android.util.Log;
import cn.lliiooll.pphelper.BuildConfig;
import de.robv.android.xposed.XposedBridge;

import java.io.File;

/**
 * 日志输出
 */
public class PLog {
    private final static String TAG = "PPHelper";

    public static void e(String msg) {
        String m = "[DEBUG]>>> " + msg;
        Log.e(TAG, m);
        save(m);
        //XposedBridge.log("[" + TAG + "][ERROR]  " + m);
    }

    private static void save(String m) {
        if (AppUtils.getHostAppInstance() != null){
            File dir = AppUtils.getHostAppInstance().getExternalFilesDir("helperLog");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(dir, "log.txt");
            IOUtils.append(m + "\n", file);
        }

    }

    public static void i(String msg) {
        Log.i(TAG, msg);
        save(msg);
    }

    public static void d(String msg) {
        String m = "[DEBUG]>>> " + msg;
        if (BuildConfig.DEBUG) {
            Log.i(TAG, m);
        }
        save(m);
        //XposedBridge.log("[" + TAG + "]  " + m);
    }

    public static void e(Throwable t) {
        e("发生了一个错误: " + t.getMessage());
        e("详细信息: " + t.getLocalizedMessage());
        t.printStackTrace();
        printStacks();
    }

    public static void printStacks() {
        for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
            d("    " + stack.toString());
        }
    }

    public static boolean isDebug() {
        return BuildConfig.DEBUG;
    }
}
