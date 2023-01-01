package cn.lliiooll.pphelper.startup;

import android.content.res.AssetManager;
import android.content.res.Resources;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XposedHelpers;

/**
 * 资源注入器，用于注入模块资源到宿主资源加载列表
 */
public class ResourcesInjector {
    private static String modulePath = null;
    private static boolean inited = false;

    /**
     * 初始化资源注入器
     *
     * @param modulePath 模块路径
     */
    public static void init(String modulePath) {
        if (inited) {
            PLog.d("资源注入器已经初始化过了!");
            return;
        }
        ResourcesInjector.modulePath = modulePath;
        PLog.d("初始化资源注入器，模块路径: " + modulePath);
        inited = true;
    }

    /**
     * 执行注入
     */
    public static void inject(Resources res) {
        if (!inited) {
            PLog.e("资源注入器未初始化...");
            return;
        }
        PLog.d("尝试注入资源...");
        if (res == null) {
            PLog.d("宿主的资源为null!");
            return;
        }
        try {
            checkInject(res);
            return;
        } catch (Resources.NotFoundException ignored) {
        }
        try {
            AssetManager manager = res.getAssets();
            XposedHelpers.callMethod(manager, "addAssetPath", modulePath);// AssetManager.addAssetPath(modulePath);
            try {
                checkInject(res);
                PLog.d("资源注入成功...");
            } catch (Throwable t) {
                PLog.d("资源注入失败");
                PLog.e(t);
            }
        } catch (Throwable t) {
            PLog.e(t);
        }

    }

    /**
     * 检查注入
     *
     * @param res 宿主程序的资源
     */
    private static void checkInject(Resources res) throws Resources.NotFoundException {
        try {
            res.getLayout(R.layout.activity_main);
            //res.getString(R.string.res_inject_success);
        } catch (Resources.NotFoundException ignored) {
            throw new Resources.NotFoundException();
        }
    }
}
