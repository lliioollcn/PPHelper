package cn.lliiooll.pphelper.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import cn.lliiooll.pphelper.config.PConfig;
import cn.lliiooll.pphelper.startup.HookEntry;
import de.robv.android.xposed.XposedHelpers;

import java.util.Arrays;

/**
 * Application工具类
 */
public class AppUtils {


    private static Application hostApp = null;// 宿主app实例

    /**
     * 初始化工具类
     *
     * @param hostApp 宿主app实例
     */
    public static void init(Application hostApp) {
        AppUtils.hostApp = hostApp;
    }

    /**
     * @return 宿主Application对象，如果初始化未完成或者初始化失败则返回null
     */
    public static Application getHostAppInstance() {
        return hostApp;
    }

    public static void hideIcon(Context ctx, String alias) {
        PackageManager pkM = ctx.getPackageManager();
        boolean hide = isHide();
        PConfig.setEnable("app_hide", !hide);
        pkM.setComponentEnabledSetting(new ComponentName(ctx, alias), hide ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED : PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
    }

    public static boolean isHide() {
//        return true;
        return PConfig.isEnable("app_hide", false);
    }

    public static void openUrl(String url, Context ctx) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        ctx.startActivity(intent);
    }

    public static int findId(String type, String name) {
        int id = 0;
        try {
            String cn = HookEntry.getPackageName() + ".R$" + type;
            PLog.d("尝试从: " + cn + " 中寻找 " + name);
            Class<?> clazz = HybridClassLoader.clLoader.loadClass(cn);
            id = XposedHelpers.getStaticIntField(clazz, name);
            PLog.d("查找结果: " + id);
        } catch (Throwable e) {
            PLog.e(e);
        }
        return id;
    }


    public static int getStatusBarHeight(Context ctx) {
        int result = 0;
        //获取状态栏高度的资源id
        int resourceId = ctx.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = ctx.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static boolean hasPermission(Context ctx, String... permissions) {
        boolean has = true;
        for (String perm : permissions) {
            if (ActivityCompat.checkSelfPermission(ctx, perm) != PackageManager.PERMISSION_GRANTED) {
                has = false;
                break;
            }
        }
        return has;
    }

    public static void requestPermission(Activity activity, int id, String... permissions) {
        PLog.d("尝试请求权限: " + Arrays.toString(permissions));
        ActivityCompat.requestPermissions(activity, permissions, id);
    }

    public static String getSettingActivity(String packageName) {
        String activity = "";
        if (packageName != null) {
            PLog.d("当前宿主应用为: " + packageName);
            if (packageName.equalsIgnoreCase(HostInfo.ZuiyouLite.PACKAGE_NAME)) {
                activity = "cn.xiaochuankeji.zuiyouLite.ui.setting.SettingActivity";
            } else if (packageName.equalsIgnoreCase(HostInfo.TieBa.PACKAGE_NAME)) {
                activity = "cn.xiaochuankeji.tieba.ui.home.setting.SettingActivity";
            } else if (packageName.equalsIgnoreCase(HostInfo.PPX.PACKAGE_NAME)) {
                activity = "com.sup.android.m_mine.view.SettingActivity";
            }
        } else {
            PLog.d("当前宿主应用为: null");
        }
        return activity;
    }

    public static float dp2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }

    public static boolean isUpdate() {
        int ver = PConfig.number(HookEntry.getPackageName() + ".version", 0);
        return ver == 0 || ver != getHostAppVersionCode();
    }

    private static int getHostAppVersionCode() {
        try {
            return AppUtils.getHostAppInstance().getPackageManager().getPackageInfo(HookEntry.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            PLog.e(e);
            return 0;
        }
    }

    public static void update() {
        PConfig.setNumber(HookEntry.getPackageName() + ".version", getHostAppVersionCode());
    }
}
