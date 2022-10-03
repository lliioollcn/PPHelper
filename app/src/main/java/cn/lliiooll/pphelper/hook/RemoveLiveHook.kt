package cn.lliiooll.pphelper.hook

import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XposedHelpers
import java.util.*

object RemoveLiveHook : BaseHook("remove_live", "去除直播") {
    override fun init(): Boolean {
        this.desc = "启用后将关闭皮皮搞笑的直播"
        //val clazz = "cn.xiaochuankeji.live.bridge.Live".loadClass()
        //val clazz = "cn.xiaochuankeji.zuiyouLite.ui.main.MainActivity".loadClass()
        val clazz = "cn.xiaochuankeji.zuiyouLite.live.FragmentLiveSquare".loadClass()
        //val clazz = DexKit.load(DexKit.OBF_LIVE_MANAGER)
        for (m in clazz?.declaredMethods!!) {
            if (m.name == "refresh")
                m.replace {
                    PLog.log("\n========================================")
                    PLog.log(
                        "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {},\n返回值: {}\n堆栈: ",
                        clazz.name,
                        m.name,
                        m.parameterCount,
                        Arrays.toString(m.parameterTypes),
                        Arrays.toString(it?.args),
                        it?.result
                    )
                    PLog.printStacks()
                    PLog.log("========================================\n")
                    PLog.log("阻止加载")
                }
            /*
            m.hookAfter {

            }

             */


        }
        return true
    }
}