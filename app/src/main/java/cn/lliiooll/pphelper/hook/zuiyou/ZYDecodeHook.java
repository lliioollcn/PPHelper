package cn.lliiooll.pphelper.hook.zuiyou;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.*;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;
import java.lang.reflect.Method;
import java.util.*;

public class ZYDecodeHook extends BaseHook {

    public static ZYDecodeHook INSTANCE = new ZYDecodeHook();
    public static final String OBF = "cn.xiaochuankeji.tieba.digest.Decoder";
    public static String DEOBF = "";

    public ZYDecodeHook() {
        super("最右文本解密", ZYDecodeHook.class.getSimpleName());
    }

    @Override
    public boolean init() {

        Class<?> clazz = HybridClassLoader.load(DEOBF);
        /*
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getReturnType() == String.class && m.getParameterTypes().length > 0 && m.getParameterTypes()[0] == String.class) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.d("调用解密: " + param.args[0]);
                        PLog.d("解密结果: " + param.getResult());
                        File dir = AppUtils.getHostAppInstance().getExternalFilesDir("helperDebug");
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        File file = new File(dir, "decodes.txt");
                        IOUtils.append(param.args[0] + "          " + param.getResult() + "\n", file);
                        PLog.d("写入完毕");
                    }
                });
                break;
            }
        }

         */
        return true;
    }

    @Override
    public View getSettingsView(Context ctx) {
        return null;
    }

    @Override
    public boolean needObf() {
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public Map<String, List<String>> obf() {
        return new HashMap<String, List<String>>() {{
            put(OBF, new ArrayList<String>() {{
                add("&F&xC$#&eELI");
            }});
        }};
    }

    @Override
    public void doObf(Map<String, List<String>> finds) {
        List<String> result = finds.get(OBF);
        if (result != null) {
            result.forEach(c -> {
                String cn = DexUtils.doReplace(c);
                PLog.d("开始过滤类: " + cn);
                DEOBF = c;
            });
        }
    }
}
