package cn.lliiooll.pphelper.activity

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Switch
import android.widget.TextView
import androidx.core.view.get
import cn.lliiooll.pphelper.BuildConfig
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.hook.RemoveADHook
import cn.lliiooll.pphelper.utils.PLog
import cn.lliiooll.pphelper.utils.hookstatus.HookStatus
import cn.lliiooll.pphelper.utils.open
import cn.lliiooll.pphelper.utils.openUrl
import cn.lliiooll.pphelper.utils.showShortToast

class MainActivity : Activity(), OnClickListener {

    val alias = "cn.lliiooll.pphelper.activity.MainActivityAlias"

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
        val main_select_group = findViewById<LinearLayout>(R.id.main_select_group)
        val main_select_about = findViewById<LinearLayout>(R.id.main_select_about)
        main_select_github.setOnClickListener(this)
        main_select_group.setOnClickListener(this)
        main_select_about.setOnClickListener(this)

        val main_more = findViewById<ImageView>(R.id.main_more)
        main_more.setOnClickListener {
            val hide = ConfigManager.isEnable("icon_hide", false)
            val popMenu = PopupMenu(this, main_more)
            popMenu.menuInflater.inflate(R.menu.main_menu, popMenu.menu)
            popMenu.menu[0].title = if (hide) "显示图标" else "隐藏图标"
            popMenu.setOnMenuItemClickListener {
                if (it.itemId == R.id.main_menu_hide) {
                    PLog.log("隐藏图标: $hide")
                    ConfigManager.setEnable("icon_hide", !hide)
                    val pkManager = packageManager
                    pkManager.setComponentEnabledSetting(
                        ComponentName(this, alias),
                        if (hide) {
                            PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                        } else {
                            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        },
                        0
                    )
                }
                false
            }
            popMenu.show()
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.main_select_github -> {
                "https://github.com/lliioollcn/PPHelper".openUrl(this)
            }

            R.id.main_select_group -> {
                "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1028233124&card_type=group&source=qrcode".openUrl(
                    this
                )
            }

            R.id.main_select_about -> {
                this.open(AboutActivity::class.java)
            }
        }
    }
}