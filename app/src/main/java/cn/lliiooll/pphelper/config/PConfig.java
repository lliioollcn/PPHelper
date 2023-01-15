package cn.lliiooll.pphelper.config;

import android.app.Application;
import cn.lliiooll.pphelper.app.PPHelperImpl;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.IOUtils;
import cn.lliiooll.pphelper.utils.PLog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 配置文件管理
 */
public class PConfig {

    private static final Gson gson = new GsonBuilder().serializeNulls().create();
    private static MMKV mmkv = null;

    public static boolean isEnable(String label, boolean def) {
        if (mmkv == null) return def;
        return mmkv.decodeBool(label, def);
    }

    public static void setEnable(String label, boolean enable) {
        if (mmkv == null) return;
        mmkv.encode(label, enable);
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
        if (mmkv == null)
            return new HashSet<>();

        return mmkv.decodeStringSet(label, new HashSet<>());
    }

    public static void setSet(String label, Set<String> hides) {
        if (mmkv == null) return;
        mmkv.encode(label, hides);
    }

    public static void init() {
        mmkv = MMKV.defaultMMKV();
        PLog.d("mmkv加载成功!");
    }

    public static void setNumber(String label, int def) {
        mmkv.encode(label, def);
    }

    public static int number(String label, int def) {
        return mmkv.decodeInt(label, def);
    }

    public static void setStr(String voicePath, String toString) {
        mmkv.encode(voicePath, toString);
    }

    public static String str(String voicePath, String def) {
        return mmkv.decodeString(voicePath,def);
    }
}
