package cn.lliiooll.pphelper.hook;

import cn.lliiooll.pphelper.utils.DexKit;
import cn.lliiooll.pphelper.utils.PLog;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Hooks {

    private static boolean inited = false;
    public static boolean sDisableHooks = false;
    private static final BaseHook[] hooks = new BaseHook[]{
            AntiHotfixHook.INSTANCE,
            SettingHook.INSTANCE,
            RemoveADHook.INSTANCE,
            NoMarkHook.INSTANCE,
            CustomVoiceHook.INSTANCE,
            RemoveLiveHook.INSTANCE,
            HidePostHook.INSTANCE,
            RemoveVoiceRoomHook.INSTANCE,
            TestHook.INSTANCE,
            SimpleMeHook.INSTANCE,
            AutoTaskHook.INSTANCE,
            VoiceDownloadHook.INSTANCE,
            ShowHideHook.INSTANCE,
    };

    /**
     * 初始化hook
     */
    public static void init(Object instance) {
        if (inited) return;
        PLog.log("正在初始化hook...");
        if (sDisableHooks) {
            PLog.log("禁用了所有的hook...");
            SettingHook.INSTANCE.init();
        } else {
            for (BaseHook hook : hooks) {
                if (hook.isEnable()) {
                    try {
                        PLog.log("hook {}({}) 是否需要进行反混淆: {}", hook.getLabel(),hook.getName(), hook.needStep());
                        if (hook.needStep()) {
                            PLog.log("为hook进行反混淆操作: {}({})", hook.getLabel(), hook.getName());
                            hook.doStep();
                        }
                        PLog.log("初始化hook: {}({})@{}", hook.getLabel(), hook.getName(), hook.init());
                    } catch (Throwable e) {
                        PLog.log("初始化hook: {}({})@失败", hook.getLabel(), hook.getName());
                        PLog.log(e);
                        Hooks.initFailed(hook, e);
                    }
                } else
                    PLog.log("hook被禁用: {}({})@{}", hook.getLabel(), hook.getName());
            }
        }
        inited = true;
    }

    private static Map<BaseHook, Throwable> failed = new ConcurrentHashMap<>();

    public static void initFailed(BaseHook hook, Throwable e) {
        failed.put(hook, e);
    }
}
