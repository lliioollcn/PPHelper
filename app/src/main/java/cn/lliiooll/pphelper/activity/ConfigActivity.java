package cn.lliiooll.pphelper.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.hook.HookBus;
import cn.lliiooll.pphelper.utils.AppUtils;

public class ConfigActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        // 沉浸式状态栏
        getWindow().setStatusBarColor(Color.argb(0, 0, 0, 0));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        LinearLayout bar = findViewById(R.id.set_bar);
        View statusBarView = findViewById(R.id.set_statusBarView);
        ViewGroup.LayoutParams params = statusBarView.getLayoutParams();
        params.height += AppUtils.getStatusBarHeight(this);
        statusBarView.setLayoutParams(params);
        statusBarView.setBackground(bar.getBackground());
        // 沉浸式状态栏结束

        LinearLayout content = findViewById(R.id.set_content);
        int q = 0;
        for (int i = 0; i < HookBus.getAllHooks().size(); i++) {
            BaseHook h = HookBus.getAllHooks().get(i);
            View hv = h.getSettingsView(this);
            if (hv != null) {
                if (q != 0) {
                    View line = new View(this);
                    line.setBackgroundResource(R.drawable.bg_line);
                    ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
                    line.setLayoutParams(lp);
                    content.addView(line);
                }
                content.addView(hv);
                q++;
            }
        }
    }

}
