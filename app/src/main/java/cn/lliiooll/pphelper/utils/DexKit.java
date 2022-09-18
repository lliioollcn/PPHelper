package cn.lliiooll.pphelper.utils;


import android.widget.Toast;
import me.teble.xposed.autodaily.dexkit.DexKitHelper;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DexKit {

    public static String OBF_COMMENT_VIDEO = "Lcn/xiaochuankeji/zuiyouLite/common/CommentVideo;";
    public static String OBF_ACCOUNT_SERVICE_MANAGER = "Lcn/xiaochuankeji/zuiyouLite/api/account/AccountServiceManager";

    private static Map<String, String> caches = new ConcurrentHashMap<>();

    public static Map<String, String[]> find(ClassLoader loader, Map<String, Set<String>> input) {
        DexKitHelper helper = new DexKitHelper(loader);
        Map<String, String[]> results = helper.batchFindClassesUsedStrings(input, false, new int[0]);
        helper.release();
        return results;
    }

    public static void init() {
        PLog.log("正在加载lib库...");
        System.loadLibrary("pp_native");
        PLog.log("加载成功！！！");
    }

    public static Class<?> load(String key) {
        return caches.containsKey(key) ? Utils.loadClass(caches.get(key)) : null;
    }

    public static void cache(Map<String, String[]> result) {
        result.forEach((token, finds) -> {
            String filter = doFilter(token, Arrays.asList(finds));
            if (Utils.isNotBlank(filter)) {
                caches.put(token, filter);
                PLog.log("找到类: " + token + " -> " + filter);
            } else {
                PLog.log("未找到合适的类(NoFilter): " + Arrays.toString(finds));
            }
        });
    }

    public static String doFilter(String key, List<String> classes) {
        if (!classes.isEmpty() && Utils.isNotBlank(key)) {
            if (classes.size() == 1) {
                return doReplace(classes.get(0));
            }
            if (key.equalsIgnoreCase(OBF_COMMENT_VIDEO)) {
                Class<?> commentBeanCls = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.data.CommentBean");
                for (String clazz : classes) {
                    String replace = doReplace(clazz);
                    PLog.log("正在过滤类: " + replace);
                    Class<?> cls = Utils.loadClass(replace);
                    if (cls != null) {
                        for (Method m : cls.getDeclaredMethods()) {
                            if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == commentBeanCls) {
                                PLog.log("过滤完毕: " + replace);
                                return replace;
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    public static String doReplace(String clazz) {
        String cl = clazz;
        if (cl.startsWith("L")) {
            cl = cl.replaceFirst("L", "");
        }
        if (cl.endsWith(";")) {
            cl = cl.substring(0, cl.length() - 1);
        }
        if (cl.contains("\\")) {
            cl = cl.replace("\\", ".");
        }
        if (cl.contains("/")) {
            cl = cl.replace("/", ".");
        }
        return cl;
    }
}