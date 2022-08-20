package cn.lliiooll.pphelper.utils

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.hook.BaseHook
import cn.lliiooll.pphelper.startup.HybridClassLoader
import cn.xiaochuankeji.zuiyouLite.app.AppController
import cn.xiaochuankeji.zuiyouLite.data.post.ServerVideoBean
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Member
import java.lang.reflect.Method
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
        Toast.makeText(AppController.instance(), str, length).show()
    }

    @JvmStatic
    fun getApplication(): Application {
        return XposedHelpers.callStaticMethod(
            loadClass("cn.xiaochuankeji.zuiyouLite.app.AppController"), "instance"
        ) as Application
    }

    @JvmStatic
    fun loadClass(name: String): Class<*>? {
        return XposedHelpers.findClass(name, HybridClassLoader.clLoader)
    }
}


fun String.loadClass(): Class<*>? {
    return Utils.loadClass(this)
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

fun Method.hookAfter(callback: (XC_MethodHook.MethodHookParam?) -> Unit) {
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
            val videoBean = XposedHelpers.getObjectField(this, "videoBean") as ServerVideoBean
            val urlSrc = XposedHelpers.getObjectField(videoBean, "urlsrc") as String
            val thumbId = XposedHelpers.getObjectField(videoBean, "thumbId") as Long
            ("无水印链接获取成功: " + urlSrc).log()
            DownloadManager.download(thumbId, urlSrc)
            "开始无水印下载".showShortToast()
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
    val setting_child = LayoutInflater.from(ctx).inflate(R.layout.settings_child, parent, false) as LinearLayout
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

