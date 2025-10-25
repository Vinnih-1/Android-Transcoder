package io.github.vinnih.androidtranscoder.converter

import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import android.util.Log
import io.github.vinnih.androidtranscoder.exceptions.AudioTrackNotFoundException
import io.github.vinnih.androidtranscoder.types.AudioType
import java.io.File

const val TAG = "AudioTranscoder"
const val TIMEOUT_US = 10000L

abstract class BaseConverter(
    val audioType: AudioType,
    val inputFile: File,
    val outputFile: File,
    val onProgress: (Int) -> Unit = {},
    val onSuccess: (File) -> Unit = {},
    val onFailure: () -> Unit = {}
) {
    val extractor = MediaExtractor()
    var codec: MediaCodec
    var format: MediaFormat

    abstract suspend fun execute(): File

    init {
        extractor.setDataSource(inputFile.absolutePath)
        val trackIndex = findAudioTrack()
        format = extractor.getTrackFormat(trackIndex)
        codec = MediaCodec.createDecoderByType(format.getString(MediaFormat.KEY_MIME)!!)
        extractor.selectTrack(trackIndex)
        codec.configure(extractor.getTrackFormat(trackIndex), null, null, 0)
        codec.start()
    }

    private fun findAudioTrack(): Int {
        for (i in 0 until extractor.trackCount) {
            val format = extractor.getTrackFormat(i)
            val mime = format.getString(MediaFormat.KEY_MIME)
            if (mime?.startsWith("audio/") == true) {
                return i
            }
        }
        throw AudioTrackNotFoundException()
    }
}
