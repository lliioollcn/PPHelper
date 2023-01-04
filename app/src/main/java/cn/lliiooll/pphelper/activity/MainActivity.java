package cn.lliiooll.pphelper.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.*;
import cn.lliiooll.pphelper.BuildConfig;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.startup.hookstatus.HookStatus;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.view.BlurRelativeLayout;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends Activity {

    private float lastY = 0;
    private boolean isSlide = false;
    private boolean isUp = false;
    private int radius = 0;

    private final String alias = "cn.lliiooll.pphelper.activity.MainActivityAlias";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 沉浸式状态栏
        getWindow().setStatusBarColor(Color.argb(0, 0, 0, 0));
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        LinearLayout bar = findViewById(R.id.main_bar);
        View statusBarView = findViewById(R.id.statusBarView);
        ViewGroup.LayoutParams params = statusBarView.getLayoutParams();
        params.height += AppUtils.getStatusBarHeight(this);
        statusBarView.setLayoutParams(params);
        statusBarView.setBackground(bar.getBackground());
        // 沉浸式状态栏结束

        TextView statusText = findViewById(R.id.status_text);
        ImageView statusIcon = findViewById(R.id.status_icon);
        if (HookStatus.isModuleEnabled()) {
            statusIcon.setBackgroundResource(R.drawable.ic_round_check);
            statusText.setText("模块已经正常启用，请按住箭头向上滑动获得更多信息");
        } else {
            statusIcon.setBackgroundResource(R.drawable.ic_round_close);
            statusText.setText("模块似乎没有正常激活。请在xposed管理器中启用本模块后重新尝试");
        }
        ImageView mainBarMore = findViewById(R.id.main_bar_more);
        bar.setWillNotDraw(false);
        mainBarMore.setOnClickListener(v -> {
            try {
                Animation animation = AnimationUtils.loadAnimation(this, R.anim.main_more_rotate_anim);
                animation.setFillAfter(true);
                animation.setFillEnabled(true);
                v.setAnimation(animation);
                animation.startNow();
                bar.invalidate();
                PopupMenu menu = new PopupMenu(this, v);
                menu.getMenu().add(AppUtils.isHide() ? "显示桌面图标" : "隐藏桌面图标");
                PLog.d("是否隐藏: " + AppUtils.isHide());
                menu.setOnMenuItemClickListener(item -> {
                    AppUtils.hideIcon(this, alias);
                    return false;
                });
                menu.setOnDismissListener(menu1 -> {
                    Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.main_more_rotate_anim_back);
                    animation1.setFillAfter(true);
                    animation1.setFillEnabled(true);
                    v.setAnimation(animation1);
                    animation1.startNow();
                    bar.invalidate();
                });
                menu.show();
            } catch (Throwable e) {
                PLog.e(e);
            }
        });

        LinearLayout mainInfo = findViewById(R.id.main_info);
        ImageView mainInfoSlide = findViewById(R.id.main_info_slide);
        BlurRelativeLayout statusInfo = findViewById(R.id.status_info);

        float srcY = mainInfo.getY();
        int srcH = mainInfo.getLayoutParams().height;
        final int[] lastH = {mainInfo.getLayoutParams().height};
        float maxY = Resources.getSystem().getDisplayMetrics().ydpi;
        int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

        mainInfoSlide.setOnTouchListener((v, event) -> {
            if (lastY == 0) lastY = event.getRawY();
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // TODO: 手指按下
                isSlide = true;
                lastY = event.getRawY();
                lastH[0] = mainInfo.getLayoutParams().height;
            }
            float slide = event.getRawY() - lastY;
            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                ViewGroup.LayoutParams layoutParams = mainInfo.getLayoutParams();
                layoutParams.height = lastH[0] - ((int) slide);
                statusInfo.setBlur(25);
                mainInfo.setLayoutParams(layoutParams);
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                // TODO: 手指抬起
                ViewGroup.LayoutParams layoutParams = mainInfo.getLayoutParams();
                if (event.getRawY() < srcY || event.getRawY() < (maxY / 2) || mainInfo.getLayoutParams().height < srcH) {
                    layoutParams.height = srcH;
                    if (isUp) {
                        Animation animation = AnimationUtils.loadAnimation(this, R.anim.main_info_rotate_anim_back);
                        animation.setFillAfter(true);
                        animation.setFillEnabled(true);
                        mainInfoSlide.setAnimation(animation);
                        animation.startNow();
                    }
                    statusInfo.setBlur(0);
                    isUp = false;
                } else {
                    layoutParams.height = (int) (screenHeight - getResources().getDimension(R.dimen.barHeight));
                    if (!isUp) {
                        Animation animation = AnimationUtils.loadAnimation(this, R.anim.main_info_rotate_anim);
                        animation.setFillAfter(true);
                        animation.setFillEnabled(true);
                        mainInfoSlide.setAnimation(animation);
                        animation.startNow();
                    }
                    statusInfo.setBlur(25);
                    isUp = true;
                }
                mainInfo.setLayoutParams(layoutParams);
                isSlide = false;
            }
            return true;
        });


        //文字信息
        TextView version = findViewById(R.id.main_info_version);
        TextView time = findViewById(R.id.main_info_time);
        TextView chat = findViewById(R.id.main_info_chat);
        TextView code = findViewById(R.id.main_info_code);
        TextView eula = findViewById(R.id.main_info_eula);
        TextView setting = findViewById(R.id.main_info_setting);
        version.setText("模块版本: " + BuildConfig.VERSION_NAME);
        time.setText("构建时间: " + new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss").format(new Date(BuildConfig.BUILD_TIMESTAMP)));
        chat.setOnClickListener(v -> AppUtils.openUrl("https://pd.qq.com/s/iflagazq", this));
        code.setOnClickListener(v -> AppUtils.openUrl("https://github.com/lliioollcn/PPHelper", this));
        eula.setOnClickListener(v -> AppUtils.openUrl("https://github.com/qwq233/License/blob/master/v2/LICENSE.md", this));
        setting.setOnClickListener(v -> {
            //TODO: 跳转皮皮搞笑设置界面
        });

        chat.setOnLongClickListener(v -> {
            AlertDialog.Builder d = new AlertDialog.Builder(this);
            d.setTitle("截图扫码加入频道");
            ImageView img = new ImageView(this);
            img.setBackgroundResource(R.mipmap.qq);
            d.setView(img);
            d.setPositiveButton("确定", (dialog, which) -> {

            });
            d.show();
            return true;
        });

        LinearLayout main_info_dexkit = findViewById(R.id.main_info_dexkit);
        LinearLayout main_info_qa = findViewById(R.id.main_info_qa);
        LinearLayout main_info_xd = findViewById(R.id.main_info_xd);
        main_info_dexkit.setOnClickListener(v -> AppUtils.openUrl("https://github.com/LuckyPray/DexKit", this));
        main_info_qa.setOnClickListener(v -> AppUtils.openUrl("https://github.com/cinit/QAuxiliary", this));
        main_info_xd.setOnClickListener(v -> AppUtils.openUrl("https://github.com/LuckyPray/XAutoDaily", this));
    }

}
