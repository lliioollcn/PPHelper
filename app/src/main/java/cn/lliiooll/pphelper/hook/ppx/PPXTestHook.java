package cn.lliiooll.pphelper.hook.ppx;

import android.os.Bundle;
import android.widget.Toast;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class PPXTestHook extends BaseHook {

    public static PPXTestHook INSTANCE = new PPXTestHook();

    public PPXTestHook() {
        super("测试Hook", "test");
    }

    @Override
    public boolean init() {
        XposedHelpers.findAndHookMethod("com.sup.superb.m_teenager.view.EnterTeenagerModeDialogActivity", HybridClassLoader.clLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Toast.makeText(AppUtils.getHostAppInstance(), "Hook成功", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }
}
