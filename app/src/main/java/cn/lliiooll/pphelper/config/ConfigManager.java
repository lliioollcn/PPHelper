package cn.lliiooll.pphelper.config;

import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.hook.SettingHook;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.utils.Utils;
import com.tencent.mmkv.MMKV;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ConfigManager {
    private static boolean inited = false;
    public static final String MMKV_MODULES = "helper_modules";
    public static final String MMKV_CACHES = "helper_caches";
    private static MMKV mmkv;
    private static MMKV mmkvCache;

    /**
     * 初始化配置文件加载器
     */
    public static void init() {
        if (inited) return;
        PLog.log("正在初始化配置文件...");
        File mmkvDir = new File(Utils.getApplication().getFilesDir(), "pp_mmkv");
        if (mmkvDir.isFile()) {
            mmkvDir.delete();
        }
        if (!mmkvDir.exists()) {
            mmkvDir.mkdirs();
        }
        // MMKV requires a ".tmp" cache directory, we have to create it manually
        File cacheDir = new File(mmkvDir, ".tmp");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        MMKV.initialize(Utils.getApplication(), mmkvDir.getAbsolutePath(), s -> {

        });
        ConfigManager.mmkv = MMKV.mmkvWithID(ConfigManager.MMKV_MODULES, MMKV.MULTI_PROCESS_MODE);
        ConfigManager.mmkvCache = MMKV.mmkvWithID(ConfigManager.MMKV_CACHES, MMKV.MULTI_PROCESS_MODE);
        inited = true;
    }

    /**
     * 设置模块状态
     *
     * @param baseHook 要设置的hook
     * @param enable   启用(true)/禁用(false)
     */
    public static void setEnable(BaseHook baseHook, boolean enable) {
        setEnable(baseHook.getLabel(), enable);
    }

    /**
     * 设置模块状态
     *
     * @param hookName 要设置的hook名称
     * @param enable   启用(true)/禁用(false)
     */
    public static void setEnable(String hookName, boolean enable) {
        if (!inited || Objects.isNull(mmkv)) return;
        if (hookName.equalsIgnoreCase(SettingHook.INSTANCE.getLabel())) enable = true;
        mmkv.encode(hookName, enable);

    }

    /**
     * @param baseHook 要获取的hook
     * @return hook启用(true)/禁用(false)
     */
    public static boolean isEnable(BaseHook baseHook) {
        return isEnable(baseHook.getLabel());
    }

    /**
     * @param baseHook 要获取的hook
     * @return hook启用(true)/禁用(false)
     */
    public static boolean isEnable(String baseHook) {
        return isEnable(baseHook, true);
    }

    /**
     * @param baseHook 要获取的hook
     * @return hook启用(true)/禁用(false)
     */
    public static boolean isEnable(String baseHook, boolean def) {
        if (!inited || Objects.isNull(mmkv)) return false;
        if (baseHook.equalsIgnoreCase(SettingHook.INSTANCE.getLabel())) return true;
        return mmkv.decodeBool(baseHook, def);
    }

    public static int getInt(String key, int i) {
        if (!inited || Objects.isNull(mmkv)) return i;
        return mmkv.decodeInt(key, i);
    }

    public static void setInt(String key, int i) {
        if (!inited || Objects.isNull(mmkv)) return;
        mmkv.encode(key, i);
    }

    public static boolean isFirst(@NotNull BaseHook hook) {
        if (!inited || Objects.isNull(mmkv)) return false;
        return mmkv.decodeBool(hook.getLabel() + "_first", true);
    }

    public static void setFirst(@NotNull BaseHook hook) {
        if (!inited || Objects.isNull(mmkv)) return;
        mmkv.encode(hook.getLabel() + "_first", false);
    }

    public static Set<String> getSet(String label) {
        if (!inited || Objects.isNull(mmkv)) return null;
        return mmkv.decodeStringSet(label, new HashSet<>());
    }

    public static void setSet(String label, Set<String> set) {
        if (!inited || Objects.isNull(mmkv)) return;
        mmkv.encode(label, set);
    }

    public static void cache(String token, String filter) {
        if (!inited || Objects.isNull(mmkvCache)) return;
        mmkvCache.encode(token, filter);
    }

    public static boolean hasCache(String key) {
        if (!inited || Objects.isNull(mmkvCache)) return false;
        boolean has = mmkvCache.decodeString(key, null) != null;
        PLog.log("缓存 {} 是否存在: {}",key,has);
        return has;
    }

    public static String cache(String key) {
        if (!inited || Objects.isNull(mmkvCache) || !hasCache(key)) return "";
        return mmkvCache.decodeString(key);
    }
}
