package cn.lliiooll.pphelper.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import cn.lliiooll.pphelper.app.PPHelperImpl;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.util.*;

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
        File file = new File(dir, "obfCaches");
        IOUtils.write(gson.toJson(finds), file);
    }

    public static Map<String, List<String>> cache() {
        Map<String, List<String>> finds = new HashMap<>();
        Application app = AppUtils.getHostAppInstance() == null ? PPHelperImpl.getApp() : AppUtils.getHostAppInstance();
        File dir = app.getExternalFilesDir("pphelperConfig");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "obfCaches");
        if (file.exists()) {
            try {
                String content = IOUtils.read(file);
                finds.putAll(gson.fromJson(content, Map.class));
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
            return gson.fromJson(IOUtils.read(file), Set.class);
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
        IOUtils.write(gson.toJson(hides), file);
    }
}
