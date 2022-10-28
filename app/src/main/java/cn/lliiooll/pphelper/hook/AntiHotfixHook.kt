package cn.lliiooll.pphelper.hook

import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.utils.DexKit
import cn.lliiooll.pphelper.utils.allMethod
import cn.lliiooll.pphelper.utils.replace

object AntiHotfixHook : BaseHook("anti_hotfix", "禁用热补丁") {
    override fun init(): Boolean {
        this.desc = "禁用热补丁"
        if (ConfigManager.isFirst(this)) {
            ConfigManager.setEnable(this, false)
            ConfigManager.setFirst(this)
        }

        DexKit.load(DexKit.OBF_HOTFIX_INIT).allMethod { it ->
            val m = it
            if (m.name == "F0") {
                it.replace {
                    it?.result = null
                    /*
                    val par = it
                    PLog.log("\n========================================")
                    PLog.log(
                        "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {},\n堆栈: ",
                        DexKit.OBF_HOTFIX_INIT,
                        m.name,
                        m.parameterCount,
                        Arrays.toString(m.parameterTypes),
                        StringBuilder().also {
                            for (obj in par?.args!!) {
                                PLog.log(JSONUtil.toJsonStr(obj))
                            }
                        }
                    )
                    PLog.printStacks()
                    PLog.log("========================================\n")

                     */
                }
            }
        }
        return true
    }
}