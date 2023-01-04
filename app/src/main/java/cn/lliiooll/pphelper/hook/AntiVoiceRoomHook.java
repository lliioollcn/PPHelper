package cn.lliiooll.pphelper.hook;

import android.os.Bundle;
import android.widget.Toast;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;

public class AntiVoiceRoomHook extends BaseHook {

    public static AntiVoiceRoomHook INSTANCE = new AntiVoiceRoomHook();

    public AntiVoiceRoomHook() {
        super("去除语音房", "removeVoiceRoom");
    }

    @Override
    public boolean init() {
        Class<?> clazz = HybridClassLoader.load("com.youyisia.voices.sdk.api.HYVoiceRoomSdk");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase("isInited")) {
                XposedBridge.hookMethod(m, new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.d("替换了方法: " + m.getName());
                        return true;
                    }
                });
            }
        }
        return true;
    }
}
