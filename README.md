# AndroidSoundTouch
A SoundTouch java wrapper library for Android

This repo is a java wrapper of [SoundTouch](https://www.surina.net/soundtouch/) for Android.

 - Keep APIs of soundtouch and provide a Java class to process audio streams so you can do it without any C/C++ code.
 - Support ENCODING_PCM_16BIT samples.
 - Support mono(1)/stereo(2) channels.
 - Support short/byte array input/output

## Demo

[Demo apk download link](https://github.com/junyuecao/AndroidSoundTouch/blob/master/app-release.apk?raw=true)

Note: You should grant audio record permission manually before using.

## Usage
Gradle:
```groovy
repositories {
    jcenter()
}

// For gradle plugin 2.x
dependencies {
    compile 'io.github.junyuecao:soundtouch:1.0.1'
}

// For gradle plugin 3.x
dependencies {
    implementation 'io.github.junyuecao:soundtouch:1.0.1'
}
```

The APIs are almost the same with the C/C++ version.
- in Java
```Java
    public void onVoiceStart() {
        mSoundTouch = new SoundTouch();
        mSoundTouch.setChannels(1);
        mSoundTouch.setSampleRate(VoiceRecorder.SAMPLE_RATE);
    }

    public void onVoice(byte[] data, int size) {
        mSoundTouch.setRate(mRate);
        mSoundTouch.setPitch(mPitch);
        mSoundTouch.putSamples(data, size);
        int bufferSize = 0
        do {
            bufferSize = mSoundTouch.receiveSamples(mTempBuffer, BUFFER_SIZE);
            if (bufferSize > 0) {
                mTestWavOutput.write(mTempBuffer, 0, bufferSize);
            }
        } while (bufferSize != 0);

    }

    public void onVoiceEnd() {
        mSoundTouch.release();
    }
```

