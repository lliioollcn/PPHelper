package cn.lliiooll.pphelper.activity

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Switch
import cn.lliiooll.pphelper.BuildConfig
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.activity.dialog.PPInputDialog
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.databinding.SettingsActivityBinding
import cn.lliiooll.pphelper.hook.*
import cn.lliiooll.pphelper.lifecycle.Parasitics
import cn.lliiooll.pphelper.utils.*

class SettingsActivity : AppCompatTransferActivity(), OnClickListener {

    private val requestId: Int = 0x7c0c8cf
    lateinit var _binding: SettingsActivityBinding;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(androidx.appcompat.R.style.Theme_AppCompat)
        try {
            PLog.log(getString(R.string.res_inject_success))
        } catch (e: Throwable) {
            Parasitics.injectModuleResources(resources)
            try {
                PLog.log(getString(R.string.res_inject_success))
            } catch (e: Throwable) {
                PLog.log(e)

            }
        }
        _binding = SettingsActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(_binding.root)
        if (!PermissionUtils.checkPermissions(Utils.getApplication())) {
            PermissionUtils.requirePermissions(this, this.requestId)
        }

        val app_setting_base_parent = findViewById<LinearLayout>(R.id.app_setting_base_parent)// 基础功能
        val app_setting_play_parent = findViewById<LinearLayout>(R.id.app_setting_play_parent)// 娱乐功能
        val app_setting_clean_parent = findViewById<LinearLayout>(R.id.app_setting_clean_parent)// 净化功能
        val app_setting_debug_parent = findViewById<LinearLayout>(R.id.app_setting_debug_parent)// 调试功能
        val setting_download_multi_root = findViewById<LinearLayout>(R.id.setting_download_multi_root)
        val setting_log_root = findViewById<LinearLayout>(R.id.setting_log_root)
        val setting_debug_appcenter = findViewById<Switch>(R.id.setting_debug_appcenter)// appcenter调试
        val setting_download_multi = findViewById<Switch>(R.id.setting_download_multi)// 多线程下载
        val setting_debug_log = findViewById<Switch>(R.id.setting_debug_log)// 日志输出
        setting_debug_appcenter.isChecked = ConfigManager.isEnable("pp_appcenter")
        setting_download_multi.isChecked = ConfigManager.isEnable("pp_download_multi_thread")
        setting_debug_log.isChecked = ConfigManager.isEnable("setting_debug_log", BuildConfig.DEBUG)

        if (setting_download_multi.isChecked) {
            setting_download_multi_root.visibility = View.VISIBLE
        } else {
            setting_download_multi_root.visibility = View.GONE
        }

        if (setting_debug_log.isChecked) {
            setting_log_root.visibility = View.VISIBLE
        } else {
            setting_log_root.visibility = View.GONE
        }

        setting_debug_log.setOnClickListener {
            ConfigManager.setEnable("setting_debug_log", setting_debug_log.isChecked)
            if (setting_debug_log.isChecked) {
                setting_log_root.visibility = View.VISIBLE
            } else {
                setting_log_root.visibility = View.GONE
            }
        }

        setting_debug_appcenter.setOnClickListener {
            ConfigManager.setEnable("pp_appcenter", setting_debug_appcenter.isChecked)
            if (setting_download_multi.isChecked) {
                setting_download_multi_root.visibility = View.VISIBLE
            } else {
                setting_download_multi_root.visibility = View.GONE
            }
        }
        setting_log_root.setOnClickListener {
            this.open(LogActivity::class.java)
        }


        setting_download_multi_root.setOnClickListener {
            PPInputDialog(this).show()
        }

        setting_download_multi.setOnClickListener {
            ConfigManager.setEnable("pp_download_multi_thread", setting_download_multi.isChecked)
        }

        // 基础功能
        NoMarkHook.addSetting(this, app_setting_base_parent)
        RemoveADHook.addSetting(this, app_setting_base_parent)
        // 调试功能
        TestHook.addSetting(this, app_setting_debug_parent)
        // 娱乐功能
        CustomVoiceHook.setClickListener {
            "还没写呢".showShortToast()
        }
        CustomVoiceHook.addSetting(this, app_setting_play_parent)
        ShowHideHook.addSetting(this, app_setting_play_parent)
        // 净化功能
        RemoveLiveHook.setClickListener {
            "还没写呢".showShortToast()
        }
        RemoveLiveHook.addSetting(this, app_setting_clean_parent)
        RemoveVoiceRoomHook.addSetting(this, app_setting_clean_parent)
        RemoveVoiceRoomHook.setClickListener {
            "此功能需要重启皮皮搞笑生效".showLongToast()
        }

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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            requestId -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //"权限请求成功".showShortToast()
                } else {
                    //"权限请求失败".showShortToast()
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