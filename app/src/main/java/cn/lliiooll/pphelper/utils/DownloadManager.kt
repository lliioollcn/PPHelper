package cn.lliiooll.pphelper.utils

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import cn.hutool.core.io.IoUtil
import cn.lliiooll.pphelper.utils.Utils.getApplication
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.task.DownloadTask
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


object DownloadManager {

    fun init() {
        Aria.download(this).register()
        "初始化完成".log()
    }

    fun destroy() {
        Aria.download(this).unRegister()
        "注销完成".log()
    }

    fun download(thumbId: Long, urlSrc: String) {
        val dir = getApplication().getExternalFilesDir(Environment.DIRECTORY_MOVIES)
        if (!dir!!.exists()) {
            dir.mkdirs()
        }
        val file = File(dir, "${thumbId}.mp4")
        if (file.exists()) {
            "文件已存在: ${file.absoluteFile}".log()
            return
        }
        "文件不存在: ${file.absoluteFile}".log()
        Aria.download(this).load(urlSrc).setFilePath(file.absolutePath).create()

    }

    @Download.onTaskStart
    fun start(task: DownloadTask) {
        "任务 ${task.taskName}(${task.entity.fileName}) 开始下载(从 ${task.entity.realUrl} 到 ${task.entity.filePath})".log()
    }

    @Download.onTaskRunning
    fun running(task: DownloadTask) {
        "任务 ${task.taskName}(${task.entity.fileName}) 下载中,速度 ${task.convertSpeed},进度 ${task.convertCurrentProgress}(从 ${task.entity.realUrl} 到 ${task.entity.filePath})".log()
    }


    @Download.onTaskFail
    fun fail(task: DownloadTask) {
        "任务 ${task.taskName}(${task.entity.fileName}) 下载失败(从 ${task.entity.realUrl} 到 ${task.entity.filePath})".log()
    }

    @Download.onTaskComplete
    fun complete(task: DownloadTask) {
        "任务 ${task.taskName}(${task.entity.fileName}) 下载完成(从 ${task.entity.realUrl} 到 ${task.entity.filePath})".log()
        saveToStore(task.entity.fileName, task.entity.filePath)
        "无水印视频下载完毕".showShortToast()
        "无水印视频下载完毕".log()
    }

    fun saveToStore(fileName: String?, filePath: String) {
        val values = ContentValues()
        val file = File(filePath)
        val resolver = getApplication().contentResolver
        values.put(MediaStore.Video.Media.DISPLAY_NAME, fileName)
        values.put(MediaStore.Video.Media.TITLE, fileName)
        values.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        values.put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Video.Media.SIZE, file.length())
        var localUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
            values.put(MediaStore.Video.Media.DATE_TAKEN, System.currentTimeMillis() / 1000)
            values.put(MediaStore.Video.Media.IS_PENDING, 1)
            resolver.insert(MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY), values)
        } else {
            values.put(MediaStore.Video.Media.DATA, filePath)
            resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
        }
        val pfd = localUri?.let { resolver.openFileDescriptor(it, "w") }
        val fo = FileOutputStream(pfd?.fileDescriptor)
        val fi = FileInputStream(filePath)
        IoUtil.copy(fi, fo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.clear()
            values.put(MediaStore.Video.Media.IS_PENDING, 0)
            if (localUri != null) {
                resolver.update(localUri, values, null, null)
            }
        }
    }
}