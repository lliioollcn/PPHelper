package cn.lliiooll.pphelper.hook;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;

public class AntiAD extends BaseHook {

    public static AntiAD INSTANCE = new AntiAD();

    public AntiAD() {
        super("去广告", "antiAd");
    }

    @Override
    public boolean init() {
        PLog.d("尝试加载类 cn.xiaochuankeji.hermes.core.Hermes");
        Class<?> clazz = HybridClassLoader.load("cn.xiaochuankeji.hermes.core.Hermes");
        for (Method m : clazz.getDeclaredMethods()) {
            PLog.d("过滤方法: " + m.getName());
            if (m.getName().contains("create") && m.getName().contains("AD")) {
                XposedBridge.hookMethod(m, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.d("已经阻止广告加载");
                        return null;
                    }
                });
            }
        }
        XposedHelpers.findAndHookMethod(Handler.class, "sendEmptyMessageDelayed", int.class, long.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if ((int) param.args[0] == 29) {
                    param.args[1] = 10L;
                }
            }
        });
        return true;
    }
}
