package cn.lliiooll.pphelper.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import cn.lliiooll.pphelper.app.PPHelperImpl;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置文件管理
 */
public class PConfig {
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
        StringBuilder sb = new StringBuilder();
        finds.forEach((k, v) -> {
            if (sb.length() < 1) {
                sb.append("\n\t");
            }
            sb.append(k).append("\t");
            StringBuilder sb1 = new StringBuilder();
            v.forEach(f -> {
                if (sb1.length() < 1) {
                    sb.append("\n");
                }
                sb.append(f);
            });
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
        File file = new File(dir, "obfCaches");
        if (file.exists()) {
            String content = IOUtils.read(file);
            String[] all = content.split("\n\t");
            for (String f : all) {
                String[] f1 = f.split("\t");
                finds.put(f1[0], Arrays.asList(f1[1].split("\n")));
            }
        }
        return finds;
    }
}
