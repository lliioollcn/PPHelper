package cn.lliiooll.pphelper.startup;

import android.app.Application;
import cn.lliiooll.pphelper.hook.HookBus;
import cn.lliiooll.pphelper.utils.AppUtils;
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
        XposedHelpers.findAndHookMethod(
                "cn.xiaochuankeji.zuiyouLite.app.AppController",
                param.classLoader,
                "onCreate",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam mParam) throws Throwable {
                        if (mParam.thisObject instanceof Application) {
                            Application hostApp = (Application) mParam.thisObject;
                            doStep(hostApp, param);
                        } else {
                            PLog.e("意料之外的错误: 宿主Application实例获取失败,类名: cn.xiaochuankeji.zuiyouLite.app.AppController");
                        }
                    }
                });
    }

    private static void doStep(Application hostApp, XC_LoadPackage.LoadPackageParam param) {
        AppUtils.init(hostApp);
        Natives.init();
        ClassLoaderInjector.init(hostApp, param.classLoader);
        ClassLoaderInjector.inject();// 注入类加载器
        ResourcesInjector.inject(AppUtils.getHostAppInstance().getResources());// 注入资源
        ActivityProxyInjector.inject();// 注入界面代理
        HookBus.init();// 初始化hook
    }
}
