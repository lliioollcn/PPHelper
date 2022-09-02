package cn.lliiooll.pphelper.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.lliiooll.pphelper.BuildConfig
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.utils.open
import cn.lliiooll.pphelper.utils.openUrl
import cn.lliiooll.pphelper.utils.parseDate

class AboutActivity : Activity(), OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about_activity)
        val about_version = findViewById<TextView>(R.id.about_version)
        val about_time = findViewById<TextView>(R.id.about_time)
        val about_back = findViewById<ImageView>(R.id.about_back)
        val about_eula = findViewById<LinearLayout>(R.id.about_eula)
        val about_code_github = findViewById<LinearLayout>(R.id.about_code_github)
        val about_group_qq = findViewById<LinearLayout>(R.id.about_group_qq)
        val about_os_qn = findViewById<LinearLayout>(R.id.about_os_qn)
        val about_os_xposed = findViewById<LinearLayout>(R.id.about_os_xposed)
        val about_os_mmkv = findViewById<LinearLayout>(R.id.about_os_mmkv)
        val about_os_xa = findViewById<LinearLayout>(R.id.about_os_xa)
        val about_os_dexkit = findViewById<LinearLayout>(R.id.about_os_dexkit)
        about_version.text = BuildConfig.VERSION_NAME
        about_time.text = BuildConfig.BUILD_TIMESTAMP.parseDate()
        about_back.setOnClickListener(this)
        about_eula.setOnClickListener(this)
        about_group_qq.setOnClickListener(this)
        about_code_github.setOnClickListener(this)
        about_os_qn.setOnClickListener(this)
        about_os_xposed.setOnClickListener(this)
        about_os_mmkv.setOnClickListener(this)
        about_os_xa.setOnClickListener(this)
        about_os_dexkit.setOnClickListener(this)

    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.about_back -> {
                this.onBackPressed()
            }

            R.id.about_eula -> {
                this.open(EulaActivity::class.java)
            }

            R.id.about_group_qq -> {
                "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=1028233124&card_type=group&source=qrcode".openUrl(
                    this
                )
            }

            R.id.about_code_github -> {
                "https://github.com/lliioollcn/PPHelper".openUrl(this)
            }

            R.id.about_os_qn -> {
                "https://github.com/ferredoxin/QNotified".openUrl(this)
            }

            R.id.about_os_xposed -> {
                "https://github.com/rovo89/XposedBridge".openUrl(this)
            }

            R.id.about_os_mmkv -> {
                "https://github.com/Tencent/MMKV".openUrl(this)
            }

            R.id.about_os_xa -> {
                "https://github.com/LuckyPray/XAutoDaily".openUrl(this)
            }

            R.id.about_os_dexkit -> {
                "https://github.com/LuckyPray/DexKit".openUrl(this)
            }
        }
    }
}