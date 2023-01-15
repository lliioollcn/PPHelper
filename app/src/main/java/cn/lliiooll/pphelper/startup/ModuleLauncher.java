package cn.lliiooll.pphelper.startup;

import android.app.Application;
import cn.lliiooll.pphelper.hook.HookBus;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.ffmpeg.FFmpeg;
import cn.lliiooll.pphelper.utils.HostInfo;
import cn.lliiooll.pphelper.utils.Natives;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 模块启动器，用于模块的初始化
 */
public class ModuleLauncher {
    /**
     * 初始化模块启动器
     *
     * @param param 传入的参数
     * @see de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
     */
    public static void init(XC_LoadPackage.LoadPackageParam param) {
        if (param.processName.contains(":")) {
            PLog.e("不是主进程，拒绝加载");
            return;
        }
        PLog.d("开始加载,进程名称: " + param.processName);
        String applicationClazz = "";
        if (param.packageName.equalsIgnoreCase(HostInfo.ZuiyouLite.PACKAGE_NAME)) {
            PLog.d("开始初始化皮皮搞笑...");
            applicationClazz = "cn.xiaochuankeji.zuiyouLite.app.AppController";
        } else if (param.packageName.equalsIgnoreCase(HostInfo.TieBa.PACKAGE_NAME)) {
            PLog.d("开始初始化最右...");
            applicationClazz = "cn.xiaochuankeji.tieba.AppController";
        } else {
            PLog.d("未知的目标程序，不进行初始化");
            applicationClazz = null;
        }
        if (applicationClazz != null) {
            String finalApplicationClazz = applicationClazz;
            XposedHelpers.findAndHookMethod(
                    applicationClazz,
                    param.classLoader,
                    "onCreate",
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam mParam) throws Throwable {
                            if (mParam.thisObject instanceof Application) {
                                Application hostApp = (Application) mParam.thisObject;
                                doStep(hostApp, param);
                            } else {
                                PLog.e("意料之外的错误: 宿主Application实例获取失败,类名: " + finalApplicationClazz);
                            }
                        }
                    });
        }

    }

    private static void doStep(Application hostApp, XC_LoadPackage.LoadPackageParam param) {
        AppUtils.init(hostApp);
        Natives.init();
        FFmpeg.init();
        ClassLoaderInjector.init(hostApp, param.classLoader);
        ClassLoaderInjector.inject();// 注入类加载器
        ResourcesInjector.inject(AppUtils.getHostAppInstance().getResources());// 注入资源
        ActivityProxyInjector.inject();// 注入界面代理
        HookBus.init(param);// 初始化hook
    }
}
