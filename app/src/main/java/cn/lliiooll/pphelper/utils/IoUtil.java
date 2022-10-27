package cn.lliiooll.pphelper.utils;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import org.jetbrains.annotations.NotNull;

import java.io.*;

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
            byte[] buf = new byte[4096];
            while ((read = is.read(buf)) != -1) {
                os.write(buf, 0, read);
            }
            is.close();
            os.close();
        } catch (IOException e) {
            PLog.log(e);
        }
    }

    public static String readStr(InputStream inputs) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputs));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (Throwable e) {
            PLog.log(e);
        }
        return sb.toString();
    }

    public static void writeLine(String s, File file) {
        try {
            FileWriter fw = new FileWriter(file, true);
            fw.write(s + "\n");
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
