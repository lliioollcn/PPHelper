package cn.lliiooll.pphelper.hook;

import android.os.Handler;
import cn.lliiooll.pphelper.utils.HybridClassLoader;
import cn.lliiooll.pphelper.utils.PLog;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

import java.lang.reflect.Method;

public class AddSetting extends BaseHook {

    public static AddSetting INSTANCE = new AddSetting();

    public AddSetting() {
        super("添加设置", "addSetting");
    }

    @Override
    public boolean init() {

        return true;
    }

    @Override
    public boolean isEnable() {
        return true;
    }
}
