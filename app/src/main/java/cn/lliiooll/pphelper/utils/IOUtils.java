package cn.lliiooll.pphelper.utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * I/O工具类
 */
public class IOUtils {
    /**
     * 从流写入到文件
     *
     * @param is   输入流
     * @param file 目标文件
     */
    public static void write(InputStream is, File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            copy(is, fos);
        } catch (Throwable e) {
            PLog.e(e);
        }
    }

    public static void write(String str, File file) {
        ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        write(bis, file);
    }

    public static void copy(InputStream is, OutputStream os) {
        try {
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
            is.close();
            os.close();
        } catch (Throwable e) {
            PLog.e(e);
        }
    }


    public static String read(File file) {
        StringBuilder sb = new StringBuilder();
        try {
            if (!file.exists()) {
                file.createNewFile();
                return "";
            }
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            copy(fis, bos);
            sb.append(bos.toString("UTF-8"));
        } catch (Throwable e) {
            PLog.e(e);
        }
        return sb.toString();
    }


    public static boolean isConnected(String s) {
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
                    PLog.d("链接: " + s + " 返回值: " + conn.getResponseCode());
                    success.set(conn.getResponseCode() == HttpURLConnection.HTTP_OK);
                    ok.set(false);
                } catch (IOException e) {
                    PLog.e(e);
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
