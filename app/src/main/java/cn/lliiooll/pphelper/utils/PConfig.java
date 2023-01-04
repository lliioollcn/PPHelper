package cn.lliiooll.pphelper.utils;

import android.app.Application;
import cn.lliiooll.pphelper.app.PPHelperImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配置文件管理
 */
public class PConfig {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();

    public static boolean isEnable(String label, boolean def) {
        Application app = AppUtils.getHostAppInstance() == null ? PPHelperImpl.getApp() : AppUtils.getHostAppInstance();
        File dir = app.getExternalFilesDir("pphelperConfig");
        if (!dir.exists()) {
            dir.mkdirs();
            return def;
        }
        File file = new File(dir, label);
        if (!file.exists()) {
            IOUtils.write(def + "", file);
            return def;
        }
        String content = IOUtils.read(file);
        PLog.d("文件内容: " + content);
        return content.equalsIgnoreCase("true");
    }

    public static void setEnable(String label, boolean enable) {
        Application app = AppUtils.getHostAppInstance() == null ? PPHelperImpl.getApp() : AppUtils.getHostAppInstance();
        File dir = app.getExternalFilesDir("pphelperConfig");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, label);
        IOUtils.write(enable + "", file);
    }

    public static void cache(Map<String, List<String>> finds) {
        Application app = AppUtils.getHostAppInstance() == null ? PPHelperImpl.getApp() : AppUtils.getHostAppInstance();
        File dir = app.getExternalFilesDir("pphelperConfig");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "obfCachesNew");
        StringBuilder sb = new StringBuilder();
        AtomicInteger c = new AtomicInteger();
        finds.forEach((k, v) -> {
            if (c.get() != 0) {
                sb.append("\n");
            }
            sb.append(k).append("###");
            AtomicInteger q = new AtomicInteger();
            v.forEach(d -> {
                if (q.get() != 0) {
                    sb.append(" ");
                }
                sb.append(d);
                q.getAndIncrement();
            });
            c.getAndIncrement();
        });
        IOUtils.write(sb.toString(), file);
    }

    public static Map<String, List<String>> cache() {
        Map<String, List<String>> finds = new HashMap<>();
        Application app = AppUtils.getHostAppInstance() == null ? PPHelperImpl.getApp() : AppUtils.getHostAppInstance();
        File dir = app.getExternalFilesDir("pphelperConfig");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "obfCachesNew");
        if (file.exists()) {
            try {
                String content = IOUtils.read(file);
                String[] keys = content.split("\n");
                for (String k : keys) {
                    String[] data = k.split("###");
                    finds.put(data[0], new ArrayList<>(Arrays.asList(data[1].split(" "))));
                }
            } catch (Throwable e) {
                file.delete();
            }
        }
        return finds;
    }

    public static Set<String> getSet(String label) {
        Application app = AppUtils.getHostAppInstance() == null ? PPHelperImpl.getApp() : AppUtils.getHostAppInstance();
        File dir = app.getExternalFilesDir("pphelperConfig");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, label);
        if (file.exists()) {
            String sb = IOUtils.read(file);
            Set<String> set = new HashSet<>();
            for (String m : sb.split("\n")) {
                set.add(m);
            }
            return set;
        }
        return new HashSet<>();
    }

    public static void setSet(String label, Set<String> hides) {
        Application app = AppUtils.getHostAppInstance() == null ? PPHelperImpl.getApp() : AppUtils.getHostAppInstance();
        File dir = app.getExternalFilesDir("pphelperConfig");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, label);
        StringBuilder sb = new StringBuilder();
        final int[] c = {0};
        hides.forEach(v -> {
            if (c[0] != 0) {
                sb.append("\n");
            }
            sb.append(v);
            c[0]++;
        });
        IOUtils.write(sb.toString(), file);
    }
}
