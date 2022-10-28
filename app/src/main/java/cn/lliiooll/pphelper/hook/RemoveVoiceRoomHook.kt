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
        this.desc = "移除语音房"
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
        val clazz2 = "cn.xiaochuankeji.zuiyouLite.ui.recommend.RecommendListAdapter".loadClass()
        XposedHelpers.findAndHookMethod(
            clazz2,
            "onCreateViewHolder",
            ViewGroup::class.java,
            DexKit.clazz_int,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val i = param?.args?.get(1) as Int
                    if (i == 212 || i == 214) {
                        param.args[1] = 666999
                    }
                }

                override fun afterHookedMethod(param: MethodHookParam?) {
                    val i = param?.args?.get(1) as Int
                    if (i == 666999) {
                        val view = XposedHelpers.getObjectField(param.result, "itemView") as View
                        val layoutParams = view.layoutParams
                        layoutParams.width = 0
                        layoutParams.height = 0
                        view.setPadding(0, 0, 0, 0)
                        view.setOnClickListener { }
                        view.layoutParams = layoutParams
                        view.visibility = View.GONE
                    }
                }
            })
        return true
    }
}