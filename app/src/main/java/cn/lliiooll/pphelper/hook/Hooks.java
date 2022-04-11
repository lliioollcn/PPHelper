package cn.lliiooll.pphelper.hook;

import cn.lliiooll.pphelper.utils.PLog;
import cn.xiaochuankeji.zuiyouLite.R$id;

public class Hooks {

    private static boolean inited = false;
    public static boolean sDisableHooks = false;
    private static final BaseHook[] hooks = new BaseHook[]{
            SettingHook.INSTANCE,
            RemoveADHook.INSTANCE,
            NoMarkHook.INSTANCE,
            TestHook.INSTANCE,
    };

    /**
     * 初始化hook
     */
    public static void init() {
        if (inited) return;
        PLog.log("正在初始化hook...");
        if (sDisableHooks) {
            PLog.log("禁用了所有的hook...");
            SettingHook.INSTANCE.init();
        } else {
            for (BaseHook hook : hooks) {
                if (hook.isEnable()) {
                    PLog.log("初始化hook: {}@{}", hook.getName(), hook.init());
                } else
                    PLog.log("hook被禁用: {}@{}", hook.getName());
            }
        }
        inited = true;
    }
}
