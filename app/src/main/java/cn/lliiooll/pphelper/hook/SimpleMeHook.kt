package cn.lliiooll.pphelper.hook

import android.view.View
import android.view.ViewGroup
import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XposedHelpers

object SimpleMeHook : BaseHook("simple_me", "精简\"我的\"页面") {
    override fun init(): Boolean {
        this.desc = "精简\"我的\"页面"
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.me.FragmentMyTab".loadClass()
        clazz?.allMethod {
            PLog.log("hook my 方法: " + it.name)
            if (it.name == "onCreateView") {
                PLog.log("找到方法: " + it.name)
                it.hookAfter {
                    PLog.log("MyFragment创建视图")
                    val obj = it?.thisObject
                    val root = it?.result
                    HideList.myTypes.forEach { (name, key) ->
                        if (HideList.isHideMy(key)) {
                            var vg =
                                if (key.startsWith("!")) {
                                    if (!HideList.isHideMy("myTabDataLayout")) {
                                        val vi = XposedHelpers.getObjectField(obj, "myTabDataLayout") as ViewGroup
                                        ViewUtils.findId(key.replace("!", "")).findView(vi) as ViewGroup
                                    } else {
                                        null
                                    }
                                } else {
                                    (if (key.contains("_"))
                                        ViewUtils.findId(key).findView(root)
                                    else
                                        XposedHelpers.getObjectField(obj, key)) as ViewGroup

                                }
                            if (vg != null) {
                                vg.clearAnimation()
                                vg.removeAllViews()
                                vg.setOnClickListener { }
                                vg.visibility = View.GONE
                            }
                        }

                    }

                }
            } else if (name == "A0") {
                it.replace { }
            }
        }
        return true
    }

    override fun isEnable(): Boolean {
        return true
    }
}