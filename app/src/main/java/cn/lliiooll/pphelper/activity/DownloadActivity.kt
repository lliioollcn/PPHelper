package cn.lliiooll.pphelper.activity

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.hook.*
import cn.lliiooll.pphelper.utils.PLog
import cn.lliiooll.pphelper.utils.PermissionUtils
import cn.lliiooll.pphelper.utils.Utils
import cn.lliiooll.pphelper.utils.addSetting
import cn.lliiooll.pphelper.utils.showShortToast
import com.arialyy.aria.core.Aria

class DownloadActivity : Activity(), OnClickListener {

    private val requestId: Int = 0x7c0c8cf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.download_activity)
        if (!PermissionUtils.checkPermissions(Utils.getApplication())) {
            PermissionUtils.requirePermissions(this, this.requestId)
        }
        Aria.download(this).register()
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