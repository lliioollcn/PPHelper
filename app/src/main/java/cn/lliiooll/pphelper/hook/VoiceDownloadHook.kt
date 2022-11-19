package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.view.View
import cn.lliiooll.pphelper.activity.dialog.PPInputDialog
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.download.DownloadCallback
import cn.lliiooll.pphelper.download.DownloadManager
import cn.lliiooll.pphelper.utils.*
import cn.lliiooll.pphelper.utils.Utils.loadClass
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.reflect.Modifier
import java.net.URL
import java.util.*

object VoiceDownloadHook : BaseHook("voice_download", "语音下载") {

    var OBF_POST_REVIEW_AIO1 = "Lcn/xiaochuankeji/zuiyouLite/post/review/AIO1" // m.g.l.a

    var OBF_POST_REVIEW_AIO2 = "Lcn/xiaochuankeji/zuiyouLite/post/review/AIO2"

    override fun init(): Boolean {
        this.desc = "开启后长按评论区语音使用"
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.slide.ab.holder.PostReviewHolder".loadClass()
        val aio_clazz1 = DexKit.load(OBF_POST_REVIEW_AIO1)
        val aio_clazz2 = DexKit.load(OBF_POST_REVIEW_AIO2)
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
                                        StoreUtils.saveToAudioStore(
                                            file?.name,
                                            file?.absolutePath!!,
                                            Environment.DIRECTORY_ALARMS
                                        )
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
                                    val acObj = XposedHelpers.callStaticMethod(aio_clazz1, aio1M.name, view.context)
                                        ?: return false
                                    val activity = acObj as Activity
                                    if (m_g_u_g0_w_k0_b_h(commentBean) && i2 == -1) {
                                        i2 = 0
                                    }
                                    for (aio2M in aio_clazz2.declaredMethods) {
                                        if (aio2M.parameterCount == 0 && Modifier.isStatic(aio2M.modifiers) && aio2M.parameterTypes != DexKit.clazz_void) {
                                            val aio2Obj = XposedHelpers.callStaticMethod(aio_clazz2, aio2M.name)
                                            for (aio3M in aio2Obj?.javaClass?.declaredMethods!!) {
                                                if (aio3M.parameterCount == 4 && !Modifier.isStatic(aio3M.modifiers) && aio3M.parameterTypes[0] == Activity::class.java && aio3M.parameterTypes[1] == Object::class.java && aio3M.parameterTypes[3] == DexKit.clazz_int) {
                                                    XposedHelpers.callMethod(
                                                        aio2Obj,
                                                        aio3M.name,
                                                        activity,
                                                        param.thisObject,
                                                        commentBean,
                                                        i2
                                                    )
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

    override fun doStep() {
        val result = DexKit.find(buildMap {
            put(OBF_POST_REVIEW_AIO1, object : HashSet<String?>() {
                init {
                    add("^%d:%02d:%02d$")
                    add("^%02d:%02d$")
                    add("00:00")
                }
            })
            put(OBF_POST_REVIEW_AIO2, object : HashSet<String?>() {
                init {
                    add("巡查举报")
                    add("开启存储权限才能正常下载")
                    add("去设置")
                    add("举报成功，感谢你对家园的贡献!")
                }
            })
        })

        result.forEach { (key: String?, value: Array<String?>?) ->
            PLog.log("========================================")
            PLog.log("查找结果")
            PLog.log(key)
            PLog.log(Arrays.toString(value))
            PLog.log("========================================")
            if (key == OBF_POST_REVIEW_AIO1 && value.isNotEmpty()) {
                PLog.log("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
                for (clazz in value) {
                    val replace = DexKit.doReplace(clazz)
                    PLog.log("正在过滤类: $replace")
                    val cls = loadClass(replace)
                    PLog.log("类: $replace 加载成功")
                    if (cls != null) {
                        if (cls.declaredMethods.size <= 1) continue
                        for (m in cls.declaredMethods) {
                            PLog.log("正在过滤 $replace 中的方法: ${m.name}")
                            if (m.parameterTypes.size == 1 && m.parameterTypes[0] == Context::class.java && m.returnType == Activity::class.java) {
                                PLog.log("过滤完毕: $replace")
                                DexKit.cache(OBF_POST_REVIEW_AIO1, replace)
                                break
                            }
                        }
                    } else {
                        PLog.log("类: $replace 是null!")
                    }
                }
                PLog.log("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<")
            }
        }
        DexKit.cache(OBF_POST_REVIEW_AIO2, result[OBF_POST_REVIEW_AIO2]?.get(0))
    }

    override fun needStep(): Boolean {
        return !(ConfigManager.hasCache(OBF_POST_REVIEW_AIO1) && ConfigManager.hasCache(OBF_POST_REVIEW_AIO2))
                || DexKit.load(OBF_POST_REVIEW_AIO1) == null
                || DexKit.load(OBF_POST_REVIEW_AIO2) == null
    }
}