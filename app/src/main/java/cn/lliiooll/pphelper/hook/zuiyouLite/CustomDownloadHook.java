package cn.lliiooll.pphelper.hook.zuiyouLite;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.zuiyouLite.CustomDownloadPathActivity;
import cn.lliiooll.pphelper.hook.BaseHook;

public class CustomDownloadHook extends BaseHook {

    public static CustomDownloadHook INSTANCE = new CustomDownloadHook();

    public CustomDownloadHook() {
        super("自定义下载路径", "customDownload");
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }

    @Override
    public View getSettingsView(Context ctx) {
        View root = LayoutInflater.from(ctx).inflate(R.layout.pp_setting_bar, null);
        TextView title = root.findViewById(R.id.set_nor_title);
        title.setText(getName());
        LinearLayout content = root.findViewById(R.id.pp_setting_bar_root);
        content.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, CustomDownloadPathActivity.class);
            ctx.startActivity(intent);
        });
        return root;
    }
}
