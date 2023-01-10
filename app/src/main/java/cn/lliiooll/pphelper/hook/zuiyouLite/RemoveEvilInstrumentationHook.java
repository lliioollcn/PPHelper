package cn.lliiooll.pphelper.hook.zuiyouLite;

import android.content.Context;
import android.view.View;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Method;

public class RemoveEvilInstrumentationHook extends BaseHook {

    public static RemoveEvilInstrumentationHook INSTANCE = new RemoveEvilInstrumentationHook();

    public RemoveEvilInstrumentationHook() {
        super("去他妈的EvilInstrumentation", "fuckEvilInstrumentation");
    }

    @Override
    public boolean init() {
        Class<?> clazz = HybridClassLoader.load("cn.xiaochuankeji.hermes.workaround.EvilInstrumentation");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().contains("attach")) {
                XposedBridge.hookMethod(m, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.d("阻止了方法: " + m.getName());
                        return null;
                    }
                });
            }
        }
        return true;
    }

    @Override
    public View getSettingsView(Context ctx) {
        return null;
    }

    @Override
    public boolean isEnable() {
        return true;
    }
}
