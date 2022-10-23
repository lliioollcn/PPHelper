package cn.lliiooll.pphelper.utils;

import android.util.Log;
import cn.lliiooll.pphelper.BuildConfig;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

public class PLog {
    public static void log(String str, Object... replaces) {
        if (BuildConfig.DEBUG) {
            String s = str;
            for (Object replace : replaces) {
                s = s.replaceFirst("\\{\\}", replace == null ? "null" : replace.toString());
            }
            int limit = 500;
            if (s.length() >= limit) {
                String q = s;
                int c = 0;
                for (int i = 0; i < s.length(); i += limit) {

                    if (i != 0 && q.length() > limit) {
                        q = q.substring(limit);
                    }
                    Log.d("PPHelper", "[PPHelper] @" + c + " >> " + q);
                    c++;
                }
            } else {
                s = "[PPHelper] >> " + s;
                //XposedBridge.log(s);
                Log.d("PPHelper", s);
            }
        }
    }

    public static void log(Throwable e2) {
        log("出现一个错误: " + e2.getClass().getName() + " @ " + e2.getMessage());
        for (StackTraceElement element : e2.getStackTrace()) {
            log("在堆栈 " + element);
        }
        if (e2.getCause() != null) {
            log(e2.getCause());
        }

    }

    public static void printStacks() {
        for (StackTraceElement stack : Thread.currentThread().getStackTrace()) {
            log("    " + stack.toString());
        }
    }

    public static void log(Class<?> clazz) {
        try {
            log(clazz, null);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public static void log(Class<?> clazz, Object ins) throws IllegalAccessException {
        if (clazz == null) {
            log("clazz为null");
            return;
        }
        log("======================================================");
        for (Method m : clazz.getDeclaredMethods()) {
            log("在类 {} 中找到方法: {}, {}@({})", clazz.getName(), m.getName(), m.getParameterTypes().length, Arrays.toString(m.getParameterTypes()));
        }
        for (Method m : clazz.getMethods()) {
            log("在类 {} 中找到方法: {}, {}@({})", clazz.getName(), m.getName(), m.getParameterTypes().length, Arrays.toString(m.getParameterTypes()));
        }
        for (Field f : clazz.getDeclaredFields()) {
            log("在类 {} 中找到变量: {}@({})", clazz.getName(), f.getName(), f.getType());
            if (!Modifier.isStatic(f.getModifiers()) && ins != null) {
                f.setAccessible(true);
                Object result = f.get(ins);
                log("在类 {} 中找到的变量 {} 的值为: {}", clazz.getName(), f.getName(), result == null ? "null" : result);
            }
        }
        for (Field f : clazz.getFields()) {
            log("在类 {} 中找到变量: {}@({})", clazz.getName(), f.getName(), f.getType());
            if (!Modifier.isStatic(f.getModifiers()) && ins != null) {
                f.setAccessible(true);
                Object result = f.get(ins);
                log("在类 {} 中找到的变量 {} 的值为: {}", clazz.getName(), f.getName(), result == null ? "null" : result);
            }
        }
        log("======================================================");
    }
}
