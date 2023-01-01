package cn.lliiooll.pphelper.utils;

import android.app.Application;
import cn.lliiooll.pphelper.startup.HookEntry;
import dalvik.system.BaseDexClassLoader;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;
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
        BaseDexClassLoader cLoader = (BaseDexClassLoader) Natives.class.getClassLoader();
        PLog.d("尝试获取lib目录...");
        Object pathList = XposedHelpers.getObjectField(cLoader, "pathList");
        List<File> nativeLibraryDirectories = (List<File>) XposedHelpers.getObjectField(pathList, "nativeLibraryDirectories");
        String path = HookEntry.getPackageName() + "!/lib/arm64-v8a";
        PLog.d("尝试注入lib目录...");
        nativeLibraryDirectories.add(new File(path));
        XposedHelpers.setObjectField(pathList, "nativeLibraryDirectories", nativeLibraryDirectories);
        XposedHelpers.setObjectField(cLoader, "pathList", pathList);
        PLog.d("尝试加载native....");
        System.loadLibrary("pphelper");
    }
}
