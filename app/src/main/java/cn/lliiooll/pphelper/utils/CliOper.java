package cn.lliiooll.pphelper.utils;

import android.app.Application;
import cn.lliiooll.pphelper.BuildConfig;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.HashMap;
import java.util.Map;


public class CliOper {
    public static void init(Application app, String account) {
        PLog.log("开始初始化统计...");
        if (!BuildConfig.DEBUG){
            if (!AppCenter.isConfigured()) {
                AppCenter.start(app, getSecert(), Analytics.class, Crashes.class);
            }
            Map<String, String> data = new HashMap<String, String>() {{
                put("app_version", BuildConfig.VERSION_NAME);
                put("app_type", BuildConfig.BUILD_TYPE);
                put("app_code", BuildConfig.VERSION_CODE + "");
                put("account", account);
            }};
            Analytics.trackEvent("onLoad", data);
            PLog.log("上报数据: " + data);
        }
    }

    private static String getSecert() {
        return "d107307f-0e8f-4e99-aaf6-1eb3060d1d22";
    }
}
