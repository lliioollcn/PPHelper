package cn.lliiooll.pphelper.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionUtils {

    private static String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.SYSTEM_ALERT_WINDOW,
    };


    /**
     * @return 应用是否有全部权限
     */
    public static boolean checkPermissions(Context ctx) {
        boolean checked = true;
        for (String permission : permissions) {
            checked = ContextCompat.checkSelfPermission(ctx, permission) == PackageManager.PERMISSION_GRANTED;
        }
        return checked;
    }

    /**
     * 请求权限
     */
    public static void requirePermissions(Activity activity, int id) {
        ActivityCompat.requestPermissions(activity, permissions, id);
    }
}
