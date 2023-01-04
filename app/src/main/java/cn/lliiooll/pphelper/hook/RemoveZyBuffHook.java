package cn.lliiooll.pphelper.hook;

import android.content.Context;
import android.view.View;
import cn.lliiooll.pphelper.utils.DexUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoveZyBuffHook extends BaseHook {

    private static String DEOBF = "";
    public static RemoveZyBuffHook INSTANCE = new RemoveZyBuffHook();

    public RemoveZyBuffHook() {
        super("去他妈的ZyBuff", "fuckZyBuff");
    }

    @Override
    public boolean init() {

        Class<?> clazz1 = HybridClassLoader.load(DEOBF);
        for (Method m : clazz1.getDeclaredMethods()) {
            if (m.getParameterTypes().length == 0 && m.getReturnType() == void.class) {
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
    public boolean needObf() {
        return true;
    }

    @Override
    public Map<String, List<String>> obf() {
        return new HashMap<String, List<String>>() {{
            put("cn.xiaochuankeji.zuiyouLite.control.crashcatch.ZyBuff", new ArrayList<String>() {{
                add("ZyBuff");
                add("start buff");
                add("start bless finalizer");
            }});
        }};
    }

    @Override
    public void doObf(Map<String, List<String>> finds) {
        if (!finds.containsKey("cn.xiaochuankeji.zuiyouLite.control.crashcatch.ZyBuff")) {
            doObf(DexUtils.reCache(obf()));
            return;
        }
        finds.get("cn.xiaochuankeji.zuiyouLite.control.crashcatch.ZyBuff").forEach(m -> {
            PLog.d("过滤类: " + m);
            DEOBF = m;
        });

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
