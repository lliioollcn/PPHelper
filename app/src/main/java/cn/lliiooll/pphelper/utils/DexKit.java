package cn.lliiooll.pphelper.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.system.Os;
import android.system.StructUtsname;
import androidx.recyclerview.widget.RecyclerView;
import cn.lliiooll.pphelper.config.ConfigManager;
import cn.lliiooll.pphelper.startup.HookEntry;
import cn.lliiooll.pphelper.startup.HybridClassLoader;
import com.tencent.mmkv.MMKV;
import io.luckypray.dexkit.DexKitBridge;
import io.luckypray.dexkit.descriptor.member.DexClassDescriptor;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DexKit {

    public static final Class<?> clazz_long = long.class;
    public static final Class<?> clazz_boolean = boolean.class;
    public static final Class<?> clazz_void = void.class;
    public static final Class<?> clazz_int = int.class;

    public static Map<String, String[]> find(Map<String, Set<String>> input) {
        DexKitBridge helper = DexKitBridge.create(getHostPath(Utils.getApplication()));

        Map<String, List<DexClassDescriptor>> results = helper.batchFindClassesUsingStrings(input, true, new int[0]);
        helper.close();
        Map<String, String[]> finds = new HashMap<>();
        results.forEach((key, value) -> {
            List<String> res = new ArrayList<>();
            value.forEach(desc -> {
                res.add(desc.getName());
            });
            finds.put(key, res.toArray(new String[0]));
        });

        return finds;
    }

    public static native void test();

    public static void init() {
        PLog.log("正在加载lib库...");
        //System.loadLibrary("pp_native");
        load(Utils.getApplication());
        PLog.log("加载成功！！！");
    }

    public static Class<?> load(String key) {
        return ConfigManager.hasCache(key) ? Utils.loadClass(ConfigManager.cache(key)) : null;
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
                String hostPath = getHostPath(ctx);
                PLog.log("宿主app路径: " + hostPath);
                if (modulePath != null) {
                    File mmkvDir = new File(Utils.getApplication().getFilesDir(), "pp_mmkv");
                    if (mmkvDir.isFile()) {
                        mmkvDir.delete();
                    }
                    if (!mmkvDir.exists()) {
                        mmkvDir.mkdirs();
                    }
                    MMKV.initialize(ctx, mmkvDir.getAbsolutePath(), s -> {
                        PLog.log("尝试加载libpp_native.so ......");
                        System.load(modulePath + "!/lib/" + abi + "/libpp_native.so");
                    });
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

    private static String getHostPath(Context ctx) {
        return ctx.getClassLoader().getResource("AndroidManifest.xml").getPath().replace("!/AndroidManifest.xml", "").replaceFirst("file:", "");
    }

    private static List<String> libList = new ArrayList<String>() {{
        add("libpp_native");
        add("libmmkv");
        add("liblog");
        add("libdexkit");
    }};

    public static void load2(Context ctx) {
        String abi = getAbiForLibrary();
        File dir = ctx.getExternalFilesDir("helper_lib");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        libList.forEach(lib -> {
            File fileLib = new File(dir, ".\\" + abi + "\\" + lib + ".so");
            if (fileLib.exists()) {
                fileLib.delete();
            }

            try {
                fileLib.createNewFile();
                //FileUtil.writeFromStream(ctx.getClassLoader().getResourceAsStream("/lib/" + abi + "/libmmkv.so"), mmkv);
                FileUtil.writeFromStream(DexKit.class.getClassLoader().getResourceAsStream("/lib/" + abi + "/" + lib + ".so"), fileLib);
            } catch (Throwable e) {
                PLog.log(e);
            }
        });
        try {
            File mmkvDir = new File(Utils.getApplication().getFilesDir(), "pp_mmkv");
            if (mmkvDir.isFile()) {
                mmkvDir.delete();
            }
            if (!mmkvDir.exists()) {
                mmkvDir.mkdirs();
            }

            File ppNativeFile = new File(dir, ".\\" + abi + "\\" + libList.get(0) + ".so");
            MMKV.initialize(ctx, mmkvDir.getAbsolutePath(), s -> {
                PLog.log("尝试加载libpp_native.so ......");
                System.load(ppNativeFile.getAbsolutePath());
                PLog.log("加载成功");
            });

            ConfigManager.init();
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
        List<String> abis = Arrays.asList(/*"armeabi-v7a",*/ "arm64-v8a");
        for (String abi : supported) {
            if (abis.contains(abi)) {
                return abi;
            }
        }
        throw new IllegalStateException("No supported ABI in " + Arrays.toString(supported));
    }

    public static void cache(@Nullable String key, @Nullable String replace) {
        ConfigManager.cache(key, replace);
    }
}