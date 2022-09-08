package cn.lliiooll.pphelper.hook

import android.os.Bundle
import cn.hutool.json.JSONUtil
import cn.lliiooll.pphelper.utils.PLog
import cn.lliiooll.pphelper.utils.loadClass
import cn.xiaochuankeji.zuiyouLite.ui.postdetail.comment.CommentDetailActivity
import cn.xiaochuankeji.zuiyouLite.ui.slide.ActivitySlideDetail
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object ShowHideHook : BaseHook("comment_show_hide", "评论区显示隐藏评论") {
    override fun init(): Boolean {
        this.desc = "评论区显示隐藏评论"
        XposedHelpers.findAndHookMethod(
            CommentDetailActivity::class.java,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val commentBeanClazz = "cn.xiaochuankeji.zuiyouLite.data.CommentBean".loadClass()
                    val objClazz = param?.thisObject?.javaClass
                    for (field in objClazz?.declaredFields!!) {
                        if (field.type == commentBeanClazz) {
                            val commentBean = XposedHelpers.getObjectField(param.thisObject, field.name)
                            val replays = XposedHelpers.getObjectField(commentBean, "replyReviews") as List<*>
                            XposedHelpers.setIntField(commentBean, "isHide", 0)
                            for (replay in replays) {
                                XposedHelpers.setIntField(replay, "isHide", 0)
                            }
                            //PLog.log(JSONUtil.toJsonStr(commentBean))
                        }
                    }
                }
            })
        XposedHelpers.findAndHookMethod(
            ActivitySlideDetail::class.java,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    //val commentBeanClazz = "cn.xiaochuankeji.zuiyouLite.data.CommentBean".loadClass()
                    val postDataBeanClazz = "cn.xiaochuankeji.zuiyouLite.data.post.PostDataBean".loadClass()
                    val objClazz = param?.thisObject?.javaClass
                    for (field in objClazz?.declaredFields!!) {
                        if (field.type == postDataBeanClazz) {
                            val postDataBean = XposedHelpers.getObjectField(param.thisObject, field.name)
                            val godReviews = XposedHelpers.getObjectField(postDataBean, field.name) as List<*>
                            val myReviews = XposedHelpers.getObjectField(postDataBean, field.name) as List<*>
                            val reviewList = XposedHelpers.getObjectField(postDataBean, field.name) as List<*>
                            for (replay in godReviews) {
                                XposedHelpers.setIntField(replay, "isHide", 0)
                            }
                            for (replay in myReviews) {
                                XposedHelpers.setIntField(replay, "isHide", 0)
                            }
                            for (replay in reviewList) {
                                XposedHelpers.setIntField(replay, "isHide", 0)
                            }
                            //PLog.log(JSONUtil.toJsonStr(commentBean))
                        }
                    }
                }
            })
        return true
    }
}