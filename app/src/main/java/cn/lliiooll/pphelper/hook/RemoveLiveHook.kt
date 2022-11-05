package cn.lliiooll.pphelper.hook

import android.os.Bundle
import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.util.*

object RemoveLiveHook : BaseHook("remove_live", "去除直播") {
    override fun init(): Boolean {
        this.desc = "启用后将去除皮皮搞笑的直播功能"
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

        XposedHelpers.findAndHookMethod(
            "cn.xiaochuankeji.zuiyouLite.ui.slide.ActivitySlideDetail".loadClass(),
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    //val commentBeanClazz = "cn.xiaochuankeji.zuiyouLite.data.CommentBean".loadClass()
                    val postDataBeanClazz = "cn.xiaochuankeji.zuiyouLite.data.post.PostDataBean".loadClass()
                    val objClazz = param?.thisObject?.javaClass
                    for (field in objClazz?.declaredFields!!) {
                        if (field.type == postDataBeanClazz) {
                            val postDataBean = XposedHelpers.getObjectField(param.thisObject, field.name)
                            //PLog.log("liveCardItem: ${XposedHelpers.getObjectField(postDataBean,"liveCardItem")}")
                            //PLog.log(XposedHelpers.callStaticMethod("com.alibaba.fastjson.JSON".loadClass(),"toJSONString",postDataBean) as String)
                            val memberBean = XposedHelpers.getObjectField(postDataBean, "member")
                            XposedHelpers.setIntField(memberBean, "liveOn", 0)
                        }
                    }
                }
            })
        XposedHelpers.findAndHookMethod(
            "cn.xiaochuankeji.zuiyouLite.data.member.MemberInfoBean".loadClass(),
            "isLiveOn",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    XposedHelpers.setIntField(param?.thisObject, "liveOn", 0)
                    param?.result = false
                }
            })
        return true
    }
}