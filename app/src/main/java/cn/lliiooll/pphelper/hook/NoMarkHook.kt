package cn.lliiooll.pphelper.hook

import android.app.Activity
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.utils.*
import cn.lliiooll.pphelper.utils.Utils.loadClass
import de.robv.android.xposed.XC_MethodReplacement
import java.util.*

object NoMarkHook : BaseHook("no_mark", "去水印") {
    var OBF_COMMENT_VIDEO = "Lcn/xiaochuankeji/zuiyouLite/common/CommentVideo;"
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
            }
        }
        val clazz1 = DexKit.load(OBF_COMMENT_VIDEO)
        val commentBeanClass = "cn.xiaochuankeji.zuiyouLite.data.CommentBean".loadClass()
        for (m in clazz1?.declaredMethods!!) {
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
        }
        return true
    }

    override fun doStep() {
        val result = DexKit.find(buildMap {
            put(OBF_COMMENT_VIDEO, object : HashSet<String?>() {
                init {
                    add("event_media_play_observer")
                    add("event_on_play_review_comment")
                    add("post")
                    add("review")
                    add("+%d")
                    add("http://alfile.ippzone.com/img/mp4/id/")
                    add("videocomment")
                }
            })
        })
        val commentBeanCls = loadClass("cn.xiaochuankeji.zuiyouLite.data.CommentBean")

        result.forEach { (key: String?, value: Array<String?>?) ->
            PLog.log("========================================")
            PLog.log("查找结果")
            PLog.log(key)
            PLog.log(Arrays.toString(value))
            PLog.log("========================================")
            if (value.isNotEmpty()) {
                for (clazz in value) {
                    val replace = DexKit.doReplace(clazz)
                    PLog.log("正在过滤类: $replace")
                    val cls = loadClass(replace)
                    if (cls != null) {
                        for (m in cls.declaredMethods) {
                            if (m.parameterTypes.size == 1 && m.parameterTypes[0] == commentBeanCls) {
                                PLog.log("过滤完毕: $replace")
                                DexKit.cache(key, replace)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun needStep(): Boolean {
        return !ConfigManager.hasCache(OBF_COMMENT_VIDEO) || DexKit.load(OBF_COMMENT_VIDEO) == null
    }
}


