package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import cn.lliiooll.pphelper.BuildConfig
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.activity.SettingsActivity
import cn.lliiooll.pphelper.utils.Utils
import cn.lliiooll.pphelper.utils.findView
import cn.lliiooll.pphelper.utils.loadClass
import cn.lliiooll.pphelper.utils.showShortToast
import cn.xiaochuankeji.zuiyouLite.ui.setting.SettingActivity
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object SettingHook : BaseHook("setting") {
    override fun init(): Boolean {
        val settingC = "cn.xiaochuankeji.zuiyouLite.ui.setting.SettingActivity".loadClass()
        XposedHelpers.findAndHookMethod(settingC, "onCreate", Bundle::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val activity = param?.thisObject as Activity
                val textCheckUpdate = 2131367613.findView(activity) as TextView
                val ivNew = 2131364191.findView(activity) as TextView
                val tvVersion = 2131367918.findView(activity) as TextView
                val relaCheckUpdate = 2131366843.findView(activity) as LinearLayout
                textCheckUpdate.text = "PPHelper"
                ivNew.visibility = View.INVISIBLE
                tvVersion.text = BuildConfig.VERSION_NAME
                relaCheckUpdate.setOnClickListener {
                    "点击了检查更新".showShortToast()
                    val intent = Intent(activity, SettingsActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Utils.getApplication().startActivity(intent)
                }
            }
        })
        return true
    }
}