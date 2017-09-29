/*
 * Copyright 2016 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.junyuecao.androidsoundeffect;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

/**
 * 麦克风采集
 * Continuously records audio and notifies the {@link VoiceRecorder.Callback} when voice (or any
 * sound) is heard.
 *
 * <p>The recorded audio format is always {@link AudioFormat#ENCODING_PCM_16BIT} and
 * {@link AudioFormat#CHANNEL_IN_MONO}. This class will automatically pick the right sample rate
 * for the device. Use {@link #getSampleRate()} to get the selected value.</p>
 */
@RequiresApi(18)
public class VoiceRecorder {

//    private static final int[] SAMPLE_RATE_CANDIDATES = new int[]{44100};

    private static final int CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;

//    private static final int AMPLITUDE_THRESHOLD = 1500;
//    private static final int SPEECH_TIMEOUT_MILLIS = 2000;
    private static final int MAX_SPEECH_LENGTH_MILLIS = 30 * 1000;
    private final Callback mCallback;
    private final Object mLock = new Object();
    private AudioRecord mAudioRecord;

    private Thread mThread;

    private byte[] mBuffer;
    /** The timestamp of the last time that voice is heard. */
    private long mLastVoiceHeardMillis = Long.MAX_VALUE;
    /** The timestamp when the current voice is started. */
    private long mVoiceStartedMillis;

    public VoiceRecorder(@NonNull Callback callback) {
        mCallback = callback;
    }

    /**
     * Starts recording audio.
     *
     * <p>The caller is responsible for calling {@link #stop()} later.</p>
     */
    public void start() {
        // Stop recording if it is currently ongoing.
        stop();
        // Try to create a new recording session.
        mAudioRecord = createAudioRecord();
        if (mAudioRecord == null) {
            throw new RuntimeException("Cannot instantiate VoiceRecorder");
        }
        // Start recording.
        mAudioRecord.startRecording();
        // Start processing the captured audio.
        mThread = new Thread(new ProcessVoice());
        mThread.start();
    }

    /**
     * Stops recording audio.
     */
    public void stop() {
        if (mThread != null) {
            mThread.interrupt();
            mThread = null;
        }
        synchronized (mLock) {
            dismiss();
            if (mAudioRecord != null) {
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
            mBuffer = null;
        }
    }

    /**
     * Dismisses the currently ongoing utterance.
     */
    public void dismiss() {
        if (mLastVoiceHeardMillis != Long.MAX_VALUE) {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            mCallback.onVoiceEnd();
        }
    }

    /**
     * Retrieves the sample rate currently used to record audio.
     *
     * @return The sample rate of recorded audio.
     */
    public int getSampleRate() {
        if (mAudioRecord != null) {
            return mAudioRecord.getSampleRate();
        }
        return 0;
    }

    /**
     * Creates a new {@link AudioRecord}.
     *
     * @return A newly created {@link AudioRecord}, or null if it cannot be created (missing
     * permissions?).
     */
    private AudioRecord createAudioRecord() {
//        for (int sampleRate : SAMPLE_RATE_CANDIDATES) {
        final int sizeInBytes = AudioRecord.getMinBufferSize(44100, CHANNEL, ENCODING);
        if (sizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
            return null;
        }
        final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                44100, CHANNEL, ENCODING, sizeInBytes);
        if (audioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            mBuffer = new byte[sizeInBytes];
            return audioRecord;
        } else {
            audioRecord.release();
        }
//        }
        return null;
    }

    public static abstract interface Callback {

        /**
         * Called when the recorder starts hearing voice.
         */
        void onVoiceStart();

        /**
         * Called when the recorder is hearing voice.
         *
         * @param data The audio data in {@link AudioFormat#ENCODING_PCM_16BIT}.
         * @param size The size of the actual data in {@code data}.
         */
        void onVoice(byte[] data, int size);

        /**
         * Called when the recorder stops hearing voice.
         */
        void onVoiceEnd();
    }

    /**
     * Continuously processes the captured audio and notifies {@link #mCallback} of corresponding
     * events.
     */
    private class ProcessVoice implements Runnable {

        @Override
        public void run() {
            while (true) {
                synchronized (mLock) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                    if(mAudioRecord == null)
                        continue;
                    if(mBuffer == null)
                        continue;
                    final int size = mAudioRecord.read(mBuffer, 0, mBuffer.length);
                    final long now = System.currentTimeMillis();
                    if (mLastVoiceHeardMillis == Long.MAX_VALUE) {
                        mVoiceStartedMillis = now;
                        mCallback.onVoiceStart();
                    }
                    mCallback.onVoice(mBuffer, size);
                    mLastVoiceHeardMillis = now;
                    if (now - mVoiceStartedMillis > MAX_SPEECH_LENGTH_MILLIS) {
                        end();
                    }

                }
            }
        }

        private void end() {
            mLastVoiceHeardMillis = Long.MAX_VALUE;
            mCallback.onVoiceEnd();
        }

    }

}
