#include <jni.h>
#include "helperlog.h"
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
                #include <libavutil/log.h>
                #include <libavutil/samplefmt.h>
                #include <libpostproc/postprocess.h>
                #include <libavdevice/avdevice.h>
                #include <ffmpeg.h>
    namespace
    {
        jclass ffmpegClazz;
        jmethodID nativeMsgM;
        jmethodID progressM;
        JNIEnv *globalEnv;
        jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);
        jint DexKit_JNI_OnLoad(JavaVM *vm, void *reserved);
        const char *av_version_info(void);
        unsigned avcodec_version(void);
        unsigned avfilter_version(void);
        unsigned avutil_version(void);
        unsigned avformat_version(void);
        unsigned swscale_version(void);
        unsigned swresample_version(void);
        unsigned postproc_version(void);


        JNIEXPORT void JNICALL
                            Java_cn_lliiooll_pphelper_utils_Natives_test(JNIEnv *env, jobject thiz)
        {
            LOGI("[Native] >>> test <<<");
        }
        void msg_callback(const char *format, va_list args)
        {
            LOGI(format,args);
        }
        void log_callback(void *ptr, int level, const char *format, va_list args)
        {
            msg_callback(format, args);
        }
        void progress_callback(int position, int duration, int state)
        {
        /*
            if (globalEnv && ffmpegClazz && progressM)
            {
                globalEnv->CallStaticVoidMethod(ffmpegClazz, progressM, position, duration, state);
            }
            */
            LOGI("[FFmpeg] 进度: %d, 时长: %d, 状态: %d",position,duration,state);
        }
        void finish_callback(){
            LOGI("[FFmpeg] 处理完毕");
            if (globalEnv && ffmpegClazz && progressM)
                        {
                            globalEnv->CallStaticVoidMethod(ffmpegClazz, progressM, 0,0,0);
                        }
                        LOGI("[FFmpeg] 回调完毕");
        }
        JNIEXPORT void JNICALL Java_cn_lliiooll_pphelper_ffmpeg_FFmpeg_initNative
                              (JNIEnv *env, jclass clazz)
        {
            globalEnv= env;
            av_log_set_level(AV_LOG_TRACE);
            av_log_set_callback(log_callback);
            LOGI("[Native] >>> FFmpeg加载成功");
            LOGI("[Native] >>>>  av_version_info = %s ",av_version_info());
            LOGI("[Native] >>>>  avcodec_version = %d ", avcodec_version());
            LOGI("[Native] >>>>  avfilter_version = %d ", avfilter_version());
            LOGI("[Native] >>>>  avutil_version = %d ", avutil_version());
            LOGI("[Native] >>>>  avformat_version = %d ", avformat_version());
            LOGI("[Native] >>>>  swresample_version = %d ", swresample_version());
            LOGI("[Native] >>>>  swscale_version = %d ", swscale_version());
            LOGI("[Native] >>>>  postproc_version = %d ", postproc_version());
            LOGI("[Native] >>>>  avdevice_version = %d ",avdevice_version());
            LOGI("[Native] 尝试加载Class: cn.lliiooll.pphelper.ffmpeg.FFmpeg");
            ffmpegClazz = env->FindClass("cn/lliiooll/pphelper/ffmpeg/FFmpeg");
            LOGI("[Native] 尝试寻找方法: nativeMsg");
            nativeMsgM = env->GetStaticMethodID( ffmpegClazz, "nativeMsg", "(Ljava/lang/String;)V");
            LOGI("[Native] 尝试寻找方法: progress");
            progressM = env->GetStaticMethodID(ffmpegClazz, "progress", "(III)V");
            LOGI("[Native] 初始化完毕!");
        }
        JNIEXPORT void JNICALL Java_cn_lliiooll_pphelper_ffmpeg_FFmpeg_runCmd
                              (JNIEnv *env, jclass clazz, jint jArgc, jobjectArray jArgv, jobject jCallback)
        {
            LOGI("[Native] 尝试运行指令...");
            int argc = (int) jArgc;
            char **argv = (char**)malloc(argc*sizeof(char*));
            int i = 0;
            int result=0;
            for (i=0;i<argc;i++)
            {
                jstring jstr = (jstring) env->GetObjectArrayElement(jArgv, i);
                char *temp = (char *) env->GetStringUTFChars(jstr, 0);
                argv[i] = (char*)malloc(1024);
                strcpy(argv[i], temp);
                env->ReleaseStringUTFChars(jstr, temp);
            }
            result = run(argc,argv);
            for (i = 0; i < argc; i++)
            {
                free(argv[i]);
            }
            free(argv);
        }
        JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
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
}