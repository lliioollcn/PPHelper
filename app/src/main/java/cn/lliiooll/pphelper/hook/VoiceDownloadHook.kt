package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.view.View
import cn.lliiooll.pphelper.activity.dialog.PPInputDialog
import cn.lliiooll.pphelper.download.DownloadCallback
import cn.lliiooll.pphelper.download.DownloadManager
import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.reflect.Modifier
import java.net.URL
import java.util.StringJoiner

object VoiceDownloadHook : BaseHook("voice_download", "语音下载") {
    override fun init(): Boolean {
        this.desc = "开启后长按评论区语音使用"
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.slide.ab.holder.PostReviewHolder".loadClass()
        val aio_clazz1 = DexKit.load(DexKit.OBF_POST_REVIEW_AIO1)
        val aio_clazz2 = DexKit.load(DexKit.OBF_POST_REVIEW_AIO2)
        clazz?.allMethod {
            if (it.returnType == DexKit.clazz_boolean && it.parameterCount == 3 && it.parameterTypes[1] == View::class.java && it.parameterTypes[2] == DexKit.clazz_int) {
                PLog.log("hook方法: ${it.name}")
                XposedBridge.hookMethod(it, object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        PLog.log("调用方法: ${it.name}")
                        val commentBean = param?.args?.get(0)
                        val view = param?.args?.get(1) as View
                        var i2 = param.args?.get(2) as Int
                        val audio = XposedHelpers.getObjectField(commentBean, "audio")
                        if (audio != null) {
                            val url = XposedHelpers.getObjectField(audio, "url") as String
                            PLog.log("语音url: $url")
                            PPInputDialog(view.context).setA(4).setCall {
                                val dir = Utils.getApplication()?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
                                if (!dir!!.exists()) {
                                    dir.mkdirs()
                                }

                                val file = File(dir, url.md5())
                                if (file.exists()) {
                                    "文件已存在: ${file.absolutePath}".log()
                                    file.delete()
                                }
                                file.createNewFile()
                                DownloadManager(url, file).download(object : DownloadCallback {
                                    override fun onFinished(url: URL?, file: File?) {
                                        "语音下载完毕".showShortToast()
                                        StoreUtils.saveToAudioStore(file?.name, file?.absolutePath!!, "Alarms")
                                    }

                                    override fun onFailed(url: URL?, file: File?, e: Throwable?) {
                                        PLog.log(e)
                                        "语音下载失败".showShortToast()
                                    }

                                    override fun onStart(url: URL?, file: File?) {
                                        "开始下载语音".showShortToast()
                                    }

                                })
                            }.show()
                        } else {
                            for (aio1M in aio_clazz1.declaredMethods) {
                                if (Modifier.isStatic(aio1M.modifiers) && aio1M.parameterCount == 1 && aio1M.parameterTypes[0] == Context::class.java && aio1M.returnType == Activity::class.java) {
                                    val acObj = aio1M.invokeStatic(view.context) ?: return false
                                    val activity = acObj as Activity
                                    if (m_g_u_g0_w_k0_b_h(commentBean) && i2 == -1) {
                                        i2 = 0
                                    }
                                    for (aio2M in aio_clazz2.declaredMethods) {
                                        if (aio2M.parameterCount == 0 && Modifier.isStatic(aio2M.modifiers) && aio2M.parameterTypes != DexKit.clazz_void) {
                                            val aio2Obj = aio2M.invokeStatic()
                                            for (aio3M in aio2Obj?.javaClass?.declaredMethods!!) {
                                                if (aio3M.parameterCount == 4 && !Modifier.isStatic(aio3M.modifiers) && aio3M.parameterTypes[0] == Activity::class.java && aio3M.parameterTypes[1] == Object::class.java && aio3M.parameterTypes[3] == DexKit.clazz_int) {
                                                    aio3M.call(aio2Obj, activity, param.thisObject, commentBean, i2)
                                                }
                                            }
                                        }
                                    }

                                }
                            }

                        }
                        return true
                    }

                })
            }
        }
        return true
    }

    fun m_g_u_g0_w_k0_b_h(ins: Any?): Boolean {
        return !(if (ins == null) {
            true
        } else {
            val serverImagesObj = XposedHelpers.getObjectField(ins, "serverImages")
            if (serverImagesObj == null) {
                true
            } else {
                val serverImages = serverImagesObj as List<*>
                if (serverImages.isEmpty()) {
                    true
                } else {
                    if (serverImages.get(0) == null) {
                        true
                    } else {
                        serverImages.size != 1
                    }
                }
            }
        })

    }
}