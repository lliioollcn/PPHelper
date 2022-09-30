package cn.lliiooll.pphelper.utils;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.system.Os;
import android.system.StructUtsname;
import android.widget.Toast;
import cn.hutool.core.io.FileUtil;
import cn.lliiooll.pphelper.BuildConfig;
import cn.lliiooll.pphelper.config.ConfigManager;
import cn.lliiooll.pphelper.startup.HookEntry;
import cn.lliiooll.pphelper.startup.HybridClassLoader;
import com.tencent.mmkv.MMKV;
import me.teble.xposed.autodaily.dexkit.DexKitHelper;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DexKit {

    public static String OBF_COMMENT_VIDEO = "Lcn/xiaochuankeji/zuiyouLite/common/CommentVideo;";
    public static String OBF_ACCOUNT_SERVICE_MANAGER = "Lcn/xiaochuankeji/zuiyouLite/api/account/AccountServiceManager";

    private static Map<String, String> caches = new ConcurrentHashMap<>();

    public static Map<String, String[]> find(ClassLoader loader, Map<String, Set<String>> input) {
        DexKitHelper helper = new DexKitHelper(loader);
        Map<String, String[]> results = helper.batchFindClassesUsedStrings(input, false, new int[0]);
        helper.release();
        return results;
    }

    public static native void test();

    public static void init() {
        PLog.log("正在加载lib库...");
        //System.loadLibrary("pp_native");
        load(Utils.getApplication());
        PLog.log("加载成功！！！");
    }

    public static Class<?> load(String key) {
        return caches.containsKey(key) ? Utils.loadClass(caches.get(key)) : null;
    }

    public static void cache(Map<String, String[]> result) {
        result.forEach((token, finds) -> {
            String filter = doFilter(token, Arrays.asList(finds));
            if (Utils.isNotBlank(filter)) {
                caches.put(token, filter);
                PLog.log("找到类: " + token + " -> " + filter);
            } else {
                PLog.log("未找到合适的类(NoFilter): " + Arrays.toString(finds));
            }
        });
    }

    public static String doFilter(String key, List<String> classes) {
        if (!classes.isEmpty() && Utils.isNotBlank(key)) {
            if (classes.size() == 1) {
                return doReplace(classes.get(0));
            }
            if (key.equalsIgnoreCase(OBF_COMMENT_VIDEO)) {
                Class<?> commentBeanCls = Utils.loadClass("cn.xiaochuankeji.zuiyouLite.data.CommentBean");
                for (String clazz : classes) {
                    String replace = doReplace(clazz);
                    PLog.log("正在过滤类: " + replace);
                    Class<?> cls = Utils.loadClass(replace);
                    if (cls != null) {
                        for (Method m : cls.getDeclaredMethods()) {
                            if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == commentBeanCls) {
                                PLog.log("过滤完毕: " + replace);
                                return replace;
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    @SuppressLint("UnsafeDynamicallyLoadedCode")
    public static void load(Context ctx) throws LinkageError {
        try {
            test();
            return;
        } catch (UnsatisfiedLinkError ignored) {
        }
        String abi = getAbiForLibrary();
        try {
            Class.forName(HybridClassLoader.getXposedBridgeClassName());
            // in host process
            try {
                String modulePath = HookEntry.getModulePath();
                if (modulePath != null) {
                    // try direct memory map
                    PLog.log("尝试加载libpp_native.so ......");
                    System.load(modulePath + "!/lib/" + abi + "/libpp_native.so");
                    PLog.log("尝试加载libmmkv.so ......");
                    System.load(modulePath + "!/lib/" + abi + "/libmmkv.so");
                    PLog.log("尝试加载liblog.so ......");
                    System.load(modulePath + "!/lib/" + abi + "/liblog.so");
                    PLog.log("dlopen by mmap success");
                }
            } catch (UnsatisfiedLinkError e1) {

                // give enough information to help debug
                // Is this CPU_ABI bad?
                PLog.log("Build.SDK_INT=" + Build.VERSION.SDK_INT);
                PLog.log("Build.CPU_ABI is: " + Build.CPU_ABI);
                PLog.log("Build.CPU_ABI2 is: " + Build.CPU_ABI2);
                PLog.log("Build.SUPPORTED_ABIS is: " + Arrays.toString(Build.SUPPORTED_ABIS));
                PLog.log("Build.SUPPORTED_32_BIT_ABIS is: " + Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
                PLog.log("Build.SUPPORTED_64_BIT_ABIS is: " + Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));
                // check whether this is a 64-bit ART runtime
                PLog.log("Process.is64bit is: " + Process.is64Bit());
                StructUtsname uts = Os.uname();
                PLog.log("uts.machine is: " + uts.machine);
                PLog.log("uts.version is: " + uts.version);
                PLog.log("uts.sysname is: " + uts.sysname);
                // panic, this is a bug

            }
        } catch (ClassNotFoundException e) {
            // not in host process, ignore
            System.loadLibrary("pp_native");
        }
        try {
            test();
        } catch (UnsatisfiedLinkError e) {
            PLog.log(e);
            PLog.log("加载失败，尝试方案2...");
            load2(ctx);
            return;
        }
        ConfigManager.init();
    }

    public static void load2(Context ctx) {
        String abi = getAbiForLibrary();
        File dir = ctx.getExternalFilesDir("helper_lib");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File pp_native = new File(dir, ".\\" + abi + "\\libpp_native.so");
        File mmkv = new File(dir, ".\\" + abi + "\\libmmkv.so");
        File log = new File(dir, ".\\" + abi + "\\liblog.so");
        if (pp_native.exists()) {
            pp_native.delete();
        }
        if (mmkv.exists()) {
            mmkv.delete();
        }
        if (log.exists()) {
            log.delete();
        }
        try {
            pp_native.createNewFile();
            mmkv.createNewFile();
            log.createNewFile();
            FileUtil.writeFromStream(DexKit.class.getClassLoader().getResourceAsStream("/lib/" + abi + "/libpp_native.so"), pp_native);
            FileUtil.writeFromStream(DexKit.class.getClassLoader().getResourceAsStream("/lib/" + abi + "/libmmkv.so"), mmkv);
            FileUtil.writeFromStream(DexKit.class.getClassLoader().getResourceAsStream("/lib/" + abi + "/liblog.so"), log);
            PLog.log("尝试加载libpp_native.so ......");
            System.load(pp_native.getAbsolutePath());
            PLog.log("尝试加载libmmkv.so ......");
            System.load(mmkv.getAbsolutePath());
            PLog.log("尝试加载liblog.so ......");
            System.load(log.getAbsolutePath());
            PLog.log("加载成功");
            ConfigManager.init();
        } catch (IOException e) {
            PLog.log(e);
        } catch (UnsatisfiedLinkError e) {
            PLog.log(">>>>>>>>>> 又nm得失败了！！！ <<<<<<<<<");
            PLog.log(e);
        }
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


    public static String getAbiForLibrary() {
        String[] supported = Process.is64Bit() ? Build.SUPPORTED_64_BIT_ABIS : Build.SUPPORTED_32_BIT_ABIS;
        if (supported == null || supported.length == 0) {
            throw new IllegalStateException("No supported ABI in this device");
        }
        List<String> abis = Arrays.asList("armeabi-v7a", "arm64-v8a");
        for (String abi : supported) {
            if (abis.contains(abi)) {
                return abi;
            }
        }
        throw new IllegalStateException("No supported ABI in " + Arrays.toString(supported));
    }
}