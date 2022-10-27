package cn.lliiooll.pphelper.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import cn.lliiooll.pphelper.R
import cn.lliiooll.pphelper.lifecycle.Parasitics
import cn.lliiooll.pphelper.utils.*
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class LogActivity : AppCompatTransferActivity() {

    lateinit var launcher: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(androidx.appcompat.R.style.Theme_AppCompat)
        try {
            PLog.log(getString(R.string.res_inject_success))
        } catch (e: Throwable) {
            Parasitics.injectModuleResources(resources)
            try {
                PLog.log(getString(R.string.res_inject_success))
            } catch (e: Throwable) {
                PLog.log(e)

            }
        }
        setContentView(R.layout.log_activity)
        if (!PermissionUtils.checkPermissions(Utils.getApplication())) {
            PermissionUtils.requirePermissions(this, 0x4c)
        }
        val log_content = findViewById<TextView>(R.id.log_content)
        val log_back = findViewById<ImageView>(R.id.log_save)
        val log_save = findViewById<ImageView>(R.id.log_save)
        log_back.setOnClickListener {
            this.onBackPressed()
        }

        val dir = Utils.getApplication().getExternalFilesDir("log")
        if (!dir?.exists()!!) {
            dir.mkdirs()
        }
        val file = File(dir, "log.txt");
        if (!file.exists()) {
            file.createNewFile()
        }
        val fr = FileReader(file)
        var txt = fr.readText()
        if (txt.isNotBlank()) {
            log_content.text = txt
        }
        this.launcher =
            registerForActivityResult(
                CreateSpecificTypeDocument("application/zip")
            ) {
                val resolver = this.contentResolver
                val zip = resolver.openFileDescriptor(it, "wt")
                ZipOutputStream(FileOutputStream(zip?.fileDescriptor)).also {
                    it.putNextEntry(ZipEntry("log.txt"))
                    it.write(txt.toByteArray())
                    it.closeEntry()
                    it.close()
                }
                "日志已保存到${it.path}".showShortToast()
            }
        log_save.setOnClickListener {
            //TODO: 导出日志
            this.launcher.launch("log-${SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(Date())}.zip")
        }
        log_save.setOnLongClickListener {
            val dir = Utils.getApplication().getExternalFilesDir("log")
            if (!dir?.exists()!!) {
                dir.mkdirs()
            }
            val file = File(dir, "log.txt");
            if (file.exists()) {
                file.delete()
            }
            "日志清除完毕".showShortToast()
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            0x4c -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    //"权限请求成功".showShortToast()
                } else {
                    //"权限请求失败".showShortToast()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                PLog.log("未知的权限请求代码")
            }
        }

    }
}