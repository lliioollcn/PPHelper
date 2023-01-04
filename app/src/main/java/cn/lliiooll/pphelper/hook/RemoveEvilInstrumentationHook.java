package cn.lliiooll.pphelper.hook;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.DexUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
