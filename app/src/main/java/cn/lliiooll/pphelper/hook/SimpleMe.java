package cn.lliiooll.pphelper.hook;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import cn.lliiooll.pphelper.R;
import cn.lliiooll.pphelper.activity.SimpleMeActivity;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HideList;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;

public class SimpleMe extends BaseHook {

    public static SimpleMe INSTANCE = new SimpleMe();

    public SimpleMe() {
        super("精简\"我的\"界面", "simpleMe");
    }

    @Override
    public boolean init() {
        Class<?> clazz = HybridClassLoader.load("cn.xiaochuankeji.zuiyouLite.ui.me.FragmentMyTab");
        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equalsIgnoreCase("onCreateView")) {
                XposedBridge.hookMethod(m, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Object obj = param.thisObject;
                        ViewGroup root = (ViewGroup) param.getResult();
                        HideList.myTypes.forEach((k, v) -> {
                            if (HideList.isHideMy(v)) {
                                ViewGroup vg = null;
                                if (v.startsWith("!")) {
                                    if (!HideList.isHideMy("myTabDataLayout")) {
                                        ViewGroup vi = (ViewGroup) XposedHelpers.getObjectField(obj, "myTabDataLayout");
                                        vg = vi.findViewById(AppUtils.findId("id", v.replace("!", "")));
                                    }
                                } else if (v.contains("_")) {
                                    vg = root.findViewById(AppUtils.findId("id", v));
                                } else {
                                    vg = (ViewGroup) XposedHelpers.getObjectField(obj, v);
                                }

                                if (vg != null) {
                                    vg.clearAnimation();
                                    vg.removeAllViews();
                                    vg.setOnClickListener(null);
                                    vg.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                });
            }
        }
        return true;
    }

    @Override
    public View getSettingsView(Context ctx) {
        View root = LayoutInflater.from(ctx).inflate(R.layout.pp_setting_bar, null);
        TextView title = root.findViewById(R.id.set_nor_title);
        title.setText(getName());
        LinearLayout content = root.findViewById(R.id.pp_setting_bar_root);
        content.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, SimpleMeActivity.class);
            ctx.startActivity(intent);
        });
        return root;
    }
}
