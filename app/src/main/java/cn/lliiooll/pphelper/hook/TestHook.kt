package cn.lliiooll.pphelper.hook

import android.os.Bundle
import cn.lliiooll.pphelper.utils.Utils
import cn.xiaochuankeji.zuiyouLite.ui.main.MainActivity
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object TestHook : BaseHook("test", "测试hook") {
    override fun init(): Boolean {
        this.desc = "测试hook"
        XposedHelpers.findAndHookMethod(
            MainActivity::class.java,
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