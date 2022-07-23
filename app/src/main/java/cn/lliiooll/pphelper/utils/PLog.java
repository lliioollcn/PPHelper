package cn.lliiooll.pphelper.utils;

import android.util.Log;
import de.robv.android.xposed.XposedBridge;

public class PLog {
    public static void log(String str, Object... replaces) {
        String s = str;
        for (Object replace : replaces) {
            s = s.replaceFirst("\\{\\}", replace == null ? "null" : replace.toString());
        }
        s = "[PPHelper] >> " + s;
        //XposedBridge.log(s);
        Log.d("PPHelper", s);
    }

    public static void log(Throwable e2) {
        e2.printStackTrace();
    }

    public static void printStacks() {
        for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
            log("    " + stack.toString());
        }
    }
}
