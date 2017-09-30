package io.github.junyuecao.androidsoundeffect

import android.media.AudioFormat
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.SeekBar
import io.github.junyuecao.soundtouch.SoundTouch
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder


class MainActivity : AppCompatActivity(), VoiceRecorder.Callback {
    val TAG = "MainActivity"
    private var mRecorder : VoiceRecorder? = null
    private var mSoundTouch : SoundTouch? = null
    private var mIsRecording = false
    private var mTestWavOutput: FileOutputStream? = null
    private val BUFFER_SIZE: Int = 4096
    private var mTempBuffer : ByteArray = ByteArray(BUFFER_SIZE)

    private var mPitch: Double = 1.0;
    private var mRate: Double = 1.0;

    override fun onVoiceStart() {
        mSoundTouch = SoundTouch()
        mSoundTouch?.setChannels(1)
        mSoundTouch?.setSampleRate(VoiceRecorder.SAMPLE_RATE)
        mTestWavOutput = getTestWavOutput()
        writeWavHeader(mTestWavOutput!!,
                AudioFormat.CHANNEL_IN_MONO,
                VoiceRecorder.SAMPLE_RATE,
                AudioFormat.ENCODING_PCM_16BIT);
    }

    override fun onVoice(data: ByteArray?, size: Int) {
        Log.d(TAG, "onVoice: $data, Size: $size")
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
        try {
            mTestWavOutput?.close()
            mTestWavOutput = null
        } catch (e: IOException) {
            e.printStackTrace()
        }
        updateWavHeader(getTempFile())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Example of a call to a native method
        mRecorder = VoiceRecorder(this)

        start.setOnClickListener {
            mIsRecording = !mIsRecording
            if (mIsRecording) {
                start.text = "Stop"
                mRecorder?.start()
            } else {
                start.text = "Start"
                mRecorder?.stop()
            }

        }

        play.setOnClickListener {
            val tempFile = getTempFile()
            if (tempFile.exists()) {
                val player = MediaPlayer.create(this, Uri.fromFile(tempFile))
                player.start()
            }
        }
        pitchText.text = "Pitch: $mPitch"
        pitch.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (pitch.progress > 500) {
                    mPitch = (3.0 / 500.0) * pitch.progress - 2
                } else{
                    mPitch = 0.75 / 500 * pitch.progress+ 0.25
                }
                pitchText.text = "Pitch: $mPitch"
            }

        })
        rateText.text = "Rate: $mRate"
        rate.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if (pitch.progress > 500) {
                    mRate = (3.0 / 500.0) * rate.progress - 2
                } else{
                    mRate = 0.75 / 500 * rate.progress+ 0.25
                }
                rateText.text = "Rate: $mRate"
            }
        })
    }

    private fun getTestWavOutput(): FileOutputStream? {
        val s = getTempFile()

        try {
            val os = FileOutputStream(s)
            return os
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            return null
        }

    }

    override fun onStop() {
        super.onStop()
        mIsRecording = false
        start.text = "Start"
        mRecorder?.stop()
    }

    private fun getTempFile() = File(getExternalFilesDir(null), "record_temp.wav")

    /**
     * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
     * Two size fields are left empty/null since we do not yet know the final stream size

     * @param out         The stream to write the header to
     * *
     * @param channelMask An AudioFormat.CHANNEL_* mask
     * *
     * @param sampleRate  The sample rate in hertz
     * *
     * @param encoding    An AudioFormat.ENCODING_PCM_* value
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeWavHeader(out: OutputStream, channelMask: Int, sampleRate: Int, encoding: Int) {
        val channels: Short
        when (channelMask) {
            AudioFormat.CHANNEL_IN_MONO -> channels = 1
            AudioFormat.CHANNEL_IN_STEREO -> channels = 2
            else -> throw IllegalArgumentException("Unacceptable channel mask")
        }

        val bitDepth: Short
        when (encoding) {
            AudioFormat.ENCODING_PCM_8BIT -> bitDepth = 8
            AudioFormat.ENCODING_PCM_16BIT -> bitDepth = 16
            AudioFormat.ENCODING_PCM_FLOAT -> bitDepth = 32
            else -> throw IllegalArgumentException("Unacceptable encoding")
        }

        writeWavHeader(out, channels, sampleRate, bitDepth)
    }

    /**
     * Writes the proper 44-byte RIFF/WAVE header to/for the given stream
     * Two size fields are left empty/null since we do not yet know the final stream size

     * @param out        The stream to write the header to
     * *
     * @param channels   The number of channels
     * *
     * @param sampleRate The sample rate in hertz
     * *
     * @param bitDepth   The bit depth
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun writeWavHeader(out: OutputStream, channels: Short, sampleRate: Int, bitDepth: Short) {
        // Convert the multi-byte integers to raw bytes in little endian format as required by the spec
        val littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels.toInt() * (bitDepth / 8))
                .putShort((channels * (bitDepth / 8)).toShort())
                .putShort(bitDepth)
                .array()

        // Not necessarily the best, but it's very easy to visualize this way
        out.write(byteArrayOf(
                // RIFF header
                'R'.toByte(), 'I'.toByte(), 'F'.toByte(), 'F'.toByte(), // ChunkID
                0, 0, 0, 0, // ChunkSize (must be updated later)
                'W'.toByte(), 'A'.toByte(), 'V'.toByte(), 'E'.toByte(), // Format
                // fmt subchunk
                'f'.toByte(), 'm'.toByte(), 't'.toByte(), ' '.toByte(), // Subchunk1ID
                16, 0, 0, 0, // Subchunk1Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // NumChannels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // ByteRate
                littleBytes[10], littleBytes[11], // BlockAlign
                littleBytes[12], littleBytes[13], // BitsPerSample
                // data subchunk
                'd'.toByte(), 'a'.toByte(), 't'.toByte(), 'a'.toByte(), // Subchunk2ID
                0, 0, 0, 0)// Subchunk2Size (must be updated later)
        )
    }

    /**
     * Updates the given wav file's header to include the final chunk sizes

     * @param wav The wav file to update
     * *
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun updateWavHeader(wav: File) {
        val sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // There are probably a bunch of different/better ways to calculate
                // these two given your circumstances. Cast should be safe since if the WAV is
                // > 4 GB we've already made a terrible mistake.
                .putInt((wav.length() - 8).toInt()) // ChunkSize
                .putInt((wav.length() - 44).toInt()) // Subchunk2Size
                .array()

        var accessWave: RandomAccessFile? = null

        try {
            accessWave = RandomAccessFile(wav, "rw")
            // ChunkSize
            accessWave.seek(4)
            accessWave.write(sizes, 0, 4)

            // Subchunk2Size
            accessWave.seek(40)
            accessWave.write(sizes, 4, 4)
        } catch (ex: IOException) {
            // Rethrow but we still close accessWave in our finally
            throw ex
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close()
                } catch (ex: IOException) {
                    //
                }

            }
        }
    }
}
