package cn.lliiooll.pphelper.utils

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import cn.lliiooll.pphelper.utils.Utils.getApplication
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object StoreUtils {
    fun saveToVideoStore(fileName: String?, filePath: String,outDir:String) {
        val values = ContentValues()
        val file = File(filePath)
        val resolver = getApplication()?.contentResolver
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Video.Media.TITLE, fileName)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Video.Media.SIZE, file.length())
        var localUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, outDir)
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Video.Media.IS_PENDING, 1)
            resolver?.insert(MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
        } else {
            values.put(MediaStore.Video.Media.DATA, filePath)
            resolver?.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        }
        val pfd = localUri?.let { resolver?.openFileDescriptor(it, "w") }
        val fo = FileOutputStream(pfd?.fileDescriptor)
        val fi = FileInputStream(filePath)
        IoUtil.copy(fi, fo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            if (localUri != null) {
                resolver?.update(localUri, values, null, null)
            }
        }
        file.delete()
    }
    fun saveToAudioStore(fileName: String?, filePath: String,outDir:String) {
        val values = ContentValues()
        val file = File(filePath)
        val resolver = getApplication()?.contentResolver
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Audio.Media.TITLE, fileName)
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/x-mpeg")
        values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Audio.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Audio.Media.SIZE, file.length())
        var localUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Audio.Media.RELATIVE_PATH, outDir)
            values.put(MediaStore.Audio.Media.DATE_TAKEN, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Audio.Media.IS_PENDING, 1)
            resolver?.insert(MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
        } else {
            values.put(MediaStore.Audio.Media.DATA, filePath)
            resolver?.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
        }
        val pfd = localUri?.let { resolver?.openFileDescriptor(it, "w") }
        val fo = FileOutputStream(pfd?.fileDescriptor)
        val fi = FileInputStream(filePath)
        IoUtil.copy(fi, fo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Audio.Media.IS_PENDING, 0)
            if (localUri != null) {
                resolver?.update(localUri, values, null, null)
            }
        }
        file.delete()
    }

}