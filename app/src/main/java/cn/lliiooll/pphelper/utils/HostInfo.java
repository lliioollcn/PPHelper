package cn.lliiooll.pphelper.utils;

import cn.lliiooll.pphelper.hook.Hooks;
import cn.xiaochuankeji.hermes.BuildConfig;

public class HostInfo {
    public static void init() {
        if (!isSupport()) {
            Hooks.sDisableHooks = true;
        }
    }

    public static boolean isSupport() {
        if (BuildConfig.VERSION_CODE < 207029) {
            PLog.log("不支持的版本: {}", BuildConfig.VERSION_NAME);
            return false;
        }
        return true;
    }
}
