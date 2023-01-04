package cn.lliiooll.pphelper.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HideList;
import cn.lliiooll.pphelper.utils.IOUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class LogActivity extends Activity {

    private static Handler handler = new Handler(Looper.getMainLooper());
    private TextView text = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        // 沉浸式状态栏
        getWindow().setStatusBarColor(Color.argb(0, 0, 0, 0));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        LinearLayout bar = findViewById(R.id.log_bar);
        View statusBarView = findViewById(R.id.log_statusBarView);
        ViewGroup.LayoutParams params = statusBarView.getLayoutParams();
        params.height += AppUtils.getStatusBarHeight(this);
        statusBarView.setLayoutParams(params);
        statusBarView.setBackground(bar.getBackground());
        ImageView back = findViewById(R.id.log_back);
        back.setOnClickListener(v -> onBackPressed());
        // 沉浸式状态栏结束

        this.text = findViewById(R.id.log_text);
        File dir = AppUtils.getHostAppInstance().getExternalFilesDir("helperLog");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "log.txt");

        ImageView save = findViewById(R.id.log_clear);
        save.setOnClickListener(v -> {

        });

        save.setOnLongClickListener(v -> {
            IOUtils.write("", file);
            Toast.makeText(this, "日志清理完毕", Toast.LENGTH_SHORT).show();
            return true;
        });
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                text.setText(file.exists() ? IOUtils.read(file) : "暂时没有日志");
                handler.postDelayed(this, 100L);
            }
        }, 100L);


    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }
}
