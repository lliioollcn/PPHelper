package cn.lliiooll.pphelper.hook.zuiyou;

import android.os.Bundle;
import android.widget.Toast;
import cn.lliiooll.pphelper.hook.BaseHook;
import cn.lliiooll.pphelper.utils.AppUtils;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class TestHook extends BaseHook {

    public static TestHook INSTANCE = new TestHook();

    public TestHook() {
        super("测试Hook", "test");
    }

    @Override
    public boolean init() {
        XposedHelpers.findAndHookMethod("cn.xiaochuankeji.tieba.ui.home.page.PageMainActivity", HybridClassLoader.clLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Toast.makeText(AppUtils.getHostAppInstance(), "Hook成功", Toast.LENGTH_SHORT).show();
            }
        });
        return true;
    }
}
