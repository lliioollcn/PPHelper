package cn.lliiooll.pphelper.startup;

import android.content.Context;
import android.widget.Toast;
import cn.lliiooll.pphelper.config.ConfigManager;
import cn.lliiooll.pphelper.hook.Hooks;
import cn.lliiooll.pphelper.lifecycle.Parasitics;
import cn.lliiooll.pphelper.utils.DexKit;
import cn.lliiooll.pphelper.utils.HostInfo;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.utils.Utils;
import cn.xiaochuankeji.zuiyouLite.app.AppController;
import com.tencent.mmkv.MMKV;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;
import java.lang.reflect.Method;

/**
 * 初始化类
 */
public class StartupHook {

    /**
     * 模块初始化
     *
     * @param instance          传入的 {@link cn.xiaochuankeji.zuiyouLite.app.AppController}
     * @param xposedClassLoader Xposed提供的类加载器
     */
    public static void init(Object instance, ClassLoader xposedClassLoader) throws Throwable {
        injectClassLoader(instance, xposedClassLoader);
        Toast.makeText(Utils.getApplication(),"正在寻找被混淆的类...",Toast.LENGTH_LONG).show();
        DexKit.init();
        Parasitics.injectModuleResources(Utils.getApplication().getResources());
        Parasitics.initForStubActivity(Utils.getApplication());
        ConfigManager.init();
        HostInfo.init();
        Hooks.init(instance);
    }

    /**
     * 注入类加载器
     *
     * @param instance          传入的 {@link cn.xiaochuankeji.zuiyouLite.app.AppController}
     * @param xposedClassLoader Xposed提供的类加载器
     */
    private static void injectClassLoader(Object instance, ClassLoader xposedClassLoader) throws Throwable {
        PLog.log("开始注入类加载器");
        ClassLoader appCl = instance.getClass().getClassLoader();// 宿主提供的类加载器
        ClassLoader bootCl = Context.class.getClassLoader();// Context的类加载器
        ClassLoader now = StartupHook.class.getClassLoader();// 模块类加载器
        ClassLoader parent = (ClassLoader) XposedHelpers.getObjectField(now, "parent");// 父类加载器
        if (parent == null) {
            parent = XposedBridge.class.getClassLoader();
        }
        if (!parent.getClass().getName().equals(HybridClassLoader.class.getName())) {
            XposedHelpers.setObjectField(now, "parent", new HybridClassLoader(appCl, bootCl, xposedClassLoader, parent));

        }
        PLog.log("注入类加载器成功");
    }
}

