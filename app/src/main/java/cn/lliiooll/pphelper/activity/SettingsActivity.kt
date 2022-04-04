package cn.lliiooll.pphelper.activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Switch
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.hook.RemoveADHook
import cn.lliiooll.pphelper.utils.showShortToast

class SettingsActivity : Activity(), OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        val switch_noad = findViewById<Switch>(R.id.switch_noad)
        val switch_nomark = findViewById<Switch>(R.id.switch_nomark)
        switch_noad.isChecked = ConfigManager.isEnable("remove_ad")
        switch_noad.setOnClickListener(this)
        switch_nomark.isChecked = ConfigManager.isEnable("no_mark")
        switch_nomark.setOnClickListener(this)
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