package cn.lliiooll.pphelper.hook

import android.app.Activity
import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XC_MethodReplacement
import java.util.*

object NoMarkHook : BaseHook("no_mark", "去水印") {
    override fun init(): Boolean {
        this.desc = "启用后将替换帖子右下角的保存至相册按钮，评论视频请点击右上角下载按钮"
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator".loadClass()
        for (m in clazz?.declaredMethods!!) {
            if (m.parameterTypes.size == 5 && m.parameterTypes[0] == Activity::class.java && m.parameterTypes[1] == String::class.java) {
                PLog.log("找到下载视频的方法")
                m.hook(object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        val imageBean = param?.args?.get(2)
                        imageBean.download()
                    }
                })
                break
            } else {
                /*
                m.hook(object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        PLog.log("\n========================================")
                        PLog.log(
                            "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {}\n当前堆栈: ",
                            param?.thisObject?.javaClass?.simpleName,
                            m.name,
                            m.parameterTypes.size,
                            Arrays.toString(m.parameterTypes),
                            Arrays.toString(param?.args)
                        )
                        PLog.printStacks()
                        PLog.log("========================================\n")
                    }
                })

                 */
            }

        }
        //val clazz1 = "cn.xiaochuankeji.zuiyouLite.download.MediaFileDownloadListener".loadClass()
        //val clazz1 = "cn.xiaochuankeji.zuiyouLite.control.main2.MainSchedulerControl".loadClass()

        val clazz1 = DexKit.load(DexKit.OBF_COMMENT_VIDEO)
        //val clazz1 = "cn.xiaochuankeji.zuiyouLite.ui.postdetail.comment.CommentDetailActivity".loadClass()
        //val clazz1 = "cn.xiaochuankeji.zuiyouLite.ui.slide.ActivitySlideDetail".loadClass()

        val commentBeanClass = "cn.xiaochuankeji.zuiyouLite.data.CommentBean".loadClass()
        for (m in clazz1?.declaredMethods!!) {
            /*
            PLog.log(
                "寻找方法: {},{}:{}@({})",
                m.name,
                m.parameterCount,
                m.returnType.name,
                Arrays.toString(m.parameterTypes)
            )

             */
            if (m.name == "u0" && m.parameterCount == 1 && m.parameterTypes[0] == commentBeanClass) {
                PLog.log(
                    "找到方法: {},{}:{}@({})",
                    m.name,
                    m.parameterCount,
                    m.returnType.name,
                    Arrays.toString(m.parameterTypes)
                )
                m.replace {
                    PLog.log("开始下载视频")
                    val obj = it?.thisObject
                    val commentBean = it?.args?.get(0)
                    val serverImageF = commentBeanClass?.getDeclaredField("serverImages")
                    val serverImageO = serverImageF?.get(commentBean)
                    if (serverImageO != null) {
                        val serverImages = serverImageF.get(commentBean) as List<*>
                        for (img in serverImages) {
                            img.download()
                        }
                    }

                }
                break
            }
            /*
            if (m.name == "w0" && m.parameterCount == 2) {
                m.replace {
                    PLog.log("开始下载视频")
                    val obj = it?.thisObject
                    val serverImages = it?.args?.get(0)
                    serverImages.download()
                }
            }

             */
        }

        /*
        val clazz2 = "com.google.android.exoplayer2.upstream.cache.SimpleCache".loadClass()
        val clazz3 = "com.google.android.exoplayer2.upstream.DataSpec".loadClass()
        for (c in clazz3?.declaredConstructors!!) {
            c.hookAfter {
                val param = it
                PLog.log("\n========================================")
                PLog.log(
                    "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {},\n堆栈: ",
                    clazz3.name,
                    c.name,
                    c.parameterCount,
                    Arrays.toString(c.parameterTypes),
                    Arrays.toString(param?.args)
                )
                //PLog.printStacks()
                PLog.log("========================================\n")
            }
        }
        for (m in clazz2?.declaredMethods!!) {
            if (m.name == "commitFile")
                m.hookAfter {
                    val param = it
                    PLog.log("\n========================================")
                    PLog.log(
                        "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {}",
                        clazz2.name,
                        m.name,
                        m.parameterCount,
                        Arrays.toString(m.parameterTypes),
                        Arrays.toString(param?.args)
                    )
                    //PLog.printStacks()
                    PLog.log("========================================\n")
                }

        }

         */

        /*
          if ((m.name == "b" || m.name == "v") && m.parameterCount == 1 && m.parameterTypes[0] == View::class.java) {
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
                        val listenerInfo = XposedHelpers.callMethod(dwBtn, "getListenerInfo")
                        val mOnClickListener = XposedHelpers.getObjectField(listenerInfo, "mOnClickListener")
                        if (mOnClickListener != null) {
                            dwBtn.setOnClickListener {
                                PLog.log("下载按钮被点击")
                                val clazz = mOnClickListener.javaClass
                                //PLog.log(clazz, mOnClickListener)
                                for (m in clazz1.declaredMethods) {
                                    if (m.name == "n0" && m.parameterCount == 1) {
                                        val type = m.parameterTypes[0];
                                        PLog.log(type)
                                    }
                                }
                                /*
                                for (f in clazz.declaredFields) {
                                    if (f.name == "a") {
                                        f.isAccessible = true
                                        PLog.log(f.type, f.get(mOnClickListener))
                                    }
                                }

                                 */
                                val click: OnClickListener = XposedHelpers.newInstance(
                                    clazz,
                                    param?.thisObject
                                ) as OnClickListener
                                click.onClick(it)
                            }
                        }

                    }
                })
            }
            if (m.name == "n0" && m.parameterCount == 1) {
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
                        PLog.printStacks()
                    }
                })
            }
         */
        return true
    }
}


