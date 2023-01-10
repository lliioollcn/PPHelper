package cn.lliiooll.pphelper.utils;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.*;

public class MediaStoreUtils {
    public static void insertVideo(Context ctx, File file) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Video.Media.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
            PLog.d("开始获取uri");
            Uri uri = ctx.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
            PLog.d("开始获取输出流: " + uri);
            OutputStream fos = ctx.getContentResolver().openOutputStream(uri);
            FileInputStream fis = new FileInputStream(file);
            PLog.d("开始写出视频");
            IOUtils.copy(fis, fos);
            PLog.d("写出完毕！");
        } catch (Throwable e) {
            PLog.e(e);
        }

    }

    public static void insertVoice(Context ctx, File file) {
        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Audio.Media.DISPLAY_NAME, file.getName());
            values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/x-mpeg");
            values.put(MediaStore.Audio.Media.DATA, file.getAbsolutePath());
            PLog.d("开始获取uri");
            Uri uri = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Audio.Media.RELATIVE_PATH, Environment.DIRECTORY_ALARMS);
                values.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis());
                uri = ctx.getContentResolver().insert(MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values);
            } else {
                uri = ctx.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values);
            }

            PLog.d("开始获取输出流: " + uri);
            OutputStream fos = ctx.getContentResolver().openOutputStream(uri);
            FileInputStream fis = new FileInputStream(file);
            PLog.d("开始写出语音");
            IOUtils.copy(fis, fos);
            PLog.d("写出完毕！");
        } catch (Throwable e) {
            PLog.e(e);
        }
    }
}
