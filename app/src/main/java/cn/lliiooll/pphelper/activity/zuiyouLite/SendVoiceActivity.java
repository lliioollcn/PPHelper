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
import cn.lliiooll.pphelper.hook.zuiyouLite.AudioSend;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HideList;
import cn.lliiooll.pphelper.utils.PConfig;
import cn.lliiooll.pphelper.utils.PLog;

import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;

public class SendVoiceActivity extends Activity {

    private static final int REQUEST_CODE = 0x5c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendvoice);
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


        EditText time = findViewById(R.id.pp_a_voice_time);
        Button save = findViewById(R.id.pp_a_voice_save);
        LinearLayout path = findViewById(R.id.pp_a_voice_path);
        Switch sw = findViewById(R.id.pp_a_voice_switch);

        time.setText(PConfig.number("voiceTime", 51201314) + "");

        save.setOnClickListener(v -> {
            PConfig.setNumber("voiceTime", Integer.parseInt(time.getText().toString()));
            Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
        });

        path.setOnClickListener(v -> {
            Toast.makeText(this, "选择一个文件夹来读取语音文件", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE);
        });

        sw.setChecked(AudioSend.INSTANCE.isEnable());
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AudioSend.INSTANCE.setEnable(isChecked);
            }
        });

        if (PConfig.str("voicePath", null) == null) {
            Toast.makeText(this, "选择一个文件夹来读取语音文件", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            startActivityForResult(intent, REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (data != null) {
                Uri uri = data.getData();
                final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                        | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
                getContentResolver().takePersistableUriPermission(uri, takeFlags);
                PConfig.setStr("voicePath", uri.toString());
                PLog.d("保存成功");
            }
        }
    }
}
