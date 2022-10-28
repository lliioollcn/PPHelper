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
            RemoveGameHook.INSTANCE,
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
            put(DexKit.OBF_ACCOUNT_SERVICE_MANAGER, new HashSet<String>() {{
                add("avatar");
                add("third_force_bind_phone");
            }});
            put(DexKit.OBF_HOTFIX_INIT, new HashSet<String>() {{
                add("event_on_load_hot_config_success");
                add("app_config_json_parse");
                add("local config cold/get json data parse failed.");
            }});
            put(DexKit.OBF_PUBLISH_BUS, new HashSet<String>() {{
                add("仅可同时进行3个图片视频或语音发布任务");
            }});
            put(DexKit.OBF_PUBLISH_DATA, new HashSet<String>() {{
                add("距你-18cm");
            }});
        }});

        result.forEach((key, value) -> {
            PLog.log("========================================");
            PLog.log("查找结果");
            PLog.log(key);
            PLog.log(Arrays.toString(value));
            PLog.log("========================================");
        });

        DexKit.cache(result);
        if (sDisableHooks) {
            PLog.log("禁用了所有的hook...");
            SettingHook.INSTANCE.init();
        } else {
            for (BaseHook hook : hooks) {
                if (hook.isEnable()) {
                    try {
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
