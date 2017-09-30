#include <jni.h>

using namespace std;

#include "SoundTouch.h"
#include "common.h"

using namespace soundtouch;

/**
 * Get SoundTouch library version string
 */
static jstring getVersionString(JNIEnv *env, jclass type) {
    SoundTouch soundTouch;
    return env->NewStringUTF(soundTouch.getVersionString());
}

/**
 * Get SoundTouch library version Id
 */
static jint getVersionId(JNIEnv *env, jclass type) {
    SoundTouch soundTouch;
    return soundTouch.getVersionId();
}


jlong newInstance(JNIEnv *env, jclass type) {
    return (jlong) (new SoundTouch());
}


void deleteInstance(JNIEnv *env, jobject thiz, jlong handle) {
    SoundTouch *ptr = (SoundTouch *) handle;
    delete ptr;
}

/**
 * Adds 'numSamples' pcs of samples from the 'samples' memory position into
 * the input of the object.
 *
 * @param env JNI
 * @param thiz object
 * @param handle SoundTouch handle
 * @param samples samples array
 * @param nSamples samples count
 */
void putSamples(JNIEnv *env, jobject thiz, jlong handle, jshortArray samples, jint nSamples) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;

    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }
    // 获取传进来的字节数组
    jshort *src = env->GetShortArrayElements(samples, NULL);

    ptr->putSamples(src, (uint) nSamples);

    env->ReleaseShortArrayElements(samples, src, 0);
}

/**
 * 输出转换过的数据
 *
 * @param env JNI env
 * @param thiz AudioProcessor
 * @param handle SoundTouch handle
 * @param outSamples output samples array
 * @param maxSamples max output samples count
 */
jint
receiveSamples(JNIEnv *env, jobject thiz, jlong handle, jshortArray outSamples, jint maxSamples) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return 0;
    }

    SAMPLETYPE sampleBuffer[maxSamples];

    uint samples = ptr->receiveSamples(sampleBuffer, (uint) maxSamples);

    env->SetShortArrayRegion(outSamples, 0, samples, sampleBuffer);

    return samples;
}

/**
 * 输出转换过的数据
 *
 * @param env JNI env
 * @param thiz AudioProcessor
 * @param handle SoundTouch handle
 * @param outSamples output samples array
 * @param maxSamples max output samples count
 */
jint
receiveSamplesDiscard(JNIEnv *env, jobject thiz, jlong handle, jint maxSamples) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return 0;
    }

    uint samples = ptr->receiveSamples((uint) maxSamples);

    return samples;
}

/// Sets new rate control value. Normal rate = 1.0, smaller values
/// represent slower rate, larger faster rates.
void setRate(JNIEnv *env, jobject thiz, jlong handle, jdouble rate) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setRate(rate);
}

/// Sets new pitch control value. Original pitch = 1.0, smaller values
/// represent lower pitches, larger values higher pitch.
void setPitch(JNIEnv *env, jobject thiz, jlong handle, jdouble pitch) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setPitch(pitch);
}

/// Sets new tempo control value. Normal tempo = 1.0, smaller values
/// represent slower tempo, larger faster tempo.
void setTempo(JNIEnv *env, jobject thiz, jlong handle, jdouble tempo) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setTempo(tempo);
}


/// Sets new rate control value as a difference in percents compared
/// to the original rate (-50 .. +100 %)
void setRateChange(JNIEnv *env, jobject thiz, jlong handle, jdouble newRate) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setRateChange(newRate);
}

/// Sets new tempo control value as a difference in percents compared
/// to the original tempo (-50 .. +100 %)
void setTempoChange(JNIEnv *env, jobject thiz, jlong handle, jdouble newTempo) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setTempoChange(newTempo);
}

/// Sets pitch change in octaves compared to the original pitch
/// (-1.00 .. +1.00)
void setPitchOctaves(JNIEnv *env, jobject thiz, jlong handle, jdouble newPitch) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setPitchOctaves(newPitch);
}

/// Sets pitch change in semi-tones compared to the original pitch
/// (-12 .. +12)
void setPitchSemiTonesInt(JNIEnv *env, jobject thiz, jlong handle, jint newPitch) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setPitchSemiTones(newPitch);
}

void setPitchSemiTonesDouble(JNIEnv *env, jobject thiz, jlong handle, jdouble newPitch) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setPitchSemiTones(newPitch);
}

/// Sets the number of channels, 1 = mono, 2 = stereo
void setChannels(JNIEnv *env, jobject thiz, jlong handle, jint numChannels) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setChannels((uint) numChannels);
}

/// Sets sample rate.
void setSampleRate(JNIEnv *env, jobject thiz, jlong handle, jint srate) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->setSampleRate((uint) srate);
}

/// Get ratio between input and output audio durations, useful for calculating
/// processed output duration: if you'll process a stream of N samples, then
/// you can expect to get out N * getInputOutputSampleRatio() samples.
///
/// This ratio will give accurate target duration ratio for a full audio track,
/// given that the the whole track is processed with same processing parameters.
///
/// If this ratio is applied to calculate intermediate offsets inside a processing
/// stream, then this ratio is approximate and can deviate +- some tens of milliseconds
/// from ideal offset, yet by end of the audio stream the duration ratio will become
/// exact.
///
/// Example: if processing with parameters "-tempo=15 -pitch=-3", the function
/// will return value 0.8695652... Now, if processing an audio stream whose duration
/// is exactly one million audio samples, then you can expect the processed
/// output duration  be 0.869565 * 1000000 = 869565 samples.
double getInputOutputSampleRatio(JNIEnv *env, jobject thiz, jlong handle) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return 1.0;
    }

    return ptr->getInputOutputSampleRatio();
}

/// Flushes the last samples from the processing pipeline to the output.
/// Clears also the internal processing buffers.
//
/// Note: This function is meant for extracting the last samples of a sound
/// stream. This function may introduce additional blank samples in the end
/// of the sound stream, and thus it's not recommended to call this function
/// in the middle of a sound stream.
void flushSoundTouch(JNIEnv *env, jobject thiz, jlong handle) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->flush();
}

/// Returns number of samples currently available.
jint numSamples(JNIEnv *env, jobject thiz, jlong handle) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return 0;
    }

    return ptr->numSamples();
}

/// Return number of channels
jint numChannels(JNIEnv *env, jobject thiz, jlong handle) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return 0;
    }

    return ptr->numChannels();
}

/// Returns number of samples currently unprocessed.
jint numUnprocessedSamples(JNIEnv *env, jobject thiz, jlong handle) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return 0;
    }

    return ptr->numUnprocessedSamples();
}

/// Returns nonzero if there aren't any samples available for outputting.
jint isEmpty(JNIEnv *env, jobject thiz, jlong handle) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return 0;
    }

    return ptr->isEmpty();
}

/// Clears all the samples in the object's output and internal processing
/// buffers.
void clear(JNIEnv *env, jobject thiz, jlong handle) {
    // 获取SoundTouch实例
    SoundTouch *ptr = (SoundTouch *) handle;
    if (ptr == NULL) {
        LOGE("The soundtouch pointer is null");
        return;
    }

    ptr->clear();
}


// 这里开始是注册JNI方法的
static const JNINativeMethod gMethods[] = {
        {"newInstance",               "()J",                  (void *) newInstance},
        {"deleteInstance",            "(J)J",                 (void *) deleteInstance},
        {"getVersionId",              "()I",                  (void *) getVersionId},
        {"getVersionString",          "()Ljava/lang/String;", (void *) getVersionString},
        {"putSamples",                "(J[SI)V",              (void *) putSamples},
        {"receiveSamples",            "(J[SI)I",              (void *) receiveSamples},
        {"receiveSamples",            "(JI)I",                (void *) receiveSamplesDiscard},
        {"setRate",                   "(JD)V",                (void *) setRate},
        {"setPitch",                  "(JD)V",                (void *) setPitch},
        {"setTempo",                  "(JD)V",                (void *) setTempo},
        {"setRateChange",             "(JD)V",                (void *) setRateChange},
        {"setTempoChange",            "(JD)V",                (void *) setTempoChange},
        {"setPitchOctaves",           "(JD)V",                (void *) setPitchOctaves},
        {"setPitchSemiTones",         "(JI)V",                (void *) setPitchSemiTonesInt},
        {"setPitchSemiTones",         "(JD)V",                (void *) setPitchSemiTonesDouble},
        {"setChannels",               "(JI)V",                (void *) setChannels},
        {"setSampleRate",             "(JI)V",                (void *) setSampleRate},
        {"getInputOutputSampleRatio", "(J)D",                 (void *) getInputOutputSampleRatio},
        {"flush",                     "(J)V",                 (void *) flushSoundTouch},
        {"numSamples",                "(J)I",                 (void *) numSamples},
        {"numUnprocessedSamples",     "(J)I",                 (void *) numUnprocessedSamples},
        {"isEmpty",                   "(J)I",                 (void *) isEmpty},
        {"clear",                     "(J)V",                 (void *) clear},
        {"numChannels",               "(J)I",                 (void *) numChannels}
};

/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv *env, const char *className,
                                 const JNINativeMethod gMethods[], int numMethods) {
    jclass clazz;
    clazz = env->FindClass(className);
    if (clazz == NULL) {
        LOGE("Native registration unable to find class '%s'", className);
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
        LOGE("RegisterNatives failed for '%s'", className);
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

int register_native_Methods(JNIEnv *env, jstring path) {
    const char * pathChars = env->GetStringUTFChars(path, NULL);
    // pathChars = package/path/to/Class
    int res = registerNativeMethods(env, pathChars,
                                    gMethods, NELEM(gMethods));
    if (res < 0) {
        LOGE("Unable to register native methods.");
    }

    return res;
}

extern "C"
JNIEXPORT jint JNICALL
Java_io_github_junyuecao_soundtouch_SoundTouch_init(
        JNIEnv *env, jclass clazz, jstring path) {
    register_native_Methods(env, path);
    return 0;
}