package cn.lliiooll.pphelper.hook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.*;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Hook管理器
 */
public class HookBus {

    private static final BaseHook[] hooks = {
            RemoveEvilInstrumentationHook.INSTANCE,
            RemoveZyBuffHook.INSTANCE,
            AddSetting.INSTANCE,
            TestHook.INSTANCE,
            AntiAD.INSTANCE,
            NoMark.INSTANCE,
            AntiVoiceRoomHook.INSTANCE,
            SimpleMe.INSTANCE,
            RemovePost.INSTANCE,
            CustomData.INSTANCE,
            PPLog.INSTANCE,
            //CustomVideoRecord.INSTANCE,
    };
    public static boolean inited = false;

    private static int obfs = 0;

    /**
     * 初始化hook管理器
     */
    public static void init() {

        for (BaseHook hook : hooks) {
            if (hook.isEnable()) {
                if (hook.needObf()) {
                    obfs += hook.obf().size();
                } else {
                    PLog.d("尝试加载不需要反混淆的hook " + hook.getName() + "(" + hook.getLabel() + ") ");
                    try {
                        hook.init();
                    } catch (Throwable e) {
                        PLog.e(e);
                    }

                }

            }
        }

        /*
        XposedHelpers.findAndHookMethod("cn.xiaochuankeji.zuiyouLite.ui.splash.SplashActivity", HybridClassLoader.clLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

            }
        });

         */

        XposedHelpers.findAndHookMethod("cn.xiaochuankeji.zuiyouLite.ui.splash.SplashActivity", HybridClassLoader.clLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                PLog.d("广告界面开始加载...");
                Activity activity = (Activity) param.thisObject;
                PLog.d("设置界面内容");

                //RelativeLayout root = (RelativeLayout) activity.findViewById(AppUtils.findId("id", "splash_open_cover")).getParent();
                View custom = LayoutInflater.from(activity).inflate(R.layout.pp_spalsh, null);
                activity.setContentView(custom);
                //root.addView(custom);
                TextView text = custom.findViewById(R.id.spalsh_text);
                ProgressBar bar = custom.findViewById(R.id.spalsh_bar);
                bar.setMax(obfs);
                final int[] obf = {0};
                PLog.d("开始加载");
                SyncUtils.async(() -> {
                    for (BaseHook hook : hooks) {
                        if (hook.isEnable()) {
                            if (hook.needObf()) {
                                SyncUtils.sync(() -> text.setText("正在加载hook: " + hook.getName()));
                                PLog.d("尝试加载需要反混淆的hook " + hook.getName() + "(" + hook.getLabel() + ") ");
                                Map<String, List<String>> finded = new HashMap<>();
                                hook.obf().forEach((k, i) -> {
                                    SyncUtils.sync(() -> text.setText("正在寻找被混淆的类: " + k));
                                    finded.putAll(DexUtils.obf(new HashMap<String, List<String>>() {{
                                        put(k, i);
                                    }}));
                                    obf[0]++;
                                    SyncUtils.sync(() -> bar.setProgress(obf[0]));
                                });
                                try {
                                    hook.doObf(finded);
                                    hook.init();
                                } catch (Throwable e) {
                                    PLog.e(e);
                                }
                            }
                        }
                    }
                    SyncUtils.sync(() -> {
                        PLog.d("加载完毕.");
                        text.setText("加载完毕~");
                        bar.setMax(1);
                        bar.setProgress(1, true);
                        inited = true;
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException e) {
                            PLog.e(e);
                        }
                        for (Field f : activity.getClass().getDeclaredFields()) {
                            if (f.getType() == Handler.class) {
                                PLog.d("尝试启动主界面");
                               Handler handler = (Handler) XposedHelpers.getObjectField(activity, f.getName());
                               handler.sendEmptyMessage(29);
                            }
                        }
                    });
                });
            }
        });

    }

    public static ArrayList<BaseHook> getAllHooks() {
        return new ArrayList<>(Arrays.asList(hooks));
    }
}
