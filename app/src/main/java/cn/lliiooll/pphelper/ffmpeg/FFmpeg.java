package cn.lliiooll.pphelper.ffmpeg;

import cn.lliiooll.pphelper.utils.PLog;

import java.io.File;

public class FFmpeg {

    public static void covert(File src, File target) {

    }

    public static void runCmd(String[] command, FFmpegCallBack callBack) {
        runCmd(command.length, command, callBack);
    }

    public static native void runCmd(int argc, String[] argv, FFmpegCallBack callBack);

    public static void init() {
        PLog.d("尝试初始化FFmpeg Native...");
        initNative();
    }

    public static void nativeMsg(String msg) {
        PLog.d("[FFmpeg] " + msg);
    }

    public static native void initNative();
}
