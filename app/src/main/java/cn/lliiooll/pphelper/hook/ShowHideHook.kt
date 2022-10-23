package cn.lliiooll.pphelper.hook

import android.os.Bundle
import cn.lliiooll.pphelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object ShowHideHook : BaseHook("comment_show_hide", "评论区显示隐藏评论") {
    override fun init(): Boolean {
        this.desc = "评论区显示隐藏评论"
        XposedHelpers.findAndHookMethod(
            "cn.xiaochuankeji.zuiyouLite.ui.postdetail.comment.CommentDetailActivity".loadClass(),
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
            "cn.xiaochuankeji.zuiyouLite.ui.slide.ActivitySlideDetail".loadClass(),
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
                            val godReviewsObj = XposedHelpers.getObjectField(postDataBean, "godReviews")
                            var godReviews: List<*>
                            if (godReviewsObj != null) {
                                godReviews = godReviewsObj as List<*>;
                                val myReviews = XposedHelpers.getObjectField(postDataBean, "myReviews") as List<*>
                                val reviewList = XposedHelpers.getObjectField(postDataBean, "reviewList") as List<*>
                                for (replay in godReviews) {
                                    XposedHelpers.setIntField(replay, "isHide", 0)
                                }
                                for (replay in myReviews) {
                                    XposedHelpers.setIntField(replay, "isHide", 0)
                                }
                                for (replay in reviewList) {
                                    XposedHelpers.setIntField(replay, "isHide", 0)
                                }
                            }

                            //PLog.log(JSONUtil.toJsonStr(commentBean))
                        }
                    }
                }
            })
        return true
    }
}