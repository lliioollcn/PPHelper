#include <jni.h>
#include "log.h"
#include <string_view>
#include <vector>
#include <map>
#include <sstream>

namespace {
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint MMKV_JNI_OnLoad(JavaVM *vm, void *reserved);
extern "C" jint DexKit_JNI_OnLoad(JavaVM *vm, void *reserved);
extern "C"
JNIEXPORT void JNICALL
Java_cn_lliiooll_pphelper_utils_Natives_test(JNIEnv *env, jobject thiz) {
LOGI(">>> test <<<");
}


EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    LOGI("native loading...");
    auto ret = MMKV_JNI_OnLoad(vm, reserved);
        if (ret != JNI_VERSION_1_6) {
            return -3;
        }
        LOGI("MMKV_JNI_OnLoad success");
    return DexKit_JNI_OnLoad(vm, reserved);
}

}