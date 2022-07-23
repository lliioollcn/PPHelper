package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import cn.lliiooll.pphelper.utils.*
import cn.xiaochuankeji.zuiyouLite.data.post.ServerImageBean
import cn.xiaochuankeji.zuiyouLite.data.post.ServerVideoBean
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method
import java.util.*

object NoMarkHook : BaseHook("no_mark", "去水印") {
    override fun init(): Boolean {
        this.desc = "启用后将替换帖子右下角的保存至相册按钮，评论视频请长按选择保存至相册"
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator".loadClass()
        for (m in clazz?.declaredMethods!!) {
            if (m.name == "a" && m.parameterCount == 5 && m.parameterTypes[0] == Activity::class.java && m.parameterTypes[1] == String::class.java) {
                PLog.log("找到下载视频的方法")
                m.hook(object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        val imageBean = param?.args?.get(2) as ServerImageBean
                        val isVideo = XposedHelpers.callMethod(imageBean, "imageIsVideo") as Boolean
                        if (isVideo) {
                            val videoBean = XposedHelpers.getObjectField(imageBean, "videoBean") as ServerVideoBean
                            val urlSrc = XposedHelpers.getObjectField(videoBean, "urlsrc") as String
                            val thumbId = XposedHelpers.getObjectField(videoBean, "thumbId") as Long
                            ("无水印链接获取成功: " + urlSrc).log()
                            DownloadManager.download(thumbId, urlSrc)
                            "开始下载".showShortToast()
                        }

                    }
                })
                break
            } else {
                m.hook(object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        PLog.log("\n========================================")
                        PLog.log(
                            "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {}\n当前堆栈: ",
                            param?.thisObject?.javaClass?.simpleName,
                            m.name,
                            m.parameterCount,
                            Arrays.toString(m.parameterTypes),
                            Arrays.toString(param?.args)
                        )
                        PLog.printStacks()
                        PLog.log("========================================\n")
                    }
                })
            }

        }
        //val clazz1 = "cn.xiaochuankeji.zuiyouLite.download.MediaFileDownloadListener".loadClass()
        val clazz1 = "cn.xiaochuankeji.zuiyouLite.control.main2.MainSchedulerControl".loadClass()
        //val clazz1 = "cn.xiaochuankeji.zuiyouLite.ui.postdetail.comment.CommentDetailActivity".loadClass()
        //val clazz1 = "cn.xiaochuankeji.zuiyouLite.ui.slide.ActivitySlideDetail".loadClass()
        for (m in clazz1?.declaredMethods!!) {
            PLog.log("寻找方法: {},{}@({})", m.name, m.parameterCount, Arrays.toString(m.parameterTypes))
            if (m.name == "b" && m.parameterCount == 1 && m.parameterTypes[0] == View::class.java) {
                PLog.log("找到方法!")
                m.hook(object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        PLog.log("\n========================================")
                        PLog.log(
                            "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {}\n当前堆栈: ",
                            param?.thisObject?.javaClass?.name,
                            m.name,
                            m.parameterCount,
                            Arrays.toString(m.parameterTypes),
                            Arrays.toString(param?.args)
                        )
                        //PLog.printStacks()
                        PLog.log("========================================\n")
                        val dwBtn: View = XposedHelpers.getObjectField(param?.thisObject, "downloadBtn") as View
                        dwBtn.setOnClickListener {
                            PLog.log("下载按钮被点击")
                            val clazz = "f.g.x.i.a.e".loadClass()
                            for (m1 in clazz?.declaredMethods!!) {
                                PLog.log(
                                    "寻找方法(在混淆方法中): {},{}@({})",
                                    m.name,
                                    m.parameterCount,
                                    Arrays.toString(m.parameterTypes)
                                )
                            }
                            val click: OnClickListener = XposedHelpers.newInstance(
                                clazz,
                                param?.thisObject
                            ) as OnClickListener
                            click.onClick(it)
                        }
                    }
                })
            }
        }
        return true
    }
}