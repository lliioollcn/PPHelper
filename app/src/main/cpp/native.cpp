#include <jni.h>
#include "log.h"
#include <string_view>
#include <vector>
#include <map>
#include <sstream>

namespace {
#define EXPORT extern "C" __attribute__((visibility("default")))
extern "C" jint DexKit_JNI_OnLoad(JavaVM *vm, void *reserved);


EXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    LOGI("native loading...");
    return DexKit_JNI_OnLoad(vm, reserved);
}

}