package cn.lliiooll.pphelper.activity.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.config.ConfigManager;
import cn.lliiooll.pphelper.utils.Utils;

public class PPInputDialog extends Dialog {

    public PPInputDialog(@NonNull Context context) {
        super(context);
    }

    public PPInputDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PPInputDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_dialog_input);
        TextView title = findViewById(R.id.pp_dialog_title);
        TextView subtitle = findViewById(R.id.pp_dialog_subtitle);
        Button button = findViewById(R.id.pp_dialog_button);
        SeekBar seekBar = findViewById(R.id.pp_dialog_seekBar);
        final int[] tCount = {ConfigManager.getInt("pp_download_multi_thread_count", 3)};
        title.setText("修改下载线程数");
        subtitle.setText("当前线程数: " + tCount[0]);
        seekBar.setProgress(tCount[0]);
        seekBar.setMin(2);
        seekBar.setMax(20);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tCount[0] = progress;
                subtitle.setText("当前线程数: " + tCount[0]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        button.setOnClickListener(v -> {
            ConfigManager.setInt("pp_download_multi_thread_count", tCount[0]);
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
