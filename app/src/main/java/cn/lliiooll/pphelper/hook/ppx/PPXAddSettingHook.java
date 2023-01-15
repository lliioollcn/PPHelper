package cn.lliiooll.pphelper.hook.ppx;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import cn.lliiooll.pphelper.BuildConfig;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.zuiyouLite.ConfigActivity;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PPXAddSettingHook extends BaseHook {

    public static PPXAddSettingHook INSTANCE = new PPXAddSettingHook();

    public PPXAddSettingHook() {
        super("添加设置", PPXAddSettingHook.class.getSimpleName());
    }

    @Override
    public boolean init() {
        Class<?> clazz = HybridClassLoader.load("com.sup.android.m_mine.view.SettingFragment");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase("onCreateView")) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        PLog.d("创建了设置界面");
                        Object ins = param.thisObject;
                        Activity ctx = (Activity) XposedHelpers.callMethod(ins, "getActivity");
                        ViewGroup rootView = (ViewGroup) param.getResult();
                        ScrollView scrollView = (ScrollView) rootView.getChildAt(1);
                        LinearLayout content = (LinearLayout) scrollView.getChildAt(0);
                        // 初始化设置
                        View ppRoot = LayoutInflater.from(ctx).inflate(R.layout.pp_setting, null, false);
                        TextView version = ppRoot.findViewById(R.id.pp_setting_version);
                        version.setText(BuildConfig.VERSION_NAME);
                        content.addView(ppRoot, 0);
                        View ppLayout = ppRoot.findViewById(R.id.pp_setting_root);
                        ppLayout.setOnClickListener(v -> {
                            PLog.d("尝试跳转到ConfigActivity...");
                            Intent intent = new Intent(ctx, ConfigActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            ctx.startActivity(intent);
                        });
                    }
                });
            }
        }
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public View getSettingsView(Context ctx) {
        return null;
    }
}
