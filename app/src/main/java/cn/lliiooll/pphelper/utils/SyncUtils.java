package cn.lliiooll.pphelper.utils;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SyncUtils {

    private static final ExecutorService sExecutor = Executors.newCachedThreadPool();

    public static void async(@NonNull Runnable r) {
        sExecutor.execute(r);
    }

    public static boolean isConnected(@NotNull String s) {
        try {
            URL u = new URL(s);
            AtomicBoolean ok = new AtomicBoolean(true);
            AtomicBoolean success = new AtomicBoolean(false);
            new Thread(() -> {
                try {
                    HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                    conn.setReadTimeout(2000);
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(2000);
                    PLog.log("链接: " + s + " 返回值: " + conn.getResponseCode());
                    success.set(conn.getResponseCode() == HttpURLConnection.HTTP_OK);
                    ok.set(false);
                } catch (IOException e) {
                    e.printStackTrace();
                    success.set(false);
                    ok.set(false);
                }
            }).start();
            while (ok.get()) {
                Thread.sleep(10L);
            }
            return success.get();
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}
