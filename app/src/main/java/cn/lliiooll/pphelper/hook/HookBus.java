package cn.lliiooll.pphelper.hook;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.config.PConfig;
import cn.lliiooll.pphelper.hook.ppx.PPXAddSettingHook;
import cn.lliiooll.pphelper.hook.ppx.PPXTestHook;
import cn.lliiooll.pphelper.hook.xiaochuankeji.AntiAD;
import cn.lliiooll.pphelper.hook.xiaochuankeji.RemoveEvilInstrumentationHook;
import cn.lliiooll.pphelper.hook.xiaochuankeji.RemoveZyBuffHook;
import cn.lliiooll.pphelper.hook.zuiyou.ZYAddSettingHook;
import cn.lliiooll.pphelper.hook.zuiyou.ZYDecodeHook;
import cn.lliiooll.pphelper.hook.zuiyouLite.*;
import cn.lliiooll.pphelper.startup.HookEntry;
import cn.lliiooll.pphelper.utils.*;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Hook管理器
 */
public class HookBus {

    private static final BaseHook[] zuiyouLiteHooks = {
            RemoveEvilInstrumentationHook.INSTANCE,
            RemoveZyBuffHook.INSTANCE,
            AddSetting.INSTANCE,
            TestHook.INSTANCE,
            AntiAD.INSTANCE,
            NoMark.INSTANCE,
            AntiVoiceRoomHook.INSTANCE,
            CustomData.INSTANCE,
            AudioDownloadHook.INSTANCE,
            AudioSend.INSTANCE,
            CustomDownloadHook.INSTANCE,
            SimpleMe.INSTANCE,
            RemovePost.INSTANCE,
            PPLog.INSTANCE,
            //CustomVideoRecord.INSTANCE,
    };
    private static final BaseHook[] zuiyouHooks = {
            cn.lliiooll.pphelper.hook.zuiyou.TestHook.INSTANCE,
            AntiAD.INSTANCE,
            ZYDecodeHook.INSTANCE,
            RemoveEvilInstrumentationHook.INSTANCE,
            RemoveZyBuffHook.INSTANCE,
            ZYAddSettingHook.INSTANCE,
    };
    private static final BaseHook[] ppxHooks = {
            PPXTestHook.INSTANCE,
            PPXAddSettingHook.INSTANCE,
    };
    public static boolean inited = true;

    private static int obfs = 0;


    /**
     * 初始化hook管理器
     */
    public static void initZuiyouLite() {
        inited = false;
        for (BaseHook hook : zuiyouLiteHooks) {
            if (hook.isEnable()) {
                if (hook.needObf()) {
                    obfs += hook.obf().size();
                } else {
                    if (!hook.obf().isEmpty()) {
                        PLog.d("尝试加载已缓存反混淆的hook " + hook.getName() + "(" + hook.getLabel() + ") ");
                        hook.doObf(PConfig.cache());
                    } else {
                        PLog.d("尝试加载不需要反混淆的hook " + hook.getName() + "(" + hook.getLabel() + ") ");
                    }

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
                if (obfs < 1) {
                    inited = true;
                    for (Field f : activity.getClass().getDeclaredFields()) {
                        if (f.getType() == Handler.class) {
                            PLog.d("尝试启动主界面");
                            Handler handler = (Handler) XposedHelpers.getObjectField(activity, f.getName());
                            handler.sendEmptyMessage(29);
                        }
                    }
                } else {
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
                        for (BaseHook hook : zuiyouLiteHooks) {
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
            }
        });

    }

    public static List<BaseHook> getAllHooks() {
        List<BaseHook> hooks = new ArrayList<>();
        if (HookEntry.getPackageName().equalsIgnoreCase(HostInfo.ZuiyouLite.PACKAGE_NAME)) {
            hooks.addAll(Arrays.asList(zuiyouLiteHooks));
        } else if (HookEntry.getPackageName().equalsIgnoreCase(HostInfo.TieBa.PACKAGE_NAME)) {
            hooks.addAll(Arrays.asList(zuiyouHooks));
        } else if (HookEntry.getPackageName().equalsIgnoreCase(HostInfo.PPX.PACKAGE_NAME)) {
            hooks.addAll(Arrays.asList(ppxHooks));
        }
        return hooks;
    }

    public static void init(XC_LoadPackage.LoadPackageParam param) {
        if (param.packageName.equalsIgnoreCase(HostInfo.ZuiyouLite.PACKAGE_NAME)) {
            initZuiyouLite();
        } else if (param.packageName.equalsIgnoreCase(HostInfo.TieBa.PACKAGE_NAME)) {
            initZuiyou();
        } else if (param.packageName.equalsIgnoreCase(HostInfo.PPX.PACKAGE_NAME)) {
            initPPX();
        }
        if (AppUtils.isUpdate()) {
            AppUtils.update();
        }

    }

    public static void initPPX() {
        PLog.d("开始为皮皮虾加载模块...");
        for (BaseHook hook : ppxHooks) {
            if (hook.isEnable()) {
                PLog.d("尝试加载hook: " + hook.getName());
                if (hook.needObf()) {
                    hook.doObf(DexUtils.obf(hook.obf()));
                } else if (!hook.obf().isEmpty()) {
                    hook.doObf(PConfig.cache());
                }
                try {
                    hook.init();
                } catch (Throwable e) {
                    PLog.e(e);
                }
            }
        }
    }

    public static void initZuiyou() {
        inited = false;
        PLog.d("开始为最右加载模块...");
        for (BaseHook hook : zuiyouHooks) {
            if (hook.isEnable()) {
                PLog.d("尝试加载hook: " + hook.getName());
                if (hook.needObf()) {
                    hook.doObf(DexUtils.obf(hook.obf()));
                } else if (!hook.obf().isEmpty()) {
                    hook.doObf(PConfig.cache());
                }
                try {
                    hook.init();
                } catch (Throwable e) {
                    PLog.e(e);
                }
            }
        }
        XposedHelpers.findAndHookMethod("cn.xiaochuankeji.tieba.ui.base.SplashActivity", HybridClassLoader.clLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                PLog.d("广告界面开始加载...");
                Activity activity = (Activity) param.thisObject;
                Class<?> clazz = activity.getClass();
                for (Method m : clazz.getDeclaredMethods()) {
                    if (m.getReturnType() == void.class && m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == String.class) {
                        PLog.d("找到方法: " + m.getName());
                        m.setAccessible(true);
                        m.invoke(activity, new Object[]{null});
                    }
                }
            }
        });

    }
}
