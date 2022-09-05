package cn.lliiooll.pphelper.utils;


import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DexKit {

    public static String OBF_COMMENT_VIDEO = "Lcn/xiaochuankeji/zuiyouLite/common/CommentVideo;";

    private static native String find(ClassLoader loader, String input);

    private static Map<String, String> caches = new ConcurrentHashMap<>();

    public static String find(ClassLoader loader, Map<String, Set<String>> input) {
        StringBuilder sb = new StringBuilder();
        input.forEach((k, v) -> {
            List<String> list = new ArrayList<>(v);
            sb.append(k);
            sb.append("\t");
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    sb.append("\t");
                }
                sb.append(list.get(i));
            }
            sb.append("\n");
        });
        return find(loader, sb.toString());
    }

    public static void init() {
        PLog.log("正在加载lib库...");
        System.loadLibrary("dexkit");
        PLog.log("加载成功！！！");
    }

    public static Class<?> load(String key) {
        return caches.containsKey(key) ? Utils.loadClass(caches.get(key)) : null;
    }

    public static void cache(String result) {
        List<String> results = new ArrayList<>(Arrays.asList(result.split("\n")));
        results.forEach(r -> {
            List<String> tokens = new ArrayList<>(Arrays.asList(r.split("\t")));
            if (tokens.size() > 2 && Utils.isNotBlank(tokens.get(0))) {
                String filter = doFilter(tokens.get(0), tokens.subList(1, tokens.size()));
                if (Utils.isNotBlank(filter)) {
                    caches.put(tokens.get(0), filter);
                    PLog.log("找到类: " + tokens.get(0) + " -> " + filter);
                } else {
                    PLog.log("未找到合适的类: \n" + Arrays.toString(tokens.toArray()));
                }
            } else {
                PLog.log("未找到合适的类: \n" + Arrays.toString(tokens.toArray()));
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