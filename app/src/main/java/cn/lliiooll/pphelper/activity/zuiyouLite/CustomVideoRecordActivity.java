package cn.lliiooll.pphelper.activity.zuiyouLite;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.IOUtils;
import cn.lliiooll.pphelper.utils.PLog;

import java.io.File;
import java.util.Arrays;

public class CustomVideoRecordActivity extends Activity {

    private static final int REQUEST_CODE = 0x3c;
    private static final int REQUEST_CODE_EX = 0x3d;
    private static boolean hasPermission = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        // 沉浸式状态栏
        getWindow().setStatusBarColor(Color.argb(0, 0, 0, 0));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        LinearLayout bar = findViewById(R.id.sm_bar);
        View statusBarView = findViewById(R.id.sm_statusBarView);
        ViewGroup.LayoutParams params = statusBarView.getLayoutParams();
        params.height += AppUtils.getStatusBarHeight(this);
        statusBarView.setLayoutParams(params);
        statusBarView.setBackground(bar.getBackground());
        ImageView back = findViewById(R.id.set_back);
        back.setOnClickListener(v -> onBackPressed());
        // 沉浸式状态栏结束

        if (!AppUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            PLog.d("没有权限，尝试请求外部储存权限...");
            hasPermission = false;
            AppUtils.requestPermission(this, REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        } else {
            hasPermission = true;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!AppUtils.hasPermission(this, Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
                PLog.d("没有权限，尝试请求外部储存权限...");
                hasPermission = false;
                AppUtils.requestPermission(this, REQUEST_CODE_EX, Manifest.permission.MANAGE_EXTERNAL_STORAGE);
            } else {
                hasPermission = true;
            }
            if (!Environment.isExternalStorageManager()) {
                PLog.d("没有权限，尝试请求管理全部文件权限...");
                hasPermission = false;
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + AppUtils.getHostAppInstance().getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            } else {
                hasPermission = true;
            }
        }

        Button btn = findViewById(R.id.test_button);
        btn.setOnClickListener(v -> {
            File storage = Environment.getExternalStorageDirectory();
            File sDir = new File(storage, "PPHelper");
            if (!sDir.exists()) {
                PLog.d("创建文件夹: " + sDir.mkdirs());
            }
            File file = new File(sDir, "test.txt");
            if (!file.exists()) {
                PLog.d("创建文件: " + file.getAbsolutePath());
                IOUtils.write("test", file);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                PLog.d("全部文件管理权限请求成功");
                hasPermission = true;
            } else {
                PLog.d("全部文件管理权限请求失败");
                Toast.makeText(this, "没有权限访问外部储存", Toast.LENGTH_SHORT).show();
                hasPermission = false;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PLog.d("权限请求返回: code:" + requestCode + " 请求权限:" + Arrays.toString(permissions));
        if (requestCode == REQUEST_CODE) {
            if (AppUtils.hasPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                PLog.d("请求成功");
                hasPermission = true;
            } else {
                PLog.d("请求失败");
                Toast.makeText(this, "没有权限访问外部储存", Toast.LENGTH_SHORT).show();
                hasPermission = false;
            }
        }
        if (requestCode == REQUEST_CODE_EX && Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                PLog.d("没有权限，尝试请求管理全部文件权限...");
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + AppUtils.getHostAppInstance().getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }
}
