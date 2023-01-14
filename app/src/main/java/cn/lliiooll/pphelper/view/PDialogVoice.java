package cn.lliiooll.pphelper.view;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.documentfile.provider.DocumentFile;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.utils.*;
import de.robv.android.xposed.XposedHelpers;

import java.io.File;
import java.lang.reflect.Field;

public class PDialogVoice extends Dialog {

    private final Activity activity;
    private Uri uri;

    public PDialogVoice(@NonNull Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pp_dialog_voice);
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
        ImageView close = findViewById(R.id.pp_voice_close);
        ImageView music = findViewById(R.id.pp_voice_music);
        close.setOnClickListener(v -> dismiss());
        music.setOnClickListener(v -> Toast.makeText(activity, "不要瞎几把点，没写完呢", Toast.LENGTH_SHORT).show());

        LinearLayout list = findViewById(R.id.pp_voice_list);

        addFiles(list, uri);

    }

    private void addFiles(LinearLayout list, Uri uri) {
        SyncUtils.async(() -> {
            DocumentFile dir = DocumentFile.fromTreeUri(getContext(), uri);
            DocumentFile[] files = dir.listFiles();
            PLog.d("文件个数: " + files.length + " @" + dir.getUri());
            for (DocumentFile file : files) {
                PLog.d("文件路径@" + file.canRead() + ": " + file.getUri());
                if (file.isDirectory()) {
                    addFiles(list, uri);
                } else {
                    SyncUtils.sync(() -> {
                        TextView text = new TextView(getContext());
                        text.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        text.setText(file.getName());
                        text.setTextColor(Color.BLACK);
                        text.setOnClickListener(v -> {
                            File tempDir = activity.getExternalFilesDir("helperVoiceTemp");
                            if (!tempDir.exists()) {
                                tempDir.mkdirs();
                            }
                            File tempFile = new File(tempDir, file.getName());
                            PLog.d("复制到文件: " + tempFile.getAbsolutePath());
                            IOUtils.copy(activity, file.getUri(), tempFile);
                            PLog.d("复制完毕: " + tempFile.length());
                            Class<?> clazz = activity.getClass();
                            for (Field f : clazz.getDeclaredFields()) {
                                if (f.getType().getName().contains("AudioBean")) {
                                    XposedHelpers.setObjectField(activity, f.getName(), AudioBuilder.build(tempFile.getAbsolutePath(), null, PConfig.number("voiceTime", 5201314)));
                                    PLog.d("语音设置成功，准备发送");
                                    XposedHelpers.callMethod(activity, "M1");
                                    PLog.d("语音发送成功: M1");

                                    /*
                                    for (Method m : clazz.getDeclaredMethods()) {
                                        if (m.getReturnType() == void.class && !Modifier.isStatic(m.getModifiers()) && m.getParameterTypes().length == 0) {
                                            XposedHelpers.callMethod(activity, m.getName());
                                            PLog.d("语音发送成功: " + m.getName());
                                            dismiss();
                                        }
                                    }

                                     */
                                    break;
                                }
                            }

                        });
                        text.setPadding(0, 10, 0, 10);
                        list.addView(text);
                    });
                }
            }
        });
    }

    public PDialogVoice uri(Uri uri) {
        this.uri = uri;
        return this;
    }
}
