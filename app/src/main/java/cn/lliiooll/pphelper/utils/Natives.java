package cn.lliiooll.pphelper.utils;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.system.Os;
import android.system.StructUtsname;
import cn.lliiooll.pphelper.startup.HookEntry;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * native类
 */
public class Natives {
    /**
     * 初始化native
     */
    public static void init() {
        Application app = AppUtils.getHostAppInstance();// 获取宿主application实例
        PLog.d(">>>>>>>>>> 模块类加载器 <<<<<<<<<<");
        /*
        BaseDexClassLoader cLoader = (BaseDexClassLoader) Natives.class.getClassLoader();
        PLog.d("尝试获取lib目录...");
        Object pathList = XposedHelpers.getObjectField(cLoader, "pathList");
        List<File> nativeLibraryDirectories = new ArrayList<>((List<File>) XposedHelpers.getObjectField(pathList, "nativeLibraryDirectories"));
        String path = HookEntry.getPackageName() + "!/lib/arm64-v8a";
        PLog.d("尝试注入lib目录...");
        nativeLibraryDirectories.add(new File(path));
        XposedHelpers.setObjectField(pathList, "nativeLibraryDirectories", nativeLibraryDirectories);
        XposedHelpers.setObjectField(cLoader, "pathList", pathList);
        PLog.d("尝试加载native....");

         */
        //System.loadLibrary("pphelper");
        load(app);
    }

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
                PLog.d("宿主app路径: " + hostPath);
                if (modulePath != null) {
                    File mmkvDir = new File(ctx.getFilesDir(), "pp_mmkv");
                    if (mmkvDir.isFile()) {
                        mmkvDir.delete();
                    }
                    if (!mmkvDir.exists()) {
                        mmkvDir.mkdirs();
                    }
                    MMKV.initialize(ctx, mmkvDir.getAbsolutePath(), s -> {
                        PLog.d("尝试加载libpphelper.so ......");
                        System.load(modulePath + "!/lib/" + abi + "/libpphelper.so");
                    });
                    PLog.d("dlopen by mmap success");
                }
            } catch (UnsatisfiedLinkError e1) {
                // give enough information to help debug
                // Is this CPU_ABI bad?
                PLog.d("Build.SDK_INT=" + Build.VERSION.SDK_INT);
                PLog.d("Build.CPU_ABI is: " + Build.CPU_ABI);
                PLog.d("Build.CPU_ABI2 is: " + Build.CPU_ABI2);
                PLog.d("Build.SUPPORTED_ABIS is: " + Arrays.toString(Build.SUPPORTED_ABIS));
                PLog.d("Build.SUPPORTED_32_BIT_ABIS is: " + Arrays.toString(Build.SUPPORTED_32_BIT_ABIS));
                PLog.d("Build.SUPPORTED_64_BIT_ABIS is: " + Arrays.toString(Build.SUPPORTED_64_BIT_ABIS));
                // check whether this is a 64-bit ART runtime
                PLog.d("Process.is64bit is: " + Process.is64Bit());
                StructUtsname uts = Os.uname();
                PLog.d("uts.machine is: " + uts.machine);
                PLog.d("uts.version is: " + uts.version);
                PLog.d("uts.sysname is: " + uts.sysname);
                // panic, this is a bug

            }
        } catch (ClassNotFoundException e) {
            // not in host process, ignore
            System.loadLibrary("pphelper");
        }
        try {
            test();
        } catch (UnsatisfiedLinkError e) {
            PLog.e(e);
            PLog.d("加载失败，尝试方案2...");
            load2(ctx);
            return;
        }
        PConfig.init();
    }

    public static native void test();

    private static String getHostPath(Context ctx) {
        return ctx.getClassLoader().getResource("AndroidManifest.xml").getPath().replace("!/AndroidManifest.xml", "").replaceFirst("file:", "");
    }

    private static List<String> libList = new ArrayList<String>() {{
        add("libpphelper");
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
                IOUtils.write(Natives.class.getClassLoader().getResourceAsStream("/lib/" + abi + "/" + lib + ".so"), fileLib);
            } catch (Throwable e) {
                PLog.e(e);
            }
        });
        try {
            File mmkvDir = new File(ctx.getFilesDir(), "pp_mmkv");
            if (mmkvDir.isFile()) {
                mmkvDir.delete();
            }
            if (!mmkvDir.exists()) {
                mmkvDir.mkdirs();
            }

            File ppNativeFile = new File(dir, ".\\" + abi + "\\" + libList.get(0) + ".so");
            MMKV.initialize(ctx, mmkvDir.getAbsolutePath(), s -> {
                PLog.d("尝试加载libpphelper.so ......");
                System.load(ppNativeFile.getAbsolutePath());
                PLog.d("加载成功");
            });

            PConfig.init();
        } catch (UnsatisfiedLinkError e) {
            PLog.d(">>>>>>>>>> 又nm得失败了！！！ <<<<<<<<<");
            PLog.e(e);
        }
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
}
