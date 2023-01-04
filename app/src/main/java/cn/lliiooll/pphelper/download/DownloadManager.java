package cn.lliiooll.pphelper.download;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import cn.lliiooll.pphelper.data.ServerImageBeanData;
import cn.lliiooll.pphelper.utils.*;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {

    public static void download(ServerImageBeanData imageBean) {
        //TODO: 尝试调用皮皮搞笑自带的下载器下载
        Handler handler = new Handler(Looper.getMainLooper());
        Toast.makeText(AppUtils.getHostAppInstance(), "开始下载无水印视频", Toast.LENGTH_SHORT).show();
        imageBean.video().urls().forEach(u -> {
            String u1 = u.replace("http://", "").replace("https://", "");
            String url = "http://127.0.0.1:2017/" + u1;
            if (!IOUtils.isConnected(url)) {
                url = "http://127.0.0.1:2018/" + u1;
            }
            String finalUrl = url;
            SyncUtils.async(() -> {
                try {
                    URL uri = new URL(finalUrl);
                    URLConnection conn = uri.openConnection();
                    File dir = AppUtils.getHostAppInstance().getExternalFilesDir("noMarkVideo");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File file = new File(dir, imageBean.id() + ".mp4");
                    if (file.exists()) {
                        file.delete();
                    }
                    PLog.d("下载到文件: " + file.getAbsolutePath());
                    FileOutputStream fos = new FileOutputStream(file);
                    IOUtils.copy(conn.getInputStream(), fos);
                    PLog.d("下载完毕，开始存库相册");
                    handler.post(() -> {
                        MediaStoreUtils.insertVideo(AppUtils.getHostAppInstance(), file);
                        Toast.makeText(AppUtils.getHostAppInstance(), "下载无水印视频完毕", Toast.LENGTH_SHORT).show();
                    });
                } catch (Throwable e) {
                    PLog.e(e);
                }
            });
        });
    }


}
