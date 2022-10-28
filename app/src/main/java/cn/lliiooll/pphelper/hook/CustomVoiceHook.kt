package cn.lliiooll.pphelper.hook

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.EditText
import androidx.core.net.toFile
import cn.lliiooll.pphelper.config.ConfigManager
import cn.lliiooll.pphelper.utils.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.lang.reflect.Modifier
import java.util.*

object CustomVoiceHook : BaseHook("custom_voice", "自定义评论区语音") {
    override fun init(): Boolean {
        this.desc = "启用后将显示一个悬浮窗，在帖子界面点击悬浮窗使用语音并发送"
        val clazz = "cn.xiaochuankeji.zuiyouLite.ui.input.ActivityInputReview".loadClass()
        XposedHelpers.findAndHookMethod(clazz, "onCreate", Bundle::class.java, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val iV = XposedHelpers.getObjectField(param?.thisObject, "iconVoice") as View
                iV.setOnLongClickListener {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
                    intent.setType("audio/*")
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    XposedHelpers.callMethod(param?.thisObject, "startActivityForResult", intent, 0x4fc)
                    "请选择一个音频文件".showLongToast()
                    true
                }
            }
        })
        XposedHelpers.findAndHookMethod(
            clazz,
            "onActivityResult",
            DexKit.clazz_int,
            DexKit.clazz_int,
            Intent::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    val req = param?.args?.get(0) as Int
                    val resp = param.args?.get(1) as Int
                    val intent = param.args?.get(2) as Intent

                    if (req == 0x4fc) {
                        PLog.log("选择完毕: $resp")
                        if (resp == RESULT_OK) {
                            val uri = intent.data
                            val dir = Utils.getApplication()?.getExternalFilesDir("voiceTmp")
                            if (!dir!!.exists()) {
                                dir.mkdirs()
                            }
                            val file = File(dir, "${System.currentTimeMillis()}.mp3")
                            if (!file.exists()) {
                                file.createNewFile()
                            }
                            PLog.log("复制到文件: ${file.absolutePath}")
                            IoUtil.copy(
                                Utils.getApplication()?.contentResolver?.openInputStream(uri!!)!!,
                                FileOutputStream(file)
                            )

                            for (f in clazz?.declaredFields!!) {
                                val fC = f.type
                                if (fC.name.contains("AudioBean")) {
                                    PLog.log("设置音频: ${fC.name}")
                                    //val obj = XposedHelpers.getObjectField(param.thisObject, f.name)
                                    XposedHelpers.setObjectField(
                                        param.thisObject,
                                        f.name,
                                        AudioBuilder.build(
                                            file.absolutePath,
                                            null,
                                            ConfigManager.getInt("pp_play_voice_time", 1)
                                        )
                                    )
                                    break
                                }
                            }

                            XposedHelpers.callMethod(param.thisObject, "V1")
                        }
                    }
                }
            })
        /*
        clazz?.allMethod { it ->
            val m = it
            it.hookAfter {
                val par = it
                PLog.log("\n========================================")
                PLog.log(
                    "\n来自{}的方法被调用；" + "\n方法名称: {}" + "\n参数数量: {}" + "\n参数类型: {}" + "\n参数内容: {},\n堆栈: ",
                    clazz.name,
                    m.name,
                    m.parameterCount,
                    Arrays.toString(m.parameterTypes),
                    StringBuilder().apply {
                        for (obj in par?.args!!) {
                            if (obj.javaClass.name.contains("cn.xiaochuankeji.zuiyouLite.data.post.AudioBean")) {
                                append(PLog.toJson(obj)).append(",")
                            } else {
                                append(obj.toString()).append(",")
                            }

                        }
                    }
                )
                if (!Modifier.isStatic(m.modifiers)) {
                    PLog.log(StringBuilder().apply {
                        for (f in clazz.declaredFields) {
                            val fC = f.type
                            if (fC.name.contains("AudioBean")) {
                                val obj = XposedHelpers.getObjectField(par?.thisObject, f.name)
                                if (obj != null)
                                    append(PLog.toJson(obj)).append(",")
                                XposedHelpers.setObjectField(
                                    par?.thisObject,
                                    f.name,
                                    AudioBuilder.build(
                                        "/storage/emulated/0/cn.xiaochuankeji.zuiyouLite/tmp/SoundRecorder/bg.mp3",
                                        null,
                                        1
                                    )
                                )
                            }
                            if (fC == DexKit.clazz_int) {
                                val obj = XposedHelpers.getIntField(par?.thisObject, f.name)
                                append(obj).append(",")
                            }
                        }
                    }.toString())
                }
                PLog.printStacks()
                PLog.log("========================================\n")
            }

        }

         */
        return true
    }
}