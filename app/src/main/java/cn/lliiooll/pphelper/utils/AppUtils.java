package cn.lliiooll.pphelper.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import androidx.core.app.ActivityCompat;
import cn.lliiooll.pphelper.activity.LogActivity;
import cn.lliiooll.pphelper.activity.MainActivity;
import cn.lliiooll.pphelper.activity.SimpleMeActivity;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Field;
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
            Class<?> clazz = HybridClassLoader.clLoader.loadClass("cn.xiaochuankeji.zuiyouLite.R$" + type);
            id = XposedHelpers.getStaticIntField(clazz, name);
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
}
