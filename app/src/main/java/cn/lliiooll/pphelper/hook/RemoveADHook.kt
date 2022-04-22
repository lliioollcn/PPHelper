package cn.lliiooll.pphelper.hook

import android.os.Handler
import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import java.util.*

object RemoveADHook : BaseHook("remove_ad", "去广告") {
    override fun init(): Boolean {
        this.desc = "启用后将去除应用内所有广告"
        val hermesC = "cn.xiaochuankeji.hermes.core.Hermes".loadClass()
        for (m in hermesC?.declaredMethods!!) {
            if (m.name.contains("create") && m.name.contains("AD")) {
                PLog.log("方法: {},{}", m.name, m.parameterTypes)
                m.hook(object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        val sb = StringBuilder()
                        sb.append("\n方法: ").append(param?.method?.name).append("\n参数: ")
                        for (arg in param?.args!!) {
                            if (!Objects.isNull(arg)) {
                                sb.append(arg.javaClass.name)
                            } else {
                                sb.append("null")
                            }
                            sb.append(",")
                        }
                        sb.append("\n返回值: ")
                        if (!Objects.isNull(param.result)) {
                            sb.append(param.result.javaClass.name)
                        } else {
                            sb.append("null")
                        }
                        PLog.log(sb.toString())
                    }
                })
            }

        }
        XposedHelpers.findAndHookMethod(
            Handler::class.java,
            "sendEmptyMessageDelayed",
            Int::class.java,
            Long::class.java,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val what: Int = param?.args?.get(0) as Int
                    if (what == 29) {
                        PLog.log("空信道消息: 29")
                        param.args[1] = 1000L
                    }
                }
            })
        return true
    }
}