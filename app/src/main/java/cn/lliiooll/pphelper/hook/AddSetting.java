package cn.lliiooll.pphelper.hook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import cn.lliiooll.pphelper.BuildConfig;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.ConfigActivity;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class AddSetting extends BaseHook {

    public static AddSetting INSTANCE = new AddSetting();

    public AddSetting() {
        super("添加设置", "addSetting");
    }

    @Override
    public boolean init() {
        Class<?> settingActivityClazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.ui.setting.SettingActivity");
        XposedHelpers.findAndHookMethod(settingActivityClazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity ctx = (Activity) param.thisObject;
                RelativeLayout root = ctx.findViewById(AppUtils.findId("id", "rootView"));
                ScrollView scrollView = (ScrollView) root.getChildAt(1);
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
                    AppUtils.getHostAppInstance().startActivity(intent);
                });
            }
        });
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
