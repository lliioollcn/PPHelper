package cn.lliiooll.pphelper.hook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.CustomVideoRecordActivity;
import cn.lliiooll.pphelper.activity.SimpleMeActivity;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HideList;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;

public class CustomVideoRecord extends BaseHook {

    public static CustomVideoRecord INSTANCE = new CustomVideoRecord();

    public CustomVideoRecord() {
        super("自定义无水印视频保存路径", "customVideoRecord");
    }

    @Override
    public boolean init() {

        return true;
    }

    @Override
    public View getSettingsView(Context ctx) {
        View root = LayoutInflater.from(ctx).inflate(R.layout.pp_setting_bar, null);
        TextView title = root.findViewById(R.id.set_nor_title);
        title.setText(getName());
        LinearLayout content = root.findViewById(R.id.pp_setting_bar_root);
        content.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, CustomVideoRecordActivity.class);
            ctx.startActivity(intent);
        });
        return root;
    }
}
