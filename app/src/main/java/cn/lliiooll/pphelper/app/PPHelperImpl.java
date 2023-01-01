package cn.lliiooll.pphelper.app;

import android.app.Application;

public class PPHelperImpl extends Application {

    private static PPHelperImpl app;

    @Override
    public void onCreate() {
        super.onCreate();
        PPHelperImpl.app = this;
    }

    public static PPHelperImpl getApp() {
        return app;
    }
}
