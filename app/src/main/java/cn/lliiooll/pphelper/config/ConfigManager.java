package cn.lliiooll.pphelper.config;

import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.utils.Utils;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.util.Objects;

public class ConfigManager {
    private static boolean inited = false;
    public static final String MMKV_MODULES = "helper_modules";
    private static MMKV mmkv;

    /**
     * 初始化配置文件加载器
     */
    public static void init() {
        if (inited) return;
        PLog.log("正在初始化配置文件...");
        File mmkvDir = new File(Utils.getApplication().getFilesDir(), "pp_mmkv");
        if (!mmkvDir.exists()) {
            mmkvDir.mkdirs();
        }
        // MMKV requires a ".tmp" cache directory, we have to create it manually
        File cacheDir = new File(mmkvDir, ".tmp");
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        MMKV.initialize(mmkvDir.getAbsolutePath());
        ConfigManager.mmkv = MMKV.mmkvWithID(ConfigManager.MMKV_MODULES, MMKV.MULTI_PROCESS_MODE);
        inited = true;
    }

    /**
     * 设置模块状态
     *
     * @param baseHook 要设置的hook
     * @param enable   启用(true)/禁用(false)
     */
    public static void setEnable(BaseHook baseHook, boolean enable) {
        if (!inited) return;
        if (Objects.isNull(mmkv)) return;
        mmkv.encode(baseHook.getName(), enable);

    }

    /**
     * @param baseHook 要获取的hook
     * @return hook启用(true)/禁用(false)
     */
    public static boolean isEnable(BaseHook baseHook) {
        if (!inited) return true;
        return mmkv.decodeBool(baseHook.getName(), true);
    }
}
