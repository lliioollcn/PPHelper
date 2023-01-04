package cn.lliiooll.pphelper.hook;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.DexUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;
import java.util.*;

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
    };

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
                    }catch (Throwable e){
                        PLog.e(e);
                    }

                }

            }
        }
        XposedHelpers.findAndHookMethod("cn.xiaochuankeji.zuiyouLite.ui.splash.SplashActivity", HybridClassLoader.clLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                RelativeLayout root = (RelativeLayout) activity.findViewById(AppUtils.findId("id", "splash_open_cover")).getParent();
                View custom = LayoutInflater.from(activity).inflate(R.layout.pp_spalsh, null);
                root.addView(custom);
                TextView text = custom.findViewById(R.id.spalsh_text);
                ProgressBar bar = custom.findViewById(R.id.spalsh_bar);
                bar.setMax(obfs);
                final int[] obf = {0};
                for (BaseHook hook : hooks) {
                    if (hook.isEnable()) {
                        if (hook.needObf()) {
                            PLog.d("尝试加载需要反混淆的hook " + hook.getName() + "(" + hook.getLabel() + ") ");
                            Map<String, List<String>> finded = new HashMap<>();
                            hook.obf().forEach((k, i) -> {
                                text.setText("正在寻找被混淆的类: " + k);
                                finded.putAll(DexUtils.obf(new HashMap<String, List<String>>() {{
                                    put(k, i);
                                }}));
                                obf[0]++;
                                bar.setProgress(obf[0]);
                            });
                            try {
                                hook.doObf(finded);
                                hook.init();
                            }catch (Throwable e){
                                PLog.e(e);
                            }
                        }
                    }
                }
                text.setText("加载完毕~");
                bar.setMax(1);
                bar.setProgress(1, true);
            }
        });

    }

    public static ArrayList<BaseHook> getAllHooks() {
        return new ArrayList<>(Arrays.asList(hooks));
    }
}
