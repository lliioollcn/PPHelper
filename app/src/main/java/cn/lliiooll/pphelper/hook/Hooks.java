package cn.lliiooll.pphelper.hook;

import android.app.Activity;
import cn.lliiooll.pphelper.utils.PLog;

public class Hooks {

    private static boolean inited = false;
    private static final BaseHook[] hooks = new BaseHook[]{
            SettingHook.INSTANCE,
            RemoveADHook.INSTANCE,
            TestHook.INSTANCE,
    };

    /**
     * 初始化hook
     */
    public static void init() {
        if (inited) return;
        PLog.log("正在初始化hook...");
        for (BaseHook hook : hooks) {
            if (hook.isEnable()) {
                PLog.log("初始化hook: {}@{}", hook.getName(), hook.init());
            } else
                PLog.log("hook被禁用: {}@{}", hook.getName());
        }
        inited = true;
    }
}
