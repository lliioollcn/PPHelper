package cn.lliiooll.pphelper.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.NonNull;
import cn.lliiooll.pphelper.config.ConfigManager;
import cn.lliiooll.pphelper.utils.IoUtil;
import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.utils.SyncUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class DownloadManager {
    private final URL url;
    private final File file;
    /**
     * 下载线程数
     */
    private int threadCount = 5;


    public DownloadManager(String url, String target) throws MalformedURLException {
        this(new URL(url), target);
    }

    public DownloadManager(String url, File target) throws MalformedURLException {
        this(new URL(url), target);
    }

    public DownloadManager(URL url, String target) {
        this(url, new File(target));
    }

    private static int tCount = 3;

    public DownloadManager(URL u, File f) {
        this.url = u;
        this.file = f;
    }

    private Handler handler;
    private Set<Integer> success = new LinkedHashSet<>();
    private Handler.Callback callback1;

    public void download(DownloadCallback callback) {
        callback.onStart(this.url, this.file);
        PLog.log("准备下载文件: " + this.file.getAbsolutePath());
        callback1 = new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if (msg.what == 996) {
                    callback.onFinished(DownloadManager.this.url, DownloadManager.this.file);
                    handler.removeCallbacksAndMessages(callback1);
                }
                if (msg.what == 997) {
                    callback.onFailed(DownloadManager.this.url, DownloadManager.this.file, new RuntimeException("下载超时"));
                    handler.removeCallbacksAndMessages(callback1);
                }
                return false;
            }
        };
        handler = new Handler(Looper.getMainLooper(), callback1);

        if (ConfigManager.isEnable("pp_download_multi_thread")) {
            tCount = ConfigManager.getInt("pp_download_multi_thread_count", 3);
            SyncUtils.async(this::start);
        } else {
            SyncUtils.async(() -> {
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    IoUtil.copy(url.openConnection().getInputStream(), Files.newOutputStream(file.toPath()));
                    Message msg = new Message();
                    msg.what = 996;
                    handler.sendMessage(msg);
                } catch (Throwable e) {
                    PLog.log(e);
                    Message msg = new Message();
                    msg.what = 997;
                    handler.sendMessage(msg);
                }

            });
        }


    }

    private boolean s = true;

    private void start() {
        try {
            PLog.log("开始下载文件: " + this.file.getAbsolutePath());
            int len = url.openConnection().getContentLength();
            int part = len / tCount;
            int index = 0;
            PLog.log("文件大小: " + len);
            PLog.log("下载线程数: " + tCount);
            PLog.log("分片大小: " + part);
            for (int tIndex = 0; tIndex <= tCount; tIndex++) {
                PLog.log("初始化线程: " + tIndex);
                success.add(tIndex);
                int finalIndex = index;
                int finalTIndex = tIndex;
                SyncUtils.async(() -> {
                    int dwIndex = finalIndex;
                    int tnIndex = finalTIndex;
                    try {
                        int endIndex = finalIndex + part;
                        if (endIndex > len) {
                            endIndex = len;
                        }
                        if (endIndex - dwIndex <= 0) {
                            PLog.log("无需进行下载操作");
                        } else {
                            PLog.log("线程 " + tnIndex + " 将从 " + dwIndex + " 下载到 " + endIndex);
                            PLog.log("线程 " + tnIndex + " 开始下载");
                            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                            httpConn.setRequestMethod("GET");
                            httpConn.setConnectTimeout(5000);
                            httpConn.setRequestProperty("Range", "bytes=" + dwIndex + "-" + endIndex);
                            if (httpConn.getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                                throw new RuntimeException("服务器连接失败: " + httpConn.getResponseCode() + " @" + url);
                            }
                            RandomAccessFile target = new RandomAccessFile(file.getAbsoluteFile(), "rwd");
                            InputStream is = httpConn.getInputStream();
                            target.seek(dwIndex);
                            IoUtil.copy(is, target);
                            PLog.log("线程 " + tnIndex + " 数据读取完毕");
                            target.close();
                            is.close();
                        }

                        PLog.log(">>>>>>>>>> 线程 " + tnIndex + " 下载完毕 <<<<<<<<<<");
                    } catch (Throwable e) {
                        PLog.log(e);
                        Message msg = new Message();
                        msg.what = 997;
                        handler.sendMessage(msg);
                        s = false;
                    } finally {
                        success.remove(tnIndex);
                    }
                });
                index += part;
            }
            SyncUtils.async(() -> {
                long start = System.currentTimeMillis();
                while (!this.success.isEmpty()) {
                    try {
                        Thread.sleep(500L);
                        PLog.log("等待下载完毕: " + this.success.size());
                        if (System.currentTimeMillis() - start > 1000 * 60 * 2) {
                            Message msg = new Message();
                            msg.what = 997;
                            handler.sendMessage(msg);
                            return;
                        }
                        if (!s) {
                            PLog.log("出现一个错误");
                            return;
                        }
                    } catch (InterruptedException e) {
                        PLog.log(e);
                    }
                }
                PLog.log("下载完毕,回调");
                Message msg = new Message();
                msg.what = 996;
                handler.sendMessage(msg);
            });
        } catch (Throwable e) {
            PLog.log(e);
        }
    }
}
