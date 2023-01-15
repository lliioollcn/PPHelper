package cn.lliiooll.pphelper.hook.zuiyouLite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.*;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.zuiyouLite.SendVoiceActivity;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.config.PConfig;
import cn.lliiooll.pphelper.view.FloatingViewTouch;
import cn.lliiooll.pphelper.view.PDialogVoice;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class AudioSend extends BaseHook {

    public static AudioSend INSTANCE = new AudioSend();
    private static int REQUEST_CODE = 0x4c;
    private static ImageView imageView;
    private static PDialogVoice dialog;

    public AudioSend() {
        super("自定义语音发送", "audioSend");
    }

    @Override
    public boolean init() {

        Class<?> clazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.ui.input.ActivityInputReview");
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                if (!Settings.canDrawOverlays(activity)) {
                    Toast.makeText(activity, "请开启皮皮搞笑的悬浮窗权限来使用此功能", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    intent.setData(Uri.parse("package:" + AppUtils.getHostAppInstance().getPackageName()));
                    activity.startActivityForResult(intent, REQUEST_CODE);
                } else {
                    initOverlay(activity);
                }
            }
        });

        XposedHelpers.findAndHookMethod(clazz,
                "onActivityResult",
                int.class,
                int.class,
                Intent.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        int req = (int) param.args[0];
                        Activity activity = (Activity) param.thisObject;
                        if (req == REQUEST_CODE) {
                            if (!Settings.canDrawOverlays(activity)) {
                                Toast.makeText(activity, "请开启皮皮搞笑的悬浮窗权限来使用此功能", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                                intent.setData(Uri.parse("package:" + AppUtils.getHostAppInstance().getPackageName()));
                                activity.startActivityForResult(intent, REQUEST_CODE);
                            } else {
                                initOverlay(activity);
                            }
                        }

                    }
                });
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Activity activity = (Activity) param.thisObject;
                if (imageView != null) {
                    activity.getWindowManager().removeView(imageView);
                    imageView = null;
                }

                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
            }
        };


        XposedHelpers.findAndHookMethod(clazz, "onDestroy", hook);
        //XposedHelpers.findAndHookMethod(clazz, "onStop", hook);

        /*

        for (Method m : clazz.getDeclaredMethods()) {

            XposedBridge.hookMethod(m, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Activity activity = (Activity) param.thisObject;
                    PLog.d("调用了方法: " + m.getName());

                    PLog.printStacks();

                }
            });

        }


         */

        return true;
    }

    private void initOverlay(Activity activity) {
        if (imageView == null) {
            imageView = new ImageView(activity);
            imageView.setBackgroundResource(R.drawable.ic_voice);
            imageView.setOnClickListener(v -> {

                String path = PConfig.str("voicePath", null);
                if (path == null) {
                    Toast.makeText(activity, "请先到设置界面选择语音读取路径后继续", Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = Uri.parse(path);
                    if (!activity.isDestroyed() && !activity.isFinishing()) {
                        if (dialog == null) {
                            dialog = new PDialogVoice(activity);
                        }
                        dialog.uri(uri).show();
                    }
                }
            });
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(120, 120, 0, 0, PixelFormat.TRANSPARENT);
            lp.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            lp.format = PixelFormat.RGBA_8888;
            lp.x = (activity.getWindowManager().getDefaultDisplay().getWidth() / 2) - 20;
            lp.y = 0;
            imageView.setOnTouchListener(new FloatingViewTouch(lp, activity.getWindowManager()));
            activity.getWindowManager().addView(imageView, lp);
        }
    }

    @Override
    public View getSettingsView(Context ctx) {
        View root = LayoutInflater.from(ctx).inflate(R.layout.pp_setting_bar, null);
        TextView title = root.findViewById(R.id.set_nor_title);
        title.setText(getName());
        LinearLayout content = root.findViewById(R.id.pp_setting_bar_root);
        content.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, SendVoiceActivity.class);
            ctx.startActivity(intent);
        });
        return root;
    }
}
