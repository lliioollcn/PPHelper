package cn.lliiooll.pphelper.hook

import android.app.Activity
import android.content.Context
import android.content.Intent
import cn.lliiooll.pphelper.utils.*
import cn.xiaochuankeji.zuiyouLite.data.post.PostDataBean
import cn.xiaochuankeji.zuiyouLite.data.post.ServerImageBean
import cn.xiaochuankeji.zuiyouLite.data.post.ServerVideoBean
import com.alibaba.fastjson.JSON
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import java.util.*

object NoMarkHook : BaseHook("no_mark") {
    override fun init(): Boolean {
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.postlist.holder.PostOperator".loadClass()
        for (m in clazz?.declaredMethods!!) {
            PLog.log("寻找方法: {},{}@({})", m.name, m.parameterCount, Arrays.toString(m.parameterTypes))


            if (m.parameterCount == 5 && m.parameterTypes[0] == Activity::class.java && m.parameterTypes[1] == String::class.java && m.parameterTypes[4] == Object::class.java && m.name == "a") {
                PLog.log("找到方法: {},{}@({})", m.name, m.parameterCount, Arrays.toString(m.parameterTypes))
                m.hook(object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?) {
                        val imageBean = param?.args?.get(2) as ServerImageBean
                        val videoBean = XposedHelpers.getObjectField(imageBean, "videoBean") as ServerVideoBean

                        val intent = Intent()
                        intent.action = "cn.xiaochuan.download.retry"
                        intent.putExtra("download_media_id", videoBean.thumbId)
                        intent.putExtra("download_url", videoBean.urlsrc)
                        intent.putExtra("download_type", 3)
                        intent.putExtra("download_title", videoBean.thumbId)
                        intent.putExtra("download_file_name", "${videoBean.thumbId}.mp4")
                        intent.putExtra("download_fmt", XposedHelpers.getObjectField(imageBean, "fmt").toString())
                        intent.putExtra("download_file_dest", "${videoBean.thumbId}.mp4")
                        intent.`package` = "cn.xiaochuankeji.zuiyouLite.download"
                        Utils.getApplication().sendBroadcast(intent)
                    }
                })
                break
            }


            /*
            for (paramType in m.parameterTypes) {
                m.hook(object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        for (arg in param?.args!!) {
                            if (arg is PostDataBean) {
                                val sb = StringBuilder()
                                sb.append("当前方法: ").append(param?.method?.name).append(" \n参数类型: ")
                                    .append(Arrays.toString(m.parameterTypes)).append(" \n参数数据: ")
                                    .append(Arrays.toString(param?.args)).append(" \n参数个数: ")
                                    .append(m.parameterCount).append(" \n返回类型: ")
                                    .append(m.returnType.simpleName)
                                PLog.log(sb.toString())
                                PLog.printStacks()

                            }
                        }
                    }
                })
            }

             */
        }
        XposedHelpers.findAndHookMethod("cn.xiaochuankeji.zuiyouLite.download.DownloadReceiver".loadClass(),
            "onReceive",
            Context::class.java,
            Intent::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    "下载完毕".showShortToast()
                }
            })
        return true
    }
}