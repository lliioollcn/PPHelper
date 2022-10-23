package cn.lliiooll.pphelper.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileUtil {
    public static void writeFromStream(InputStream is, File file) {
        try {
            int read;
            FileOutputStream fos = new FileOutputStream(file);
            while ((read = is.read()) != -1){
                fos.write(read);
            }
            fos.close();
            is.close();
        }catch (Throwable e){
            PLog.log(e);
        }
    }
}
