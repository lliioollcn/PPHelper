package cn.lliiooll.pphelper.hook

import android.os.Bundle
import cn.lliiooll.pphelper.utils.PLog
import cn.lliiooll.pphelper.utils.showLongToast
import cn.xiaochuankeji.zuiyouLite.ui.media.VideoFragment
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object NoMarkHook : BaseHook("no_mark") {
    override fun init(): Boolean {
        XposedHelpers.findAndHookMethod(VideoFragment::class.java,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val sb = StringBuilder();
                    for (field in param?.thisObject?.javaClass?.declaredFields!!) {
                        sb.append(field.name)
                            .append(XposedHelpers.getObjectField(param.thisObject, field.name))
                            .append("\n")
                    }
                    sb.toString().showLongToast()
                    PLog.log(sb.toString())
                }
            })
        return true
    }
}