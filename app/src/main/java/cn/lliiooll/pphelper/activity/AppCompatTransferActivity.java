package cn.lliiooll.pphelper.activity;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.lifecycle.Parasitics;
import cn.lliiooll.pphelper.utils.HostInfo;
import cn.lliiooll.pphelper.utils.SavedInstanceStatePatchedClassReferencer;
import cn.xiaochuankeji.zuiyouLite.ui.base.BaseActivity;

public abstract class AppCompatTransferActivity extends BaseActivity {

    private ClassLoader mXref = null;

    @Override
    public ClassLoader getClassLoader() {
        if (mXref == null) {
            mXref = new SavedInstanceStatePatchedClassReferencer(
                    AppCompatTransferActivity.class.getClassLoader());
        }
        return mXref;
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        Bundle windowState = savedInstanceState.getBundle("android:viewHierarchyState");
        if (windowState != null) {
            windowState.setClassLoader(AppCompatTransferActivity.class.getClassLoader());
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    protected void requestTranslucentStatusBar() {
        Window window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        params.flags &= ~WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
        params.flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window.setAttributes(params);
        View decorView = window.getDecorView();
        int option = decorView.getSystemUiVisibility()
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);
    }

    protected int getNavigationBarLayoutInsect() {
        View decorView = getWindow().getDecorView();
        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION) == 0) {
            return 0;
        } else {
            WindowInsets insets = decorView.getRootWindowInsets();
            if (insets != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    return insets.getInsets(WindowInsets.Type.systemBars()).bottom;
                } else {
                    return insets.getStableInsetBottom();
                }
            } else {
                return 0;
            }
        }
    }

    protected int getStatusBarLayoutInsect() {
        View decorView = getWindow().getDecorView();
        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN) == 0) {
            return 0;
        } else {
            WindowInsets insets = decorView.getRootWindowInsets();
            if (insets != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    return insets.getInsets(WindowInsets.Type.systemBars()).top;
                } else {
                    return insets.getStableInsetTop();
                }
            } else {
                return 0;
            }
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        try {
            getString(R.string.res_inject_success);
        } catch (Resources.NotFoundException e) {
            // inject resources
            // TODO: 2022-03-20 either inject into ActivityThread$H or Instrumentation to do this
            if (HostInfo.isInHostProcess()) {
                Parasitics.injectModuleResources(getResources());
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
