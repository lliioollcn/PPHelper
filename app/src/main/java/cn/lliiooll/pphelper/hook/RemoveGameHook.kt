package cn.lliiooll.pphelper.hook

import android.view.View
import android.view.ViewGroup
import cn.lliiooll.pphelper.utils.DexKit
import cn.lliiooll.pphelper.utils.hookAfter
import cn.lliiooll.pphelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object RemoveGameHook : BaseHook("removeGameCenter", "移除首页推荐小游戏") {
    override fun init(): Boolean {
        this.desc = "移除首页推荐小游戏"
        val clazz2 = "cn.xiaochuankeji.zuiyouLite.ui.recommend.RecommendListAdapter".loadClass()
        XposedHelpers.findAndHookMethod(
            clazz2,
            "onCreateViewHolder",
            ViewGroup::class.java,
            DexKit.clazz_int,
            object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    val i = param?.args?.get(1) as Int
                    if (i == 216) {
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