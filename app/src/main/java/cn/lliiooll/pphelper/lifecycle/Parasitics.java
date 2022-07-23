
package cn.lliiooll.pphelper.lifecycle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.app.UiAutomation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.*;
import android.view.KeyEvent;
import android.view.MotionEvent;
import androidx.annotation.Nullable;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.SettingsActivity;
import cn.lliiooll.pphelper.startup.HookEntry;
import cn.lliiooll.pphelper.startup.HybridClassLoader;
import cn.lliiooll.pphelper.startup.StartupHook;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.utils.Utils;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;
import java.lang.reflect.*;
import java.util.List;

public class Parasitics {

    private static boolean __stub_hooked = false;

    /**
     * 注入模块资源到宿主程序
     *
     * @param res 宿主程序的资源
     */
    public static void injectModuleResources(Resources res) {
        PLog.log("注入模块资源到宿主程序...");
        if (res == null) {
            return;
        }
        try {
            checkInject(res);
            return;
        } catch (Resources.NotFoundException ignored) {
        }
        try {
            AssetManager manager = res.getAssets();
            String modulePath = HookEntry.getModulePath();
            XposedHelpers.callMethod(manager, "addAssetPath", modulePath);
            try {
                checkInject(res);
                PLog.log("资源注入成功...");
            } catch (Throwable t) {
                PLog.log("资源注入失败");
                t.printStackTrace();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

    }

    /**
     * 检查注入
     *
     * @param res 宿主程序的资源
     */
    private static void checkInject(Resources res) throws Resources.NotFoundException {
        try {
            res.getLayout(R.layout.settings_activity);
            //res.getString(R.string.res_inject_success);
        } catch (Resources.NotFoundException ignored) {
            throw new Resources.NotFoundException();
        }
    }

    public static void initForStubActivity(Context ctx) {
        if (__stub_hooked) {
            return;
        }
        PLog.log("注入代理");
        try {
            PLog.log("开始注入 instrumentation");
            Class<?> clazz_ActivityThread = Class.forName("android.app.ActivityThread");
            Object sCurrentActivityThread = XposedHelpers.callStaticMethod(clazz_ActivityThread, "currentActivityThread");
            Instrumentation instrumentation = (Instrumentation) XposedHelpers.getObjectField(sCurrentActivityThread, "mInstrumentation");
            XposedHelpers.setObjectField(sCurrentActivityThread, "mInstrumentation", new ParasiticInstrumentation(instrumentation));
            PLog.log("instrumentation注入完毕");
            PLog.log("开始注入 handler");
            Handler oriHandler = (Handler) XposedHelpers.getObjectField(sCurrentActivityThread, "mH");
            Handler.Callback current = (Handler.Callback) XposedHelpers.getObjectField(oriHandler, "mCallback");
            if (current == null || !current.getClass().getName().equals(ParasiticHandler.class.getName())) {
                XposedHelpers.setObjectField(oriHandler, "mCallback", new ParasiticHandler(current));
            }
            PLog.log("handler注入完毕");
            PLog.log("开始注入activityManager");
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
                    PLog.log("WTF: Unable to get IActivityManagerSingleton");
                    PLog.log(err1);
                    PLog.log(err2);
                    return;
                }
            }
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);
            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Object mInstance = XposedHelpers.getObjectField(gDefault, "mInstance");
            Object amProxy = Proxy.newProxyInstance(
                    SettingsActivity.class.getClassLoader(),
                    new Class[]{Class.forName("android.app.IActivityManager")},
                    new IActivityManagerHandler(mInstance));
            XposedHelpers.setObjectField(gDefault, "mInstance", amProxy);
            PLog.log("activityManager注入完毕");
            PLog.log("开始注入 IActivityTaskManager");
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
            PLog.log("IActivityTaskManager注入完毕");
            PLog.log("开始注入PackageManager");
            Field sPackageManagerField = clazz_ActivityThread.getDeclaredField("sPackageManager");
            sPackageManagerField.setAccessible(true);
            Object packageManagerImpl = XposedHelpers.getObjectField(sCurrentActivityThread, "sPackageManager");
            Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
            PackageManager pm = ctx.getPackageManager();
            Object pmProxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                    new Class[]{iPackageManagerInterface},
                    new PackageManagerInvocationHandler(packageManagerImpl));
            XposedHelpers.setObjectField(sCurrentActivityThread, "sPackageManager", pmProxy);
            XposedHelpers.setObjectField(pm, "mPM", pmProxy);
            PLog.log("PackageManager注入完毕");
            __stub_hooked = true;
        } catch (Exception e) {
            PLog.log(e);
        }
    }
}
