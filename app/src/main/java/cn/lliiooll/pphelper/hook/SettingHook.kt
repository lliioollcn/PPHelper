package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import cn.lliiooll.pphelper.BuildConfig
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.activity.SettingsActivity
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.util.Random
import kotlin.concurrent.thread

object SettingHook : BaseHook("setting", "设置界面") {

    var isBack = false
    override fun init(): Boolean {
        val settingC = "cn.xiaochuankeji.zuiyouLite.ui.setting.SettingActivity".loadClass()
        XposedHelpers.findAndHookMethod(settingC, "onCreate", Bundle::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                isBack = false
                val activity = param?.thisObject as Activity
                val rootView = ViewUtils.findId("rootView").findView(activity) as RelativeLayout
                val settingSetting = rootView.getChildAt(1) as ScrollView
                val scrollContent = settingSetting.getChildAt(0) as LinearLayout
                // 初始化皮皮搞笑的入口布局
                val ppHelperSettingRoot = LayoutInflater.from(activity).inflate(R.layout.pp_setting, null, false)
                val ppSettingArrow = ppHelperSettingRoot.findViewById<ImageView>(R.id.pp_setting_arrow)
                ppSettingArrow.background = ViewUtils.findDrawable("icon_right_arrow_gary").findDrawable(activity)
                val settingRoot = ppHelperSettingRoot.findViewById<LinearLayout>(R.id.pp_setting_root)
                settingRoot.setOnClickListener {
                    //"点击了检查更新".showShortToast()
                    val intent = Intent(activity, SettingsActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Utils.getApplication()?.startActivity(intent)
                }
                var enable = ConfigManager.isEnable("color_setting", false)
                settingRoot.setOnLongClickListener {
                    ConfigManager.setEnable("color_setting", !enable)
                    enable = !enable;
                    true
                }
                ppHelperSettingRoot.findViewById<TextView>(R.id.pp_setting_hasNew).visibility =
                    if (PPVersion.hasUpdate()) {
                        View.VISIBLE
                    } else {
                        View.GONE
                    }
                val textView = ppHelperSettingRoot.findViewById<TextView>(R.id.pp_setting_version)
                textView.text = BuildConfig.VERSION_NAME
                // 添加设置
                scrollContent.addView(ppHelperSettingRoot, 1)
                val title = ppHelperSettingRoot.findViewById<TextView>(R.id.pp_setting_title)
                thread {
                    var red = 0;
                    var green = 0;
                    var blue = 0;
                    val random = Random()
                    while (true) {
                        if (enable) {
                            red = random.nextInt(255)
                            green = random.nextInt(255)
                            blue = random.nextInt(255)
                            title.post {
                                title.setTextColor(Color.rgb(red, green, blue))
                            }
                        } else {
                            title.post {
                                title.setTextColor(Color.BLACK)
                            }
                        }
                        if (isBack) {
                            break
                        }
                        Thread.sleep(150)
                    }
                }
            }
        })

        XposedHelpers.findAndHookMethod(settingC, "onStop", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                isBack = true
            }
        })

        return true
    }

    override fun isEnable(): Boolean {
        return true
    }
}