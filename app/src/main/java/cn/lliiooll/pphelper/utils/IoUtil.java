package cn.lliiooll.pphelper.utils;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class IoUtil {
    public static void copy(@NotNull InputStream is, @NotNull OutputStream os) {
        try {
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            PLog.log(e);
        }
    }

    public static void copy(InputStream is, RandomAccessFile os) {
        try {
            int read = 0;
            byte[] buf = new byte[2048];
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            PLog.log(e);
        }
    }
}
