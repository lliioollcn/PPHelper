package cn.lliiooll.pphelper.hook

import android.os.Bundle
import cn.lliiooll.pphelper.utils.Utils
import cn.lliiooll.pphelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

object RemoveVoiceRoomHook : BaseHook("removeVoiceRoom", "移除语音房") {
    override fun init(): Boolean {
        this.desc = "移除语音房"
        XposedHelpers.findAndHookMethod(
            "cn.xiaochuankeji.chat.gui.base.ChatBaseActivity".loadClass(),
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    Utils.showShort("")
                }
            })
        return true
    }
}