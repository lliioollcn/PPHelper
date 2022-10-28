package cn.lliiooll.pphelper.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.download.DownloadCallback
import cn.lliiooll.pphelper.download.DownloadManager
import cn.lliiooll.pphelper.hook.BaseHook
import cn.lliiooll.pphelper.startup.HybridClassLoader
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

object Utils {
    @JvmStatic
    fun showShort(str: CharSequence) {
        show(str, Toast.LENGTH_SHORT)
    }

    @JvmStatic
    fun showLong(str: CharSequence) {
        show(str, Toast.LENGTH_LONG)
    }

    @JvmStatic
    fun show(str: CharSequence, length: Int) {
        Toast.makeText(getApplication(), str, length).show()
    }

    @JvmStatic
    fun getApplication(): Application? {
        val clazz = loadClass("cn.xiaochuankeji.zuiyouLite.app.AppController")
        if (clazz != null){
            return XposedHelpers.callStaticMethod(
                clazz, "instance"
            ) as Application
        }
        return null;
    }

    @JvmStatic
    fun getVersion(ctx: Context): String {
        val pkgMgr = ctx.packageManager
        val info = pkgMgr.getPackageInfo(ctx.packageName, 0)
        return info.versionName
    }

    @JvmStatic
    fun loadClass(name: String): Class<*>? {
        return HybridClassLoader.load(name)
    }

    @JvmStatic
    fun isNotBlank(s: String): Boolean {
        return s.isNotEmpty()

    }
}


fun String.loadClass(): Class<*>? {
    return Utils.loadClass(this)
}

fun Class<*>.allMethod(function: (Method) -> Unit) {
    for (m in this.declaredMethods) {
        function.invoke(m)
    }
}

fun String.openUrl(context: Context) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(this)
    context.startActivity(intent)
}

fun Activity.open(clazz: Class<*>) {
    this.startActivity(Intent(this, clazz))
}

fun Member.hook(callback: XC_MethodHook) {
    XposedBridge.hookMethod(this, callback)
}

fun Member.hookAfter(callback: (XC_MethodHook.MethodHookParam?) -> Unit) {
    XposedBridge.hookMethod(this, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            callback.invoke(param)
        }
    })
}

fun Method.replace(callback: (XC_MethodHook.MethodHookParam?) -> Unit) {
    XposedBridge.hookMethod(this, object : XC_MethodReplacement() {
        override fun replaceHookedMethod(param: MethodHookParam?): Any {
            return callback.invoke(param)
        }
    })
}

fun Any?.download() {
    if (this?.javaClass?.name?.contains("ServerImageBean") == true) {
        val isVideo = XposedHelpers.callMethod(this, "imageIsVideo") as Boolean
        if (isVideo) {
            val videoBean = XposedHelpers.getObjectField(this, "videoBean")
            var urlSrc = XposedHelpers.getObjectField(videoBean, "urlsrc") as String
            val thumbId = XposedHelpers.getObjectField(videoBean, "thumbId") as Long
            val h265Sourceso = XposedHelpers.getObjectField(videoBean, "h265Sources")
            if (h265Sourceso != null) {
                val h265Sources = XposedHelpers.getObjectField(videoBean, "h265Sources") as List<*>
                if (h265Sources.isNotEmpty()) {
                    val src1 = h265Sources.get(0)
                    val urls = XposedHelpers.getObjectField(src1, "urls") as List<*>
                    if (urls.isNotEmpty()) {
                        val src2 = urls.get(0)
                        val url = XposedHelpers.getObjectField(src2, "url") as String
                        val urlS = url.replace("http://", "");
                        var str = "http://127.0.0.1:2017/$urlS"
                        PLog.log("可连接: $str 在返回值 ${str.isConnected()}")
                        if (!str.isConnected()) {
                            str = "http://127.0.0.1:2018/$urlS"
                        }
                        //DownloadManager.init()
                        urlSrc = str;
                    }
                }
            }
            ("无水印链接获取成功: " + urlSrc).log()
            val dir = Utils.getApplication()?.getExternalFilesDir(Environment.DIRECTORY_MOVIES)
            if (!dir!!.exists()) {
                dir.mkdirs()
            }
            val file = File(dir, "${thumbId}.mp4")
            if (file.exists()) {
                "文件已存在: ${file.absolutePath}".log()
                file.delete()
            }
            file.createNewFile()
            val dw = DownloadManager(urlSrc, file);
            dw.download(object : DownloadCallback {
                override fun onFinished(url: URL?, file: File?) {
                    StoreUtils.saveToStore(file?.name, file?.absolutePath!!)
                    "无水印视频下载完毕".showShortToast()
                }

                override fun onFailed(url: URL?, file: File?, e: Throwable?) {
                    PLog.log(e)
                    "无水印视频下载失败".showShortToast()
                }

                override fun onStart(url: URL?, file: File?) {
                    "开始无水印下载".showShortToast()
                }

            })
        } else {
            "不是视频".log()
        }
    } else {
        ("什么几把东西: " + this?.javaClass?.name).log()
    }

}

fun CharSequence.showShortToast() {
    Utils.showShort(this)
}

fun String.log() {
    PLog.log(this)
}


fun CharSequence.showLongToast() {
    Utils.showLong(this)
}

fun Int.findView(activity: Any?): View {
    return XposedHelpers.callMethod(activity, "findViewById", this) as View
}

fun Long.parseDate(): String {
    return SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date(this))
}

fun BaseHook.addSetting(ctx: Context, parent: LinearLayout) {
    val setting_child = LayoutInflater.from(ctx).inflate(R.layout.settings_child, parent, false) as RelativeLayout
    val title = setting_child.findViewById<TextView>(R.id.setting_child_title)
    val content = setting_child.findViewById<Switch>(R.id.setting_child_content)
    title.id = Random.nextInt()
    title.text = this.name
    content.id = Random.nextInt()
    content.isChecked = this.isEnable
    if (this.clickListener != null) {
        content.setOnClickListener(this.clickListener)
    } else {
        content.setOnClickListener {
            ConfigManager.setEnable(this, content.isChecked)
        }
    }
    setting_child.setOnLongClickListener {
        this.desc.showLongToast()
        true
    }
    parent.addView(setting_child)
    PLog.log("为 {}({}) 添加设置选项,自定义点击事件: {}", this.name, this.label, this.clickListener != null)
}

fun String.isConnected(): Boolean {
    return SyncUtils.isConnected(this)
}
