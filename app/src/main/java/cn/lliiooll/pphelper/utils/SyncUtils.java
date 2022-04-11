package cn.lliiooll.pphelper.utils;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SyncUtils {

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    public static void async(@NonNull Runnable r) {
        sExecutor.execute(r);
    }
}
