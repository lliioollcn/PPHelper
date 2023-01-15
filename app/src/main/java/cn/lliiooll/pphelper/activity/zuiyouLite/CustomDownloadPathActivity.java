package cn.lliiooll.pphelper.activity.zuiyouLite;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.config.PConfig;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HideList;
import cn.lliiooll.pphelper.utils.PLog;

import java.util.concurrent.atomic.AtomicInteger;

public class CustomDownloadPathActivity extends Activity {

    private static final int REQUEST_CODE_VIDEO = 0x6c;
    private static final int REQUEST_CODE_VOICE = 0x7c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_download);
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

        LinearLayout video = findViewById(R.id.pp_c_video_path);
        LinearLayout voice = findViewById(R.id.pp_c_voice_path);

        video.setOnClickListener(v -> {
            Toast.makeText(this, "选择一个文件夹来保存视频文件", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_VIDEO);
        });
        voice.setOnClickListener(v -> {
            Toast.makeText(this, "选择一个文件夹来保存语音文件", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE_VOICE);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_VIDEO) {
            if (data != null) {
                Uri uri = data.getData();
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                PConfig.setStr("saveVideoPath", uri.toString());
                PLog.d("视频路径保存成功");
            }
        }else if (requestCode == REQUEST_CODE_VOICE) {
            if (data != null) {
                Uri uri = data.getData();
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                PConfig.setStr("saveVoicePath", uri.toString());
                PLog.d("语音路径保存成功");
            }
        }
    }

}
