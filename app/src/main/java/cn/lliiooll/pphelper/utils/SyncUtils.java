package cn.lliiooll.pphelper.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncUtils {

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void async(Runnable r) {
        sExecutor.execute(r);
    }


    public static void sync(Runnable r) {
        handler.post(r);
    }
}
