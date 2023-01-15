package cn.lliiooll.pphelper.startup;

import android.app.Instrumentation;
import android.content.pm.PackageManager;
import android.os.Handler;
import cn.lliiooll.pphelper.activity.MainActivity;
import cn.lliiooll.pphelper.lifecycle.IActivityManagerHandler;
import cn.lliiooll.pphelper.lifecycle.PackageManagerInvocationHandler;
import cn.lliiooll.pphelper.lifecycle.ParasiticHandler;
import cn.lliiooll.pphelper.lifecycle.ParasiticInstrumentation;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HostInfo;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * 界面代理注入器，用于在宿主模块打开模块界面
 */
public class ActivityProxyInjector {
    private static boolean inited = false;

    /**
     * 执行注入
     */
    public static void inject() {
        if (inited) {
            PLog.d("已经注入过界面代理...");
            return;
        }
        PLog.d("尝试注入界面代理...");
        try {
            PLog.d("开始注入 instrumentation");
            Class<?> clazz_ActivityThread = Class.forName("android.app.ActivityThread");
            Object sCurrentActivityThread = XposedHelpers.callStaticMethod(clazz_ActivityThread, "currentActivityThread");
            Instrumentation instrumentation = (Instrumentation) XposedHelpers.getObjectField(sCurrentActivityThread, "mInstrumentation");
            XposedHelpers.setObjectField(sCurrentActivityThread, "mInstrumentation", new ParasiticInstrumentation(instrumentation));
            PLog.d("instrumentation注入完毕");
            PLog.d("开始注入 handler");
            Handler oriHandler = (Handler) XposedHelpers.getObjectField(sCurrentActivityThread, "mH");
            Handler.Callback current = (Handler.Callback) XposedHelpers.getObjectField(oriHandler, "mCallback");
            if (current == null || !current.getClass().getName().equals(ParasiticHandler.class.getName())) {
                XposedHelpers.setObjectField(oriHandler, "mCallback", new ParasiticHandler(current));
            }
            PLog.d("handler注入完毕");
            PLog.d("开始注入activityManager");
            Class<?> activityManagerClass;
            Field gDefaultField;
            try {
                activityManagerClass = Class.forName("android.app.ActivityManagerNative");
                gDefaultField = activityManagerClass.getDeclaredField("gDefault");
            } catch (Exception err1) {
                try {
                    activityManagerClass = Class.forName("android.app.ActivityManager");
                    gDefaultField = activityManagerClass
                            .getDeclaredField("IActivityManagerSingleton");
                } catch (Exception err2) {
                    PLog.d("WTF: Unable to get IActivityManagerSingleton");
                    PLog.e(err1);
                    PLog.e(err2);
                    return;
                }
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Object mInstance = XposedHelpers.getObjectField(gDefault, "mInstance");
            Object amProxy = Proxy.newProxyInstance(
                    MainActivity.class.getClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new IActivityManagerHandler(mInstance));
            XposedHelpers.setObjectField(gDefault, "mInstance", amProxy);
            PLog.d("activityManager注入完毕");
            PLog.d("开始注入 IActivityTaskManager");
            try {
                Class<?> activityTaskManagerClass = Class
                        .forName("android.app.ActivityTaskManager");
                Field fIActivityTaskManagerSingleton = activityTaskManagerClass
                        .getDeclaredField("IActivityTaskManagerSingleton");
                fIActivityTaskManagerSingleton.setAccessible(true);
                Object singleton = fIActivityTaskManagerSingleton.get(null);
                singletonClass.getMethod("get").invoke(singleton);
                Object mDefaultTaskMgr = XposedHelpers.getObjectField(singleton, "mInstance");
                Object proxy2 = Proxy.newProxyInstance(
                        HybridClassLoader.clLoader,
                        new Class[]{Class.forName("android.app.IActivityTaskManager")},
                        new IActivityManagerHandler(mDefaultTaskMgr));
                XposedHelpers.setObjectField(singleton, "mInstance", proxy2);
            } catch (Exception err3) {
                err3.printStackTrace();
            }
            PLog.d("IActivityTaskManager注入完毕");
            PLog.d("开始注入PackageManager");
            Field sPackageManagerField = clazz_ActivityThread.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object packageManagerImpl = XposedHelpers.getObjectField(sCurrentActivityThread, "sPackageManager");
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            PackageManager pm = AppUtils.getHostAppInstance().getPackageManager();
            Object pmProxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class[]{iPackageManagerInterface},
                    new PackageManagerInvocationHandler(packageManagerImpl));
            XposedHelpers.setObjectField(sCurrentActivityThread, "sPackageManager", pmProxy);
            XposedHelpers.setObjectField(pm, "mPM", pmProxy);
            PLog.d("PackageManager注入完毕");
            inited = true;
        } catch (Exception e) {
            PLog.e(e);
        }
    }
}
