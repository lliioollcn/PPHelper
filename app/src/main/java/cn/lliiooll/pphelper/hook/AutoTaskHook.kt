package cn.lliiooll.pphelper.hook

import android.os.Bundle
import android.webkit.WebView
import cn.lliiooll.pphelper.activity.web.PPChromeClient
import cn.lliiooll.pphelper.activity.web.PPWebClient
import cn.lliiooll.pphelper.utils.loadClass
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

object AutoTaskHook : BaseHook("autoTask", "自动任务") {
    override fun init(): Boolean {
        this.desc = "自动任务"
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.webview.WebActivity".loadClass()
        val clazz1 = "cn.xiaochuan.jsbridge.XCWebView".loadClass()
        val clazz2 = clazz?.superclass
        XposedHelpers.findAndHookMethod(
            clazz,
            "onCreate",
            Bundle::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    for (f in clazz2?.declaredFields!!) {
                        if (f.type.name == clazz1?.name) {
                            val webView = XposedHelpers.getObjectField(param?.thisObject, f.name) as WebView
                            webView.webViewClient = PPWebClient(webView.webViewClient)
                            webView.webChromeClient = PPChromeClient(webView.webChromeClient)
                        }
                    }
                }
            })
        return true
    }
}