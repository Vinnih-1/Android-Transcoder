package io.github.vinnih.androidtranscoder.extractor

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import io.github.vinnih.androidtranscoder.exceptions.AudioTrackNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile

const val TIMEOUT_US = 10000L
const val TAG = "AudioExtractor"

internal class AudioExtractor(
    val inputFile: File,
    cacheDir: String,
) {
    val outputFile: File =
        File(cacheDir, "${inputFile.nameWithoutExtension}.wav")
            .apply {
                this.createNewFile()
            }
    val fileOutputStream: RandomAccessFile = RandomAccessFile(outputFile, "rw")
    val mediaExtractor: MediaExtractor = MediaExtractor()
    var mediaCodec: MediaCodec
    var mediaFormat: MediaFormat

    init {
        mediaExtractor.setDataSource(inputFile.absolutePath).also { mediaExtractor.selectTrack(findAudioTrack()) }
        mediaFormat = mediaExtractor.getTrackFormat(findAudioTrack())
        mediaCodec = MediaCodec.createDecoderByType(mediaFormat.getString(MediaFormat.KEY_MIME)!!)
        mediaCodec.configure(mediaFormat, null, null, 0).also { mediaCodec.start() }
        fileOutputStream.seek(44)
    }

    suspend fun extract(): WavReader =
        withContext(Dispatchers.IO) {
            var isOutEOS = false
            var isInEOS = false
            val bufferInfo = MediaCodec.BufferInfo()
            var dataSize = 0

            while (!isOutEOS) {
                if (!isInEOS) {
                    val indexInputBuffer = mediaCodec.dequeueInputBuffer(TIMEOUT_US)

                    if (indexInputBuffer > 0) {
                        val inputBuffer = mediaCodec.getInputBuffer(indexInputBuffer)
                        val sampleSize = mediaExtractor.readSampleData(inputBuffer!!, 0)

                        if (sampleSize < 0) {
                            mediaCodec.queueInputBuffer(indexInputBuffer, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            isInEOS = true
                        } else {
                            mediaCodec.queueInputBuffer(indexInputBuffer, 0, sampleSize, mediaExtractor.sampleTime, 0)
                            mediaExtractor.advance()
                            dataSize += sampleSize
                        }
                    }
                }

                when (val indexOutputBuffer = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)) {
                    MediaCodec.INFO_TRY_AGAIN_LATER -> {
                        Log.d(TAG, "No output buffer ready.")
                        Thread.sleep(10)
                    }

                    in 0..Int.MAX_VALUE -> {
                        val outputBuffer = mediaCodec.getOutputBuffer(indexOutputBuffer)!!

                        if (bufferInfo.size > 0) {
                            val pcmChunk = ByteArray(bufferInfo.size)
                            outputBuffer.get(pcmChunk)
                            fileOutputStream.write(pcmChunk, 0, pcmChunk.size)
                            dataSize += pcmChunk.size
                            outputBuffer.clear()
                        }

                        if (bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0) {
                            isOutEOS = true
                            Log.d(TAG, "Output EOS detected. Decoding complete.")
                        }
                        mediaCodec.releaseOutputBuffer(indexOutputBuffer, false)
                    }
                }
            }
            val channels = mediaFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
            val sampleRate = mediaFormat.getInteger(MediaFormat.KEY_SAMPLE_RATE)

            return@withContext WavReader(channels, sampleRate, dataSize, outputFile)
        }

    fun findAudioTrack(): Int {
        for (i in 0 until mediaExtractor.trackCount) {
            val format = mediaExtractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                return i
            }
        }
        throw AudioTrackNotFoundException("Audio track not found")
    }
}
