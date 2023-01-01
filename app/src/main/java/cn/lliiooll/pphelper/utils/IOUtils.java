package cn.lliiooll.pphelper.utils;

import java.io.*;
import java.nio.charset.StandardCharsets;

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
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = is.read(buf)) != -1) {
                fos.write(buf, 0, read);
            }
            is.close();
            fos.close();
        } catch (Throwable e) {
            PLog.e(e);
        }
    }

    public static void write(String str, File file) {
        ByteArrayInputStream bis = new ByteArrayInputStream(str.getBytes(StandardCharsets.UTF_8));
        write(bis, file);
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
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = fis.read(buf)) != -1) {
                bos.write(buf, 0, read);
            }
            fis.close();
            bos.close();
            sb.append(bos.toString("UTF-8"));
        } catch (Throwable e) {
            PLog.e(e);
        }
        return sb.toString();
    }
}
