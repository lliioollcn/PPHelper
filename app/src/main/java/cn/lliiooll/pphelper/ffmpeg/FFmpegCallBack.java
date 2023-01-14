package cn.lliiooll.pphelper.ffmpeg;

public interface FFmpegCallBack {

    void progress(int position, int duration, int state);
}
