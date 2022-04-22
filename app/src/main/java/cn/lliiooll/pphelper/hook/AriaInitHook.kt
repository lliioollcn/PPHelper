package cn.lliiooll.pphelper.hook

import android.content.Intent
import cn.lliiooll.pphelper.utils.DownloadManager
import cn.lliiooll.pphelper.utils.Utils
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.common.QueueMod
import com.arialyy.aria.util.ALog

object AriaInitHook : BaseHook("aria_init", "Aria初始化") {
    override fun init(): Boolean {
        val app = Utils.getApplication()
        val dwCfg = Aria.get(app).downloadConfig
        val appCfg = Aria.get(app).appConfig
        dwCfg.maxTaskNum = 10
        dwCfg.isUseHeadRequest = false
        dwCfg.maxSpeed = 0
        dwCfg.isUseBlock = true
        dwCfg.threadNum = 3
        dwCfg.reTryNum = 5
        dwCfg.reTryInterval = 2000
        dwCfg.connectTimeOut = 10000
        dwCfg.ioTimeOut = 20000
        dwCfg.buffSize = 1024 * 8
        dwCfg.isConvertSpeed = true
        dwCfg.updateInterval = 1000
        dwCfg.queueMod = QueueMod.WAIT.tag
        appCfg.isNetCheck = true
        appCfg.isNotNetRetry = false
        appCfg.isUseBroadcast = false
        appCfg.logLevel = ALog.LOG_LEVEL_VERBOSE
        Aria.init(app)
        DownloadManager.init()
        return true
    }
}