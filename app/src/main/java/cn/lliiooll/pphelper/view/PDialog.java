package cn.lliiooll.pphelper.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.AppUtils;

public class PDialog extends Dialog {

    private Runnable ok = null;
    private Runnable cancel = null;
    private String title = "标题";

    public PDialog(@NonNull Context context) {
        super(context);
    }

    public PDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected PDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public PDialog success(Runnable success) {
        this.ok = success;
        return this;
    }

    public PDialog cancel(Runnable cancel) {
        this.cancel = cancel;
        return this;
    }

    public PDialog title(String title) {
        this.title = title;
        return this;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_dialog_confirm);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
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
                layoutParams.width = (int) AppUtils.dp2px(getContext(), WindowManager.LayoutParams.MATCH_PARENT);
                break;
        }


        //设置显示位置
        layoutParams.gravity = Gravity.CENTER;
        //设置是否屏蔽返回键与点击空白区域不关闭Dialog
        setCancelable(true);
        //设置属性
        getWindow().setAttributes(layoutParams);

        Button ok = findViewById(R.id.pp_dialog_button_ok);
        Button cancel = findViewById(R.id.pp_dialog_button_back);
        TextView title = findViewById(R.id.pp_dialog_title);
        title.setText(this.title);
        if (this.ok != null) {
            ok.setOnClickListener(v -> {
                this.dismiss();
                this.ok.run();
            });
        }
        if (this.cancel != null) {
            cancel.setOnClickListener(v -> {
                this.dismiss();
                this.cancel.run();
            });
        }
    }
}
