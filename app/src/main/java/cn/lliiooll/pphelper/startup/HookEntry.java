package cn.lliiooll.pphelper.startup;

import android.app.Application;
import android.content.Context;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.CliOper;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.utils.Utils;
import cn.lliiooll.pphelper.utils.hookstatus.HookStatusInit;
import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {

    public final static String PACKAGE_ZUIYOU_LITE = "cn.xiaochuankeji.zuiyouLite";
    private final static String PACKAGE_SELF = "cn.lliiooll.pphelper";
    private static String modulePath;
    private static XC_LoadPackage.LoadPackageParam sLoadPackageParam;

    public static String getModulePath() {
        return HookEntry.modulePath;
    }

    public static String getPackageName() {
        return sLoadPackageParam.packageName;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        sLoadPackageParam = lpparam;
        if (R.string.app_name >>> 24 == 0x7f) {
            PLog.log("package id must NOT be 0x7f, reject loading...");
            return;
        }
        Class<?> kXposedBridge = XposedBridge.class;
        if (!"de.robv.android.xposed.XposedBridge".equals(kXposedBridge.getName())) {
            String className = kXposedBridge.getName();
            String pkgName = className.substring(0, className.lastIndexOf('.'));
            HybridClassLoader.setObfuscatedXposedApiPackage(pkgName);
        }
        switch (lpparam.packageName) {
            case PACKAGE_ZUIYOU_LITE: {
                PLog.log("准备初始化");
                XposedHelpers.findAndHookMethod("cn.xiaochuankeji.zuiyouLite.app.AppController", lpparam.classLoader, "onCreate", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.log("开始初始化, app版本: "+ Utils.getVersion((Application) param.thisObject));
                        StartupHook.init(param.thisObject, lpparam.classLoader);
                    }
                });
            }
            case PACKAGE_SELF: {
                HookStatusInit.init(lpparam.classLoader);
            }
            default:
                break;
        }
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        modulePath = startupParam.modulePath;
        PLog.log("模块路径: " + modulePath);
    }
}
