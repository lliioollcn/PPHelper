package cn.lliiooll.pphelper.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import cn.lliiooll.pphelper.BuildConfig
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.hook.RemoveADHook
import cn.lliiooll.pphelper.utils.hookstatus.HookStatus
import cn.lliiooll.pphelper.utils.open
import cn.lliiooll.pphelper.utils.openUrl
import cn.lliiooll.pphelper.utils.showShortToast

class MainActivity : Activity(), OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        HookStatus.init(this)
        val check_parent = findViewById<LinearLayout>(R.id.check_parent)
        val check_img = findViewById<ImageView>(R.id.check_img)
        val check_text_enable = findViewById<TextView>(R.id.check_text_enable)
        val main_module_version = findViewById<TextView>(R.id.main_module_version)
        val check_text_type = findViewById<TextView>(R.id.check_text_type)
        if (HookStatus.isModuleEnabled()) {
            check_parent.setBackgroundResource(R.drawable.bg_round_enable)
            check_img.setBackgroundResource(R.drawable.ic_round_check)
            check_text_enable.text = "已激活"
            check_text_type.text = HookStatus.getHookProviderName()
        }
        main_module_version.text = "模块版本: " + BuildConfig.VERSION_NAME
        val main_select_github = findViewById<LinearLayout>(R.id.main_select_github)
        main_select_github.setOnClickListener {
            "https://github.com/lliioollcn/PPHelper".openUrl(this)
        }
        val main_select_group = findViewById<LinearLayout>(R.id.main_select_group)
        main_select_group.setOnClickListener {
            "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1028233124&card_type=group&source=qrcode".openUrl(
                this
            )
        }
        val main_select_about = findViewById<LinearLayout>(R.id.main_select_about)
        main_select_about.setOnClickListener {
            this.open(AboutActivity::class.java)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.switch_noad -> {
                val switch = view as Switch
                ConfigManager.setEnable("remove_ad", switch.isChecked)
                "保存成功".showShortToast()
            }
            R.id.switch_nomark -> {
                val switch = view as Switch
                "该功能还没实现".showShortToast()
                switch.isChecked = false
                //ConfigManager.setEnable("no_mark", switch.isChecked)
                //"保存成功".showShortToast()
            }
        }
    }
}