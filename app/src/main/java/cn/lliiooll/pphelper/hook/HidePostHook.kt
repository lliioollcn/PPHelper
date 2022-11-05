package cn.lliiooll.pphelper.hook

import android.view.View
import android.view.ViewGroup
import cn.lliiooll.pphelper.utils.DexKit
import cn.lliiooll.pphelper.utils.PLog
import cn.lliiooll.pphelper.utils.PostTypes
import cn.lliiooll.pphelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object HidePostHook : BaseHook("hidePostEveryWhere", "隐藏对应类型帖子") {
    override fun init(): Boolean {
        this.desc = "隐藏对应类型帖子"
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val i = param?.args?.get(1) as Int
                //PLog.log("帖子类型: $i, 是否处于隐藏列表: ${PostTypes.isHide(i)}")
                if (PostTypes.isHide(i)) {
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
        }

        val clazz2 = "cn.xiaochuankeji.zuiyouLite.ui.recommend.RecommendListAdapter".loadClass()
        val clazz3 = "cn.xiaochuankeji.zuiyouLite.ui.recommend.RecommendTopicAdapter".loadClass()
        val clazz4 = "cn.xiaochuankeji.zuiyouLite.ui.recommend.IndexListAdapter".loadClass()
        XposedHelpers.findAndHookMethod(clazz2, "onCreateViewHolder", ViewGroup::class.java, DexKit.clazz_int, hook)
        XposedHelpers.findAndHookMethod(clazz3, "onCreateViewHolder", ViewGroup::class.java, DexKit.clazz_int, hook)
        XposedHelpers.findAndHookMethod(clazz4, "onCreateViewHolder", ViewGroup::class.java, DexKit.clazz_int, hook)
        return true
    }
}