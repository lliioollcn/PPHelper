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

    public static String OBF_COMMENT_VIDEO = "Lcn/xiaochuankeji/zuiyouLite/common/CommentVideo;";
    public static String OBF_ACCOUNT_SERVICE_MANAGER = "Lcn/xiaochuankeji/zuiyouLite/api/account/AccountServiceManager";
    public static String OBF_HOTFIX_INIT = "Lcn/xiaochuankeji/zuiyouLite/common/robust/RobustStater";

    public static String OBF_PUBLISH_BUS = "Lcn/xiaochuankeji/zuiyouLite/publish/PublishBus";
    public static String OBF_PUBLISH_DATA = "Lcn/xiaochuankeji/zuiyouLite/publish/PublishData";
    public static String OBF_CONFIG_PARSER = "Lcn/xiaochuankeji/zuiyouLite/config/PPConfigParser";
    public static String OBF_POST_REVIEW_AIO1 = "Lcn/xiaochuankeji/zuiyouLite/post/review/AIO1";// m.g.l.a
    public static String OBF_POST_REVIEW_AIO2 = "Lcn/xiaochuankeji/zuiyouLite/post/review/AIO2";
    public final static Map<String, Set<String>> obfMap = new HashMap<String, Set<String>>() {{
        put(DexKit.OBF_POST_REVIEW_AIO1, new HashSet<String>() {{
            add("^%d:%02d:%02d$");
            add("^%02d:%02d$");
            add("00:00");
        }});
        put(DexKit.OBF_POST_REVIEW_AIO2, new HashSet<String>() {{
            add("巡查举报");
            add("开启存储权限才能正常下载");
            add("去设置");
            add("举报成功，感谢你对家园的贡献!");
        }});

        put(DexKit.OBF_COMMENT_VIDEO, new HashSet<String>() {{
            add("event_media_play_observer");
            add("event_on_play_review_comment");
            add("post");
            add("review");
            add("+%d");
            add("http://alfile.ippzone.com/img/mp4/id/");
            add("videocomment");
        }});
        put(DexKit.OBF_ACCOUNT_SERVICE_MANAGER, new HashSet<String>() {{
            add("avatar");
            add("third_force_bind_phone");
        }});
        put(DexKit.OBF_HOTFIX_INIT, new HashSet<String>() {{
            add("event_on_load_hot_config_success");
            add("app_config_json_parse");
            add("local config cold/get json data parse failed.");
        }});
        put(DexKit.OBF_PUBLISH_BUS, new HashSet<String>() {{
            add("仅可同时进行3个图片视频或语音发布任务");
        }});
        put(DexKit.OBF_PUBLISH_DATA, new HashSet<String>() {{
            add("距你-18cm");
        }});
        put(DexKit.OBF_CONFIG_PARSER, new HashSet<String>() {{
            add("server config Hot/get json data parse failed.");
        }});
    }};

    private static Map<String, String> caches = new ConcurrentHashMap<>();
    public static final Class<?> clazz_long = long.class;
    public static final Class<?> clazz_boolean = boolean.class;
    public static final Class<?> clazz_void = void.class;
    public static final Class<?> clazz_int = int.class;

    public static Map<String, String[]> find(ClassLoader loader, Map<String, Set<String>> input) {
        DexKitBridge helper = DexKitBridge.create(loader);

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
            if (key.equalsIgnoreCase(OBF_PUBLISH_BUS)) {
                return doReplace(classes.get(0)).split("\\$")[0];
            }
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
            if (key.equalsIgnoreCase(OBF_POST_REVIEW_AIO1)){
                for (String clazz : classes) {
                    String replace = doReplace(clazz);
                    PLog.log("正在过滤类: " + replace);
                    Class<?> cls = Utils.loadClass(replace);
                    if (cls != null) {
                        for (Method m : cls.getDeclaredMethods()) {
                            if (m.getParameterTypes().length == 1 && m.getParameterTypes()[0] == Context.class && m.getReturnType() == Activity.class) {
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
                    PLog.log("尝试加载libdexkit.so ......");
                    System.load(modulePath + "!/lib/" + abi + "/libdexkit.so");
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
        File dexkit = new File(dir, ".\\" + abi + "\\libdexkit.so");
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
            PLog.log("尝试加载libdexkit.so ......");
            System.load(dexkit.getAbsolutePath());
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