package cn.lliiooll.pphelper.hook

import android.view.View
import android.view.ViewGroup
import cn.lliiooll.pphelper.utils.DexKit
import cn.lliiooll.pphelper.utils.hookAfter
import cn.lliiooll.pphelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object RemoveVoiceRoomHook : BaseHook("removeVoiceRoom", "移除语音房") {
    override fun init(): Boolean {
        this.desc = "启用后将禁用语音房"
        val clazz1 = "com.youyisia.voices.sdk.api.HYVoiceRoomSdk".loadClass()
        for (m in clazz1?.declaredMethods!!) {
            m.hookAfter {
                if (m.name == "isInited") {
                    it?.result = true
                }
                /*
                PLog.log("========================================\n")
                PLog.log(
                    "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {}\n当前堆栈: ",
                    clazz1.name,
                    m.name,
                    m.parameterTypes.size,
                    Arrays.toString(m.parameterTypes),
                    Arrays.toString(it?.args)
                )
                //PLog.printStacks()
                PLog.log("========================================\n")

                 */
            }
        }
        return true
    }
}