package io.github.junyuecao.soundtouch;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Soundtouch Java wrapper
 */
public class SoundTouch {
    private static final String TAG = "SoundTouch";

    static {
        System.loadLibrary("native-lib");
        init(SoundTouch.class.getCanonicalName().replaceAll("\\.", "/"));
    }

    private long handle = 0; // 持有native代码中的对象

    public SoundTouch() {
        handle = newInstance();
    }

    /**
     * 释放对象
     */
    public synchronized void release() {
        handle = 0;
        deleteInstance(handle);
    }

    /**
     * Adds 'numSamples' pcs of samples from the 'samples' memory position into
     * the input of the object.
     *
     * @param samples      采样数组
     * @param samplesCount 放入数量
     */
    public void putSamples(short[] samples, int samplesCount) {
        if (handle == 0) {
            return;
        }
        putSamples(handle, samples, samplesCount);
    }

    /**
     * Adds 'numSamples' pcs of samples from the 'samples' memory position into
     * the input of the object.
     *
     * @param samples      采样数组
     * @param size 放入数量
     */
    public void putSamples(byte[] samples, int size) {
        if (handle == 0) {
            return;
        }
        short[] buffer = new short[size / 2];
        ByteBuffer.wrap(samples)
                .order(ByteOrder.LITTLE_ENDIAN)
                .asShortBuffer().get(buffer);
        putSamples(handle, buffer, size / 2);
    }

    /**
     * Output samples from beginning of the sample buffer. Copies requested samples to
     * output buffer and removes them from the sample buffer. If there are less than
     * 'numsample' samples in the buffer, returns all that available.
     *
     * @param outputSamples  接收采样的数组
     * @param maxSampleCount 最大接收数量, 字节数
     * @return Number of samples returned.
     */
    public int receiveSamples(short[] outputSamples, int maxSampleCount) {
        if (handle == 0) {
            return 0;
        }

        return receiveSamples(handle, outputSamples, maxSampleCount);
    }

    /**
     * Adjusts book-keeping so that given number of samples are removed from beginning of the
     * sample buffer without copying them anywhere.
     * Used to reduce the number of samples in the buffer when accessing the sample buffer directly
     * with 'ptrBegin' function.
     *
     * @param maxSampleCount count
     * @return Number of samples returned.
     */
    public int receiveSamples(int maxSampleCount) {
        if (handle == 0) {
            return 0;
        }

        return receiveSamples(handle, maxSampleCount);
    }

    /**
     * Sets new rate control value. Normal rate = 1.0, smaller values
     * represent slower rate, larger faster rates.
     *
     * @param rate rate value
     */
    public void setRate(double rate) {
        if (handle == 0) {
            return;
        }
        setRate(handle, rate);
    }

    /**
     * Sets new pitch control value. Original pitch = 1.0, smaller values
     * represent lower pitches, larger values higher pitch.
     *
     * @param pitch pitch value
     */
    public void setPitch(double pitch) {
        if (handle == 0) {
            return;
        }
        setPitch(handle, pitch);
    }

    /**
     * Sets new tempo control value. Normal tempo = 1.0, smaller values
     * represent slower tempo, larger faster tempo.
     *
     * @param newTempo tempo value
     */
    public void setTempo(double newTempo) {
        if (handle == 0) {
            return;
        }
        setTempo(handle, newTempo);
    }

    /**
     * Sets new rate control value as a difference in percents compared
     * to the original rate (-50 .. +100 %)
     *
     * @param rate new rate
     */
    public void setRateChange(double rate) {
        if (handle == 0) {
            return;
        }
        setRateChange(handle, rate);
    }

    /**
     * Sets new tempo control value as a difference in percents compared
     * to the original tempo (-50 .. +100 %)
     *
     * @param tempoChange new tempo
     */
    public void setTempoChange(double tempoChange) {
        if (handle == 0) {
            return;
        }
        setTempoChange(handle, tempoChange);
    }

    /**
     * Sets pitch change in octaves compared to the original pitch
     * (-1.00 .. +1.00)
     *
     * @param pitchOctaves new pitch
     */
    public void setPitchOctaves(double pitchOctaves) {
        if (handle == 0) {
            return;
        }
        setPitchOctaves(handle, pitchOctaves);
    }

    /**
     * Sets pitch change in semi-tones compared to the original pitch
     * (-12 .. +12)
     *
     * @param pitchSemiTones new pitch
     */
    public void setPitchSemiTones(int pitchSemiTones) {
        if (handle == 0) {
            return;
        }
        setPitchSemiTones(handle, pitchSemiTones);
    }

    /**
     * Sets pitch change in semi-tones compared to the original pitch
     * (-12 .. +12)
     *
     * @param pitchSemiTones new pitch
     */
    public void setPitchSemiTones(double pitchSemiTones) {
        if (handle == 0) {
            return;
        }
        setPitchSemiTones(handle, pitchSemiTones);
    }

    /**
     * Sets the number of channels, 1 = mono, 2 = stereo
     *
     * @param channels channels count
     */
    public void setChannels(int channels) {
        if (handle == 0) {
            return;
        }
        setChannels(handle, channels);
    }

    /**
     * Sets sample rate.
     *
     * @param sampleRate sample rate
     */
    public void setSampleRate(int sampleRate) {
        if (handle == 0) {
            return;
        }
        setSampleRate(handle, sampleRate);
    }

    /**
     * Get ratio between input and output audio durations, useful for calculating
     * processed output duration: if you'll process a stream of N samples, then
     * you can expect to get out N * getInputOutputSampleRatio() samples.
     */
    public double getInputOutputSampleRatio() {
        if (handle == 0) {
            return 1;
        }
        return getInputOutputSampleRatio(handle);
    }

    /**
     * Flushes the last samples from the processing pipeline to the output.
     * Clears also the internal processing buffers.
     * Note: This function is meant for extracting the last samples of a sound
     * stream. This function may introduce additional blank samples in the end
     * of the sound stream, and thus it's not recommended to call this function
     * in the middle of a sound stream.
     */
    public void flush() {
        if (handle == 0) {
            return;
        }
        flush(handle);
    }

    /**
     * Returns number of samples currently available.
     */
    public int numSamples() {
        return numSamples(handle);
    }

    /**
     * Returns number of channels
     */
    public int numChannels() {
        return numChannels(handle);
    }

    /**
     * Returns number of unprocessed samples
     */
    public int numUnprocessedSamples() {
        return numUnprocessedSamples(handle);
    }

    /**
     * Returns nonzero if there aren't any samples available for outputting.
     */
    public int isEmpty() {
        return isEmpty(handle);
    }

    /**
     * Clears all the samples in the object's output and internal processing
     * buffers.
     */
    public void clear() {
        clear(handle);
    }


    /**
     * Get SoundTouch library version Id
     *
     * @return version id
     */
    public static native int getVersionId();

    /**
     * Get SoundTouch library version string
     *
     * @return version string
     */
    public static native String getVersionString();


    /// ------ native methods start ------

    private static native long newInstance();

    private native long deleteInstance(long handle);

    private native void putSamples(long handle, short[] samples, int samplesCount);

    private native int receiveSamples(long handle, short[] outputSamples, int maxSampleCount);

    private native int receiveSamples(long handle, int maxSampleCount);

    private native void setRate(long handle, double rate);

    private native void setPitch(long handle, double pitch);

    private native void setTempo(long handle, double newTempo);

    private native void setRateChange(long handle, double rate);

    private native void setTempoChange(long handle, double tempoChange);

    private native void setPitchOctaves(long handle, double pitchOctaves);

    private native void setPitchSemiTones(long handle, int pitchSemiTones);

    private native void setPitchSemiTones(long handle, double pitchSemiTones);

    private native void setChannels(long handle, int channels);

    private native void setSampleRate(long handle, int sampleRate);

    private native double getInputOutputSampleRatio(long handle);

    private native void flush(long handle);

    private native int numSamples(long handle);

    private native int numChannels(long handle);

    private native int numUnprocessedSamples(long handle);

    private native int isEmpty(long handle);

    private native void clear(long handle);

    private static native int init(String canonicalName);
}
