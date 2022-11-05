package cn.lliiooll.pphelper.hook

import android.os.Bundle
import cn.lliiooll.pphelper.utils.CliOper
import cn.lliiooll.pphelper.utils.Utils
import cn.lliiooll.pphelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object TestHook : BaseHook("test", "测试hook") {
    override fun init(): Boolean {
        this.desc = "测试hook"
        XposedHelpers.findAndHookMethod(
            "com.tencent.bugly.crashreport.CrashReport".loadClass(),
            "setUserId",
            String::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    //Utils.showShort("用户id: " + param.args[0])
                    CliOper.init(Utils.getApplication(), param.args[0] as String?)
                }
            })
        XposedHelpers.findAndHookMethod(
           "cn.xiaochuankeji.zuiyouLite.ui.main.MainActivity".loadClass(),
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    Utils.showShort("hook成功")
                }
            })
        return true
    }
}