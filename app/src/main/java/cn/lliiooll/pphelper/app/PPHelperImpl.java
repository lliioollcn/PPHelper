package cn.lliiooll.pphelper.app;

import android.app.Application;
import cn.lliiooll.pphelper.BuildConfig;
import cn.lliiooll.pphelper.config.ConfigManager;
import cn.lliiooll.pphelper.utils.DexKit;

public class PPHelperImpl extends Application {

    public static boolean debug = BuildConfig.DEBUG;

    public static PPHelperImpl getInstance() {
        return instance;
    }

    private static PPHelperImpl instance = null;


    @Override
    public void onCreate() {
        PPHelperImpl.instance = this;
        super.onCreate();
    }
}
