package cn.lliiooll.pphelper.hook;

import cn.lliiooll.pphelper.utils.DexKit;
import cn.lliiooll.pphelper.utils.PLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Hooks {

    private static boolean inited = false;
    public static boolean sDisableHooks = false;
    private static final BaseHook[] hooks = new BaseHook[]{
            SettingHook.INSTANCE,
            RemoveADHook.INSTANCE,
            NoMarkHook.INSTANCE,
            CustomVoiceHook.INSTANCE,
            AriaInitHook.INSTANCE,
            RemoveLiveHook.INSTANCE,
            RemoveVoiceRoomHook.INSTANCE,
            TestHook.INSTANCE,
            AccountHook.INSTANCE,
            ShowHideHook.INSTANCE,
    };

    /**
     * 初始化hook
     */
    public static void init(Object instance) {
        if (inited) return;
        PLog.log("正在初始化hook...");
        PLog.log("正在寻找被混淆的类...");
        Map<String, String[]> result = DexKit.find(instance.getClass().getClassLoader(), new HashMap<String, Set<String>>() {{
            put(DexKit.OBF_COMMENT_VIDEO, new HashSet<String>() {{
                add("event_media_play_observer");
                add("event_on_play_review_comment");
                add("post");
                add("review");
                add("+%d");
                add("http://alfile.ippzone.com/img/mp4/id/");
                add("videocomment");
            }});
            put(DexKit.OBF_ACCOUNT_SERVICE_MANAGER,new HashSet<String>(){{
                add("avatar");
                add("third_force_bind_phone");
            }});
        }});
        PLog.log("查找结果: " + result);
        DexKit.cache(result);
        if (sDisableHooks) {
            PLog.log("禁用了所有的hook...");
            SettingHook.INSTANCE.init();
        } else {
            for (BaseHook hook : hooks) {
                if (hook.isEnable()) {
                    PLog.log("初始化hook: {}({})@{}", hook.getLabel(), hook.getName(), hook.init());
                } else
                    PLog.log("hook被禁用: {}({})@{}", hook.getLabel(), hook.getName());
            }
        }
        inited = true;
    }
}
