package cn.lliiooll.pphelper.ffmpeg;

import cn.lliiooll.pphelper.utils.PLog;
import cn.lliiooll.pphelper.utils.SyncUtils;

import java.io.File;

public class FFmpeg {

    private static FFmpegCallBack callBack;

    public static void covert(File src, File target) {

    }

    public static void runCmd(String command, FFmpegCallBack callBack) {
        FFmpeg.callBack = callBack;
        PLog.d("处理ffmpeg指令: " + command);
        String[] cmd = command.split(" ");
        runCmd(cmd.length, cmd, callBack);
    }

    public static native void runCmd(int argc, String[] argv, FFmpegCallBack callBack);

    public static void init() {
        PLog.d("尝试初始化FFmpeg Native...");
        initNative();
    }

    public static void nativeMsg(String msg) {
        PLog.d("[FFmpeg] " + msg);
    }

    public static void progress(int position, int duration, int state) {
        PLog.d("[FFmpegCallBack] 进度: " + position + " 时长: " + duration + " 状态:" + state);

        if (callBack != null) {
            SyncUtils.sync(() -> callBack.finish());
        }


    }

    public static native void initNative();
}
