package cn.lliiooll.pphelper.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HideList;

import java.util.concurrent.atomic.AtomicInteger;

public class HidePostActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simpleme);
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
        // 沉浸式状态栏结束

        LinearLayout content = findViewById(R.id.sm_content);
        AtomicInteger q = new AtomicInteger(0);

        HideList.postTypes.forEach((k, v) -> {
            if (q.get() != 0) {
                View line = new View(this);
                line.setBackgroundResource(R.drawable.bg_line);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 3);
                line.setLayoutParams(lp);
                content.addView(line);
            }
            View root = LayoutInflater.from(this).inflate(R.layout.pp_sm_normal, null, false);
            TextView title = root.findViewById(R.id.sm_t);
            CheckBox checkBox = root.findViewById(R.id.sm_cb);
            title.setText(k);
            checkBox.setChecked(HideList.isHidePost(v));
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> HideList.hidePost(v, isChecked));
            content.addView(root);
            q.getAndIncrement();
        });


    }

}
