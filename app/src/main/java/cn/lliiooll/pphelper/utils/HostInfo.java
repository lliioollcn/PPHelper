package cn.lliiooll.pphelper.utils;

import cn.lliiooll.pphelper.hook.Hooks;
import cn.lliiooll.pphelper.startup.HookEntry;
import cn.lliiooll.pphelper.startup.StartupHook;

import static cn.lliiooll.pphelper.startup.HookEntry.PACKAGE_ZUIYOU_LITE;

public class HostInfo {
    public static void init() {
        if (!isSupport()) {
            Hooks.sDisableHooks = true;
        }
    }

    public static boolean isSupport() {
        /*
        if (BuildConfig.VERSION_CODE < 207029) {
            PLog.log("不支持的版本: {}", BuildConfig.VERSION_NAME);
            return false;
        }

         */
        return true;
    }

    public static boolean isInHostProcess() {
        return Utils.getApplication().getPackageName().equals(PACKAGE_ZUIYOU_LITE);
    }

    public static class Version {

        public static final int PP_2_36_0 = 2360000;
        public static final int PP_2_36_1 = 2360100;
        public static final int PP_2_37_0 = 2370000;

    }
}
