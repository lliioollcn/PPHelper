package cn.lliiooll.pphelper.app;

import android.app.Application;
import cn.lliiooll.pphelper.BuildConfig;

public class PPHelperImpl extends Application {

    public static boolean debug = BuildConfig.DEBUG;

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
