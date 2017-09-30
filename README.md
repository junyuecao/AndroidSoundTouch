# AndroidSoundTouch
A SoundTouch java wrapper library for Android

This repo is a java wrapper of [SoundTouch](https://www.surina.net/soundtouch/) for Android.

 - Keep APIs of soundtouch and provide a Java class to process audio streams so you can do it without any C/C++ code.
 - Support ENCODING_PCM_16BIT samples.
 - Support mono(1)/stereo(2) channels.
 - Support short/byte array input/output

## Usage
The APIs are almost the same with the C/C++ version.
- in kotlin
```
    override fun onVoiceStart() {
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(VoiceRecorder.SAMPLE_RATE)
    }

    override fun onVoice(data: ByteArray?, size: Int) {
        mSoundTouch?.setRate(mRate)
        mSoundTouch?.setPitch(mPitch)
        mSoundTouch?.putSamples(data, size)
        var bufferSize = 0
        do {
            bufferSize = mSoundTouch!!.receiveSamples(mTempBuffer, BUFFER_SIZE)
            if (bufferSize > 0) {
                mTestWavOutput?.write(mTempBuffer, 0, bufferSize)
            }
        } while (bufferSize != 0)

    }

    override fun onVoiceEnd() {
        mSoundTouch?.release()
    }
```

