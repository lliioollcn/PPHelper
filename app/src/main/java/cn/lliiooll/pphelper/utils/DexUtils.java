package cn.lliiooll.pphelper.utils;

import android.content.Context;
import io.luckypray.dexkit.DexKitBridge;
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DexUtils {

    public static void init() {

    }

    /**
     * 反混淆操作
     *
     * @param obf 反混淆条件
     * @return 根据条件找到的类
     */
    public static Map<String, List<String>> obf(Map<String, List<String>> obf) {
        if (PConfig.cache().isEmpty()) {
            return reCache(obf);
        }
        return PConfig.cache().size() != obf.size() ? reCache(obf) : PConfig.cache();
    }

    public static Map<String, List<String>> reCache(Map<String, List<String>> obf) {
        DexKitBridge dexkit = DexKitBridge.create(getHostPath(AppUtils.getHostAppInstance()));
        Map<String, List<DexClassDescriptor>> results = dexkit.batchFindClassesUsingStrings(obf, true, new int[0]);
        dexkit.close();
        Map<String, List<String>> finds = PConfig.cache();
        results.forEach((key, value) -> {
            List<String> res = new ArrayList<>();
            value.forEach(desc -> {
                res.add(desc.getName());
            });
            finds.put(key, res);
        });
        PConfig.cache(finds);
        return finds;
    }

    private static String getHostPath(Context ctx) {
        return ctx.getClassLoader().getResource("AndroidManifest.xml").getPath().replace("!/AndroidManifest.xml", "").replaceFirst("file:", "");
    }

    public static String doReplace(String clazz) {
        String cl = clazz;
        if (cl.startsWith("L")) {
            cl = cl.replaceFirst("L", "");
        }
        if (cl.endsWith(";")) {
            cl = cl.substring(0, cl.length() - 1);
        }
        if (cl.contains("\\")) {
            cl = cl.replace("\\", ".");
        }
        if (cl.contains("/")) {
            cl = cl.replace("/", ".");
        }
        return cl;
    }
}
