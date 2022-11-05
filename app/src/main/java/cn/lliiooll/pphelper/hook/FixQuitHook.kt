package cn.lliiooll.pphelper.hook

import cn.lliiooll.pphelper.utils.DexKit
import cn.lliiooll.pphelper.utils.allMethod
import cn.lliiooll.pphelper.utils.hook
import de.robv.android.xposed.XC_MethodReplacement

object FixQuitHook : BaseHook("fix_quit", "修复闪退") {
    override fun init(): Boolean {
        this.desc = "修复闪退"
        DexKit.load(DexKit.OBF_CONFIG_PARSER).allMethod {
            if (it.name == "U") {
                it.hook(object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        return true
                    }

                })
            }
        }
        return true
    }

    override fun isEnable(): Boolean {
        return true
    }
}