//
// Created by Junyue Cao on 16/5/25.
//

#ifndef LIB_NATIVE_COMMON_H
#define LIB_NATIVE_COMMON_H

#if defined(__arm__)
    #if defined(__ARM_ARCH_7A__)
        #if defined(__ARM_NEON__)
            #define ABI "armeabi-v7a/NEON"
        #else
            #define ABI "armeabi-v7a"
        #endif
    #else
        #define ABI "armeabi"
    #endif
#elif defined(__i386__)
    #define ABI "x86"
#elif defined(__mips__)
    #define ABI "mips"
#else
    #define ABI "unknown"
#endif

#endif //LIB_NATIVE_COMMON_H
#ifndef NELEM
# define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif
#ifdef __LP64__
#define __PRI_64_prefix  "l"
#define __PRI_PTR_prefix "l"
#else
#define __PRI_64_prefix "ll"
#define __PRI_PTR_prefix
#endif
#ifndef SCNu64
#define	SCNu64			__PRI_64_prefix"u"		/* uint64_t */
#endif
#ifndef SCNx64
#define	SCNx64			__PRI_64_prefix"x"		/* uint64_t */
#endif
#ifndef SCNu32
#define	SCNu32			"u"		/* uint32_t */
#endif

//Log
#ifdef ANDROID
#include <jni.h>
#include <android/log.h>
#define LOGE(format, ...)  __android_log_print(ANDROID_LOG_ERROR, "(>_<)", format, ##__VA_ARGS__)
#define LOGD(format, ...)  __android_log_print(ANDROID_LOG_DEBUG, "LOGD", format, ##__VA_ARGS__)
#define LOGI(format, ...)  __android_log_print(ANDROID_LOG_INFO,  "(^_^)", format, ##__VA_ARGS__)
#else
#define LOGE(format, ...)  printf("(>_<) " format "\n", ##__VA_ARGS__)
#define LOGI(format, ...)  printf("(^_^) " format "\n", ##__VA_ARGS__)
#endif
