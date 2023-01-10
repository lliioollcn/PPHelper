package cn.lliiooll.pphelper.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import cn.lliiooll.pphelper.R;

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
