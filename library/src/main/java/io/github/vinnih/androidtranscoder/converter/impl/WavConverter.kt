package io.github.vinnih.androidtranscoder.converter.impl

import android.media.MediaCodec
import android.media.MediaFormat
import android.util.Log
import io.github.vinnih.androidtranscoder.converter.BaseConverter
import io.github.vinnih.androidtranscoder.converter.TAG
import io.github.vinnih.androidtranscoder.converter.TIMEOUT_US
import io.github.vinnih.androidtranscoder.types.AudioType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer

class WavConverter(
    inputFile: File,
    outputFile: File,
    onProgress: (Int) -> Unit,
    onSuccess: (File) -> Unit,
    onFailure: () -> Unit,
) : BaseConverter(
    audioType = AudioType.WAV,
    inputFile = inputFile,
    outputFile = outputFile,
    onProgress = onProgress,
    onSuccess = onSuccess,
    onFailure = onFailure,
) {
    private val bufferInfo = MediaCodec.BufferInfo()
    private val totalSize: Long = inputFile.length()
    private var progressCount: Int = 0
    private var byteSize: Long = 0L
    private var dataSize: Long = 0L
    private var isInputEOS: Boolean = false
    private var isOutputEOS: Boolean = false
    private val WAV_HEADER_SIZE: Int = 44
    private val file = RandomAccessFile(outputFile, "rw")

    override suspend fun execute(): File =
        withContext(Dispatchers.IO) {
            while (!isOutputEOS) {
                if (!isInputEOS) {
                    val inputBufferIndex = codec.dequeueInputBuffer(TIMEOUT_US)
                    if (inputBufferIndex >= 0) {
                        val inputBuffer = codec.getInputBuffer(inputBufferIndex)!!
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)

                        if (sampleSize < 0) {
                            codec.queueInputBuffer(inputBufferIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                            isInputEOS = true
                        } else {
                            codec.queueInputBuffer(inputBufferIndex, 0, sampleSize, extractor.sampleTime, 0)
                            extractor.advance()
                            byteSize += sampleSize
                        }
                    }
                }
                val outputBufferIndex = codec.dequeueOutputBuffer(bufferInfo, TIMEOUT_US)
                writeFile(outputBufferIndex)
            }
            writeHeader()
            return@withContext outputFile
        }

    private fun writeHeader() {
        val header = ByteArray(WAV_HEADER_SIZE)
        val bitsPerSample = 16
        val channels = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
        val sampleRate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
        val totalDataLen = dataSize + 36
        val byteRate = sampleRate * channels * (bitsPerSample / 8)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = ((totalDataLen shr 8) and 0xff).toByte()
        header[6] = ((totalDataLen shr 16) and 0xff).toByte()
        header[7] = ((totalDataLen shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * bitsPerSample / 8).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (dataSize and 0xff).toByte()
        header[41] = ((dataSize shr 8) and 0xff).toByte()
        header[42] = ((dataSize shr 16) and 0xff).toByte()
        header[43] = ((dataSize shr 24) and 0xff).toByte()

        file.seek(0)
        file.write(header)
        Log.d(TAG, "WAV header written successfully.")
    }

    private fun writeFile(
        index: Int,
    ) {
        when (index) {
            MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(TAG, "Output format changed: ${codec.outputFormat}")
            MediaCodec.INFO_TRY_AGAIN_LATER -> {
                Log.d(TAG, "No output buffer ready.")
                if (isInputEOS) Thread.sleep(10)
            }
            in 0..Int.MAX_VALUE -> {
                val outputBuffer: ByteBuffer = codec.getOutputBuffer(index)!!

                if (bufferInfo.size > 0) {
                    val pcmChunk = ByteArray(bufferInfo.size)
                    outputBuffer.get(pcmChunk)
                    file.seek(44)
                    file.write(pcmChunk)
                    dataSize += pcmChunk.size
                    outputBuffer.clear()
                }
                val percentage = ((byteSize.toDouble() / totalSize) * 100).toInt()
                val endOfStream = bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0

                if (percentage > progressCount) {
                    progressCount = percentage
                    onProgress(progressCount)
                }
                if (endOfStream) {
                    isOutputEOS = true
                    onProgress(100)
                    Log.d(TAG, "Output EOS detected. Decoding complete.")
                    onSuccess(outputFile)
                }
                codec.releaseOutputBuffer(index, false)
            }
            else -> {
                Log.w(TAG, "Unexpected output index: $index")
                onFailure.invoke()
            }
        }
    }
}
