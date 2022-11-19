package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.content.Context
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.utils.*
import java.util.*

object AntiHotfixHook : BaseHook("anti_hotfix", "禁用热补丁") {

    var OBF_HOTFIX_INIT = "Lcn/xiaochuankeji/zuiyouLite/common/robust/RobustStater"
    override fun init(): Boolean {
        this.desc = "禁用热补丁"
        if (ConfigManager.isFirst(this)) {
            ConfigManager.setEnable(this, false)
            ConfigManager.setFirst(this)
        }

        DexKit.load(OBF_HOTFIX_INIT).allMethod { it ->
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

    override fun doStep() {
        val result = DexKit.find(buildMap {
            put(OBF_HOTFIX_INIT, object : HashSet<String?>() {
                init {
                    add("event_on_load_hot_config_success")
                    add("app_config_json_parse")
                    add("local config cold/get json data parse failed.")
                }
            })
        })

        result.forEach { (key: String?, value: Array<String?>?) ->
            PLog.log("========================================")
            PLog.log("查找结果")
            PLog.log(key)
            PLog.log(Arrays.toString(value))
            PLog.log("========================================")
        }
        DexKit.cache(OBF_HOTFIX_INIT, result[OBF_HOTFIX_INIT]?.get(0))
    }

    override fun needStep(): Boolean {
        return !ConfigManager.hasCache(OBF_HOTFIX_INIT) || DexKit.load(OBF_HOTFIX_INIT) == null
    }
}