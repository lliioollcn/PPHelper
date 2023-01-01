package cn.lliiooll.pphelper.startup;

import android.app.Application;
import android.content.Context;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

/**
 * 类加载器注入器，用于解决部分找不到类的问题
 */
public class ClassLoaderInjector {

    private static Application app;
    private static ClassLoader xposedClassLoader;
    private static boolean inited = false;

    /**
     * 初始化类加载器注入器
     *
     * @param hostApp           宿主app实例
     * @param xposedClassLoader xposed提供的类加载器
     */
    public static void init(Application hostApp, ClassLoader xposedClassLoader) {
        if (inited) {
            PLog.d("类加载器注入器已经初始化过了!");
            return;
        }
        ClassLoaderInjector.app = hostApp;
        ClassLoaderInjector.xposedClassLoader = xposedClassLoader;
        PLog.d("初始化类加载器注入器,宿主app对象: " + hostApp.getClass().getName() + " ,xposed提供的类加载器: " + xposedClassLoader.getClass().getName());
        inited = true;
    }

    /**
     * 执行注入
     */
    public static void inject() {
        if (!inited) {
            PLog.e("类加载器注入器未初始化...");
            return;
        }
        PLog.d("尝试注入类加载器...");
        ClassLoader appCl = app.getClass().getClassLoader();// 宿主提供的类加载器
        ClassLoader bootCl = Context.class.getClassLoader();// Context的类加载器
        ClassLoader now = ClassLoaderInjector.class.getClassLoader();// 模块类加载器
        ClassLoader parent = (ClassLoader) XposedHelpers.getObjectField(now, "parent");// 父类加载器
        if (parent == null) {
            parent = XposedBridge.class.getClassLoader();
        }
        if (!parent.getClass().getName().equals(HybridClassLoader.class.getName())) {
            XposedHelpers.setObjectField(now, "parent", new HybridClassLoader(appCl, bootCl, xposedClassLoader, parent));

        }
        PLog.d("注入类加载器成功");
    }
}
