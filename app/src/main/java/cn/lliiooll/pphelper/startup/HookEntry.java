package cn.lliiooll.pphelper.startup;

import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.startup.hookstatus.HookStatusInit;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * 模块入口
 */
public class HookEntry implements IXposedHookLoadPackage, IXposedHookZygoteInit {


    private final static String PACKAGE_TARGET = "cn.xiaochuankeji.zuiyouLite";// 目标应用包名
    private final static String PACKAGE_SELF = "cn.lliiooll.pphelper";// 模块包名

    public static String getModulePath() {
        return modulePath;
    }

    private static String modulePath = "";

    public static String getPackageName() {
        return PACKAGE_TARGET;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam param) throws Throwable {
        if (R.string.app_name >>> 24 == 0x7f) {
            PLog.e("包id不能为 0x7f, 拒绝加载...");
            return;
        }
        if (param.packageName.equalsIgnoreCase(PACKAGE_TARGET)) {
            //TODO: 执行模块初始化
            ModuleLauncher.init(param);
        } else if (param.packageName.equalsIgnoreCase(PACKAGE_SELF)) {
            //TODO: 执行模块激活状态更新
            PLog.d("<<<<<<<<<<<<<<<<<<<<");
            HookStatusInit.init(param.classLoader);
        } else {
            //TODO: 不是目标应用
            PLog.e("非目标应用, 拒绝加载...");
        }
    }

    @Override
    public void initZygote(StartupParam param) throws Throwable {
        HookEntry.modulePath = param.modulePath;
        ResourcesInjector.init(param.modulePath);
    }
}
