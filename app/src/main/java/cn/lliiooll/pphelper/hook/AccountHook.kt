package cn.lliiooll.pphelper.hook

import cn.lliiooll.pphelper.utils.DexKit
import cn.lliiooll.pphelper.utils.PLog
import cn.lliiooll.pphelper.utils.hook
import de.robv.android.xposed.XC_MethodHook
import java.util.*

object AccountHook : BaseHook("account", "账号hook") {
    override fun init(): Boolean {
        this.desc = "账号hook"
        val clazz = DexKit.load(DexKit.OBF_ACCOUNT_SERVICE_MANAGER)
        for (m in clazz?.declaredMethods!!) {
            m.hook(object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    PLog.log("\n========================================")
                    PLog.log(
                        "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {}\n当前堆栈: ",
                        param?.thisObject?.javaClass?.simpleName,
                        m.name,
                        m.parameterTypes.size,
                        Arrays.toString(m.parameterTypes),
                        Arrays.toString(param?.args)
                    )
                    PLog.printStacks()
                    PLog.log("========================================\n")
                }
            })
        }
        return true
    }
}