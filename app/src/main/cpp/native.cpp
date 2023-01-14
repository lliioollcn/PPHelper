
#include <jni.h>
#include "log.h"
#include <string_view>
#include <vector>
#include <map>
#include <sstream>
#include <iostream>
#include <string>
#include "cn_lliiooll_pphelper_ffmpeg_FFmpeg.h"
extern "C"
{
    #include <libavcodec/avcodec.h>
        #include <libavformat/avformat.h>
        #include <libavutil/avutil.h>
        #include <libswresample/swresample.h>
        #include <libswscale/swscale.h>
        #include <libswscale/swscale.h>
        #include <libavutil/channel_layout.h>
        #include <libavutil/common.h>
        #include <libavutil/frame.h>
        #include <libavutil/samplefmt.h>
        #include <libpostproc/postprocess.h>
        #include "ffmpeg.h"
}
namespace
{
    #define EXPORT extern "C" __attribute__((visibility("default")))
                extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);
    extern "C" jint DexKit_JNI_OnLoad(JavaVM *vm, void *reserved);
    extern "C" const char *av_version_info(void);
    extern "C" unsigned avcodec_version(void);
    extern "C" unsigned avfilter_version(void);
    extern "C" unsigned avutil_version(void);
    extern "C" unsigned avformat_version(void);
    extern "C" unsigned swscale_version(void);
    extern "C" unsigned swresample_version(void);
    extern "C" unsigned postproc_version(void);
    extern "C"
                JNIEXPORT void JNICALL
                Java_cn_lliiooll_pphelper_utils_Natives_test(JNIEnv *env, jobject thiz)
    {
        LOGI("[Native] >>> test <<<");
    }
    jclass callBackClazz;
    jclass ffmpegClazz;
    jmethodID nativeMsgM;
    jmethodID progressM;
    JNIEXPORT void JNICALL Java_cn_lliiooll_pphelper_ffmpeg_FFmpeg_initNative
                  (JNIEnv *env, jclass clazz)
    {
        LOGI("[Native] >>> FFmpeg加载成功");
        LOGI("[Native] >>>>  av_version_info = %s ",av_version_info());
        LOGI("[Native] >>>>  avcodec_version = %d ", avcodec_version());
        LOGI("[Native] >>>>  avfilter_version = %d ", avfilter_version());
        LOGI("[Native] >>>>  avutil_version = %d ", avutil_version());
        LOGI("[Native] >>>>  avformat_version = %d ", avformat_version());
        LOGI("[Native] >>>>  swresample_version = %d ", swresample_version());
        LOGI("[Native] >>>>  swscale_version = %d ", swscale_version());
        LOGI("[Native] >>>>  swscale_version = %d ", postproc_version());
        LOGI("[Native] 尝试加载Class: cn.lliiooll.pphelper.ffmpeg.FFmpeg");
        ffmpegClazz = env->FindClass("cn/lliiooll/pphelper/ffmpeg/FFmpeg");
        LOGI("[Native] 尝试寻找方法: nativeMsg");
        nativeMsgM = env->GetStaticMethodID( ffmpegClazz, "nativeMsg", "(Ljava/lang/String;)V");
        LOGI("[Native] 尝试加载Class: cn.lliiooll.pphelper.ffmpeg.FFmpegCallBack");
        callBackClazz = env->FindClass("cn/lliiooll/pphelper/ffmpeg/FFmpegCallBack");
        LOGI("[Native] 尝试寻找方法: progress");
        progressM = env->GetMethodID(callBackClazz, "progress", "(III)V");
        LOGI("[Native] 初始化完毕!");
    }
    JNIEXPORT void JNICALL Java_cn_lliiooll_pphelper_ffmpeg_FFmpeg_runCmd
                  (JNIEnv *env, jclass clazz, jint jArgc, jobjectArray jArgv, jobject jCallback)
    {
        LOGI("[Native] 尝试运行指令...");
        int argc = (int) jArgc;
        char **argv = (char**)malloc(argc*sizeof(char*));
        int i = 0;
        for (i=0;i<argc;i++)
        {
            jstring jstr = (jstring) env->GetObjectArrayElement(jArgv, i);
            char *temp = (char *) env->GetStringUTFChars(jstr, 0);
            argv[i] = (char*)malloc(8*1024);
            strcpy(argv[i], temp);
            env->ReleaseStringUTFChars(jstr, temp);
        }
        //run(argc,argv);
        for (i = 0; i < argc; i++)
        {
            free(argv[i]);
        }
        free(argv);
    }
    EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
    {
        JNIEnv *env = nullptr;
        if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK)
        {
            return -1;
        }
        LOGI("native loading...");
        auto ret = MMKV_JNI_OnLoad(vm, reserved);
        if (ret != JNI_VERSION_1_6)
        {
            return -3;
        }
        LOGI("MMKV_JNI_OnLoad success");
        return DexKit_JNI_OnLoad(vm, reserved);
    }
}