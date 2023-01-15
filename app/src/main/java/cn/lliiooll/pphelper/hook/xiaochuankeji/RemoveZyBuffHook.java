package cn.lliiooll.pphelper.hook.xiaochuankeji;

import android.content.Context;
import android.view.View;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.DexUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.config.PConfig;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class RemoveZyBuffHook extends BaseHook {

    private static String DEOBF = "";
    private static String OBF = "cn.xiaochuankeji.zuiyouLite.control.crashcatch.ZyBuff";
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
        Map<String, List<String>> cache = PConfig.cache();
        AtomicBoolean need = new AtomicBoolean(false);
        if (cache.containsKey(OBF)) {
            if (cache.getOrDefault(OBF, new ArrayList<>()).size() > 0) {
                cache.get(OBF).forEach(c -> {
                    try {
                        HybridClassLoader.loadWithThrow(c);
                    } catch (Throwable e) {
                        need.set(true);
                    }
                });
            } else {
                need.set(true);
            }
        } else {
            need.set(true);
        }
        return need.get();
    }

    @Override
    public Map<String, List<String>> obf() {
        return new HashMap<String, List<String>>() {{
            put(OBF, new ArrayList<String>() {{
                add("ZyBuff");
                add("start buff");
                add("start bless finalizer");
            }});
        }};
    }

    @Override
    public void doObf(Map<String, List<String>> finds) {
        if (!finds.containsKey(OBF)) {
            doObf(DexUtils.reCache(obf()));
            return;
        }
        finds.get(OBF).forEach(m -> {
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
