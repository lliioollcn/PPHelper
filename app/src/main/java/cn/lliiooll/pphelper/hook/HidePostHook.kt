package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import cn.lliiooll.pphelper.utils.*
import com.google.gson.GsonBuilder
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object HidePostHook : BaseHook("hidePostEveryWhere", "隐藏对应类型帖子") {
    override fun init(): Boolean {
        this.desc = "隐藏对应类型帖子"
        val hook = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                val i = param?.args?.get(1) as Int
                PLog.log("帖子类型: $i, 是否处于隐藏列表: ${HideList.isHidePost(i)}")
                if (HideList.isHidePost(i)) {
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
        XposedHelpers.findAndHookMethod(
            "cn.xiaochuankeji.zuiyouLite.ui.slide.ActivitySlideDetail".loadClass(),
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val act = param?.thisObject as Activity
                    for (e in act.intent?.extras?.keySet()!!) {
                        if (e == "key_post_data") {
                            val postData = act.intent.extras!!.get(e)
                            val o = XposedHelpers.getObjectField(postData, "activityBean")
                            if (o != null) {

                            }
                            PLog.json(GsonBuilder().serializeNulls().create().toJson(postData?.toMap()))
                        }
                    }
                }
            }
        )
        return true
    }

    override fun isEnable(): Boolean {
        return true
    }
}