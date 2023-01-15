package cn.lliiooll.pphelper.download;

import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.documentfile.provider.DocumentFile;
import cn.lliiooll.pphelper.config.PConfig;
import cn.lliiooll.pphelper.data.ServerImageBeanData;
import cn.lliiooll.pphelper.utils.*;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DownloadManager {

    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void download(ServerImageBeanData imageBean) {
        //TODO: 尝试调用皮皮搞笑自带的下载器下载
        Toast.makeText(AppUtils.getHostAppInstance(), "开始下载无水印视频", Toast.LENGTH_SHORT).show();
        /*
        File dir = AppUtils.getHostAppInstance().getExternalFilesDir("helperJson");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, System.currentTimeMillis() + ".json");
        if (file.exists()) {
            file.delete();
        }

        IOUtils.write(new GsonBuilder().serializeNulls().create().toJson(IOUtils.toMap(imageBean)),file);

         */


        String u = imageBean.video().urls().get(0);
        String u1 = u.replace("http://", "").replace("https://", "");
        String url = "http://127.0.0.1:2017/" + u1;
        if (!IOUtils.isConnected(url)) {
            url = "http://127.0.0.1:2018/" + u1;
        }
        String finalUrl = url;
        SyncUtils.async(() -> {
            try {
                File dir = AppUtils.getHostAppInstance().getExternalFilesDir("noMarkVideo");
                if (!dir.exists()) {
                    dir.mkdirs();
                } else {
                    if (dir.listFiles() != null) {
                        for (File f : dir.listFiles()) {
                            PLog.d("删除缓存: " + f.getAbsolutePath());
                            f.delete();
                        }
                    }
                }
                File file = new File(dir, imageBean.id() + ".mp4");
                if (file.exists()) {
                    file.delete();
                }
                download(finalUrl, file);

                String videoSavePath = PConfig.str("saveVideoPath", null);
                if (videoSavePath != null) {
                    PLog.d("存在自定义视频路径，复制到自定义路径: " + videoSavePath);
                    Uri uri = Uri.parse(videoSavePath);
                    DocumentFile saveDir = DocumentFile.fromTreeUri(AppUtils.getHostAppInstance(), uri);
                    DocumentFile saveFile = saveDir.createFile("video/mp4", file.getName());
                    IOUtils.copy(AppUtils.getHostAppInstance(), file, saveFile.getUri());
                    PLog.d("复制视频到自定义路径成功");
                }
                handler.post(() -> {
                    MediaStoreUtils.insertVideo(AppUtils.getHostAppInstance(), file);
                    Toast.makeText(AppUtils.getHostAppInstance(), "无水印视频下载完毕", Toast.LENGTH_SHORT).show();
                    file.delete();
                    PLog.d("文件删除完毕.");
                });
            } catch (Throwable e) {
                PLog.e(e);
            }
        });
    }

    public static void download(String url, File file) {
        try {
            URL uri = new URL(url);
            URLConnection conn = uri.openConnection();
            if (file.exists()) {
                file.delete();
            }
            PLog.d("下载到文件: " + file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);
            IOUtils.copy(conn.getInputStream(), fos);
        } catch (Throwable e) {
            PLog.e(e);
        }

    }


    public static void downloadVoice(String url) {
        Toast.makeText(AppUtils.getHostAppInstance(), "开始下载语音", Toast.LENGTH_SHORT).show();
        SyncUtils.async(() -> {
            try {
                File dir = AppUtils.getHostAppInstance().getExternalFilesDir("voiceTemp");
                if (!dir.exists()) {
                    dir.mkdirs();
                } else {
                    if (dir.listFiles() != null) {
                        for (File f : dir.listFiles()) {
                            PLog.d("删除缓存: " + f.getAbsolutePath());
                            f.delete();
                        }
                    }
                }
                File file = new File(dir, System.currentTimeMillis() + ".mp3");
                if (file.exists()) {
                    file.delete();
                }
                download(url, file);
                String voiceSavePath = PConfig.str("saveVoicePath", null);
                if (voiceSavePath != null) {
                    PLog.d("存在自定义语音路径，复制到自定义路径: " + voiceSavePath);
                    Uri uri = Uri.parse(voiceSavePath);
                    DocumentFile saveDir = DocumentFile.fromTreeUri(AppUtils.getHostAppInstance(), uri);
                    DocumentFile saveFile = saveDir.createFile("audio/x-mpeg", file.getName());
                    IOUtils.copy(AppUtils.getHostAppInstance(), file, saveFile.getUri());
                    PLog.d("复制到自定义语音路径成功");
                }
                handler.post(() -> {
                    MediaStoreUtils.insertVoice(AppUtils.getHostAppInstance(), file);
                    Toast.makeText(AppUtils.getHostAppInstance(), "语音下载完毕", Toast.LENGTH_SHORT).show();
                    file.delete();
                    PLog.d("文件删除完毕.");
                });
            } catch (Throwable e) {
                PLog.e(e);
            }
        });
    }
}
