package cn.lliiooll.pphelper.download;

import java.io.File;
import java.net.URL;

public interface DownloadCallback {


    void onFinished(URL url, File file);
    void onFailed(URL url, File file,Throwable e);

    void onStart(URL url, File file);
}
