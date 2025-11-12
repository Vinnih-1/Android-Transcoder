package io.github.vinnih.androidtranscoder

import android.content.Context
import io.github.vinnih.androidtranscoder.encoder.EncoderManager
import io.github.vinnih.androidtranscoder.exceptions.IncompatibleAudioTypeException
import io.github.vinnih.androidtranscoder.extractor.AudioExtractor
import io.github.vinnih.androidtranscoder.types.AudioType
import java.io.File

const val TAG = "AndroidTranscoder"

class AndroidTranscoder(
    val file: File,
    val to: AudioType,
    val context: Context,
) {
    suspend fun converter(): File {
        if (!EncoderManager.checkCompatibility(file)) {
            throw IncompatibleAudioTypeException("Incompatible Audio Type")
        }
        val reader = AudioExtractor(file, context.cacheDir.absolutePath).extract()
        val file = EncoderManager.convert(reader, to, context.filesDir.absolutePath)

        reader.dispose()

        return file
    }
}
