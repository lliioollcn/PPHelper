package cn.lliiooll.pphelper.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.config.ConfigManager;
import cn.lliiooll.pphelper.utils.Utils;

public class PPInputDialog extends Dialog {
    private int a = 0;

    public PPInputDialog(@NonNull Context context) {
        super(context);
    }

    public PPInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PPInputDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public PPInputDialog setA(int a) {
        this.a = a;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (a == 0) {
            setContentView(R.layout.pp_dialog_input);
        } else {
            setContentView(R.layout.pp_dialog_number);
        }

        TextView title = findViewById(R.id.pp_dialog_title);
        TextView subtitle = findViewById(R.id.pp_dialog_subtitle);
        Button button = findViewById(R.id.pp_dialog_button);
        final int[] tCount = {a == 0 ? ConfigManager.getInt("pp_download_multi_thread_count", 3) : ConfigManager.getInt("pp_play_voice_time", 1)};

        if (a == 0) {
            SeekBar seekBar = findViewById(R.id.pp_dialog_seekBar);
            seekBar.setProgress(tCount[0]);
            seekBar.setMin(2);
            seekBar.setMax(20);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    tCount[0] = progress;
                    subtitle.setText((a == 0 ? "当前线程数: " : "当前自定义语音时间: ") + tCount[0]);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        } else {
            EditText text = findViewById(R.id.input_text);
            text.setText(tCount[0] + "");
            text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    button.callOnClick();
                    tCount[0] = Integer.parseInt(text.getText().toString());
                    return true;
                }
            });
        }
        title.setText(a == 0 ? "修改下载线程数" : "修改自定义语音时间");
        subtitle.setText((a == 0 ? "当前线程数: " : "当前自定义语音时间: ") + tCount[0]);

        button.setOnClickListener(v -> {
            if (a != 0) {
                EditText text = findViewById(R.id.input_text);
                tCount[0] = Integer.parseInt(text.getText().toString());
            }
            ConfigManager.setInt(a == 0 ? "pp_download_multi_thread_count" : "pp_play_voice_time", tCount[0]);
            this.hide();
            Toast.makeText(Utils.getApplication(), "保存完毕", Toast.LENGTH_SHORT).show();
        });

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        //设置宽
        switch (WindowManager.LayoutParams.MATCH_PARENT) {
            case WindowManager.LayoutParams.MATCH_PARENT:
                layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                break;
            case WindowManager.LayoutParams.WRAP_CONTENT:
                layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
                break;
            default:
                layoutParams.width = (int) dp2px(getContext(), WindowManager.LayoutParams.MATCH_PARENT);
                break;
        }


        //设置显示位置
        layoutParams.gravity = Gravity.CENTER;
        //设置是否屏蔽返回键与点击空白区域不关闭Dialog
        setCancelable(true);
        //设置属性
        getWindow().setAttributes(layoutParams);
    }

    public float dp2px(Context context, int dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (dpValue * scale + 0.5f);
    }


}
