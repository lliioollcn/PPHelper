package cn.lliiooll.pphelper.hook

import cn.lliiooll.pphelper.utils.PLog
import cn.lliiooll.pphelper.utils.allMethod
import cn.lliiooll.pphelper.utils.hookAfter
import cn.lliiooll.pphelper.utils.loadClass

object VoiceDownloadHook : BaseHook("voice_download", "语音下载") {
    override fun init(): Boolean {
        this.desc = "评论语音下载"
        val clazz = "cn.xiaochuankeji.zuiyouLite.widget.SoundWavePlayerView".loadClass()
        clazz?.allMethod {
            PLog.log("hook方法: ${it.name}")
            it.hookAfter {
                for (f in clazz.declaredFields) {
                    if (f.name == "d") {
                        PLog.log("长按处理类: ${f.type.name}")
                    }
                }
            }
        }
        return true
    }
}