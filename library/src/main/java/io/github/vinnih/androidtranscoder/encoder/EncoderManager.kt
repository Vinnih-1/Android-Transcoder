package io.github.vinnih.androidtranscoder.encoder

import android.content.Context
import io.github.vinnih.androidtranscoder.encoder.impl.Mp3Encoder
import io.github.vinnih.androidtranscoder.encoder.impl.WavEncoder
import io.github.vinnih.androidtranscoder.exceptions.IncompatibleAudioTypeException
import io.github.vinnih.androidtranscoder.extractor.AudioExtractor
import io.github.vinnih.androidtranscoder.status.StatusProgress
import io.github.vinnih.androidtranscoder.types.AudioType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class EncoderManager(
    val file: File,
    context: Context,
    val to: AudioType,
    progress: (progress: Int) -> Unit,
) {
    private val cacheDir = context.cacheDir.absolutePath
    private val filesDir = context.filesDir.absolutePath
    private val statusProgress = StatusProgress(if (to != AudioType.WAV) 2 else 1, progress)

    suspend fun convert(): File =
        withContext(Dispatchers.IO) {
            if (!checkCompatibility(file)) {
                throw IncompatibleAudioTypeException("Incompatible Audio Type")
            }
            val reader = AudioExtractor(file, cacheDir).extract(statusProgress)
            val file =
                when (to) {
                    AudioType.MP3 -> Mp3Encoder(reader, filesDir).encode(statusProgress)
                    AudioType.WAV -> WavEncoder(reader, filesDir).encode(statusProgress)
                    AudioType.M4A -> TODO()
                    AudioType.AAC -> TODO()
                    AudioType.FLAC -> TODO()
                }
            reader.dispose()

            return@withContext file
        }

    fun checkCompatibility(file: File): Boolean {
        val extension = file.extension
        return AudioType.entries
            .map { it.value.removePrefix(".") }
            .any { it.equals(extension, ignoreCase = true) }
    }
}
