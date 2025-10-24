package io.github.vinnih.androidtranscoder.converter.impl

import android.media.MediaCodec
import android.util.Log
import io.github.vinnih.androidtranscoder.converter.BaseConverter
import io.github.vinnih.androidtranscoder.converter.TAG
import io.github.vinnih.androidtranscoder.converter.TIMEOUT_US
import io.github.vinnih.androidtranscoder.types.AudioType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

class WavConverter(
    inputFile: File,
    outputFile: File,
    onProgress: (Int) -> Unit,
    onSuccess: (File) -> Unit,
    onFailure: () -> Unit
) : BaseConverter(
    audioType = AudioType.WAV,
    inputFile = inputFile,
    outputFile = outputFile,
    onProgress = onProgress,
    onSuccess = onSuccess,
    onFailure = onFailure
) {
    override suspend fun execute(): File = withContext(Dispatchers.IO) {
        val fileOutputStream = FileOutputStream(outputFile)
        var isInputEOS = false
        var isOutputEOS = false
        val bufferInfo = MediaCodec.BufferInfo()
        val totalSize = inputFile.length()
        var byteSize = 0L
        var progress = 0

        while(!isOutputEOS) {
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

            when (outputBufferIndex) {
                MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> Log.d(TAG, "Output format changed: ${codec.outputFormat}")
                MediaCodec.INFO_TRY_AGAIN_LATER -> {
                    Log.d(TAG, "No output buffer ready.")
                    if (isInputEOS) Thread.sleep(10)
                }
                in 0..Int.MAX_VALUE -> {
                    val outputBuffer: ByteBuffer = codec.getOutputBuffer(outputBufferIndex)!!

                    if (bufferInfo.size > 0) {
                        val pcmChunk = ByteArray(bufferInfo.size)
                        outputBuffer.get(pcmChunk)
                        fileOutputStream.write(pcmChunk)
                        outputBuffer.clear()
                    }
                    val percentage = ((byteSize.toDouble() / totalSize) * 100).toInt()
                    val endOfStream = bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0

                    if (percentage > progress) {
                        progress = percentage
                        onProgress(progress)
                    }
                    if (endOfStream) {
                        isOutputEOS = true
                        onProgress(100)
                        Log.d(TAG, "Output EOS detected. Decoding complete.")
                        onSuccess(outputFile)
                    }
                    codec.releaseOutputBuffer(outputBufferIndex, false)
                }
                else -> {
                    Log.w(TAG, "Unexpected output index: $outputBufferIndex")
                    onFailure.invoke()
                }
            }
        }
        return@withContext outputFile
    }
}