package cn.lliiooll.pphelper.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.hook.*
import cn.lliiooll.pphelper.utils.*
import cn.xiaochuankeji.zuiyouLite.ui.base.BaseActivity

class SettingsActivity : BaseActivity(), OnClickListener {

    private val requestId: Int = 0x7c0c8cf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (!PermissionUtils.checkPermissions(Utils.getApplication())) {
            PermissionUtils.requirePermissions(this, this.requestId)
        }
        val app_setting_base_parent = findViewById<LinearLayout>(R.id.app_setting_base_parent)// 基础功能
        val app_setting_play_parent = findViewById<LinearLayout>(R.id.app_setting_play_parent)// 娱乐功能
        val app_setting_clean_parent = findViewById<LinearLayout>(R.id.app_setting_clean_parent)// 净化功能
        val app_setting_debug_parent = findViewById<LinearLayout>(R.id.app_setting_debug_parent)// 调试功能
        NoMarkHook.addSetting(this, app_setting_base_parent)
        RemoveADHook.addSetting(this, app_setting_base_parent)
        TestHook.addSetting(this, app_setting_debug_parent)
        CustomVoiceHook.setClickListener {
            "还没写呢".showShortToast()
        }
        CustomVoiceHook.addSetting(this, app_setting_play_parent)
        RemoveLiveHook.setClickListener {
            "还没写呢".showShortToast()
        }
        RemoveLiveHook.addSetting(this, app_setting_clean_parent)
        RemoveVoiceRoomHook.addSetting(this, app_setting_clean_parent)
        RemoveVoiceRoomHook.setClickListener {
            "此功能需要重启皮皮搞笑生效".showLongToast()
        }
        ShowHideHook.addSetting(this, app_setting_play_parent)
        /*
        val switch_noad = findViewById<Switch>(R.id.switch_noad)
        val switch_nomark = findViewById<Switch>(R.id.switch_nomark)
        switch_noad.isChecked = ConfigManager.isEnable("remove_ad")
        switch_noad.setOnClickListener(this)
        switch_nomark.isChecked = ConfigManager.isEnable("no_mark")
        switch_nomark.setOnClickListener(this)

         */
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            /*
            R.id.switch_noad -> {
                val switch = view as Switch
                ConfigManager.setEnable("remove_ad", switch.isChecked)
                "保存成功,重启应用后生效".showShortToast()
            }
            R.id.switch_nomark -> {
                val switch = view as Switch
                "该功能还没实现".showShortToast()
                switch.isChecked = false
                ConfigManager.setEnable("no_mark", switch.isChecked)
                //"保存成功,重启应用后生效".showShortToast()
            }

             */
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            requestId -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    "权限请求成功".showShortToast()
                } else {
                    "权限请求失败".showShortToast()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                PLog.log("未知的权限请求代码")
            }
        }

    }
}