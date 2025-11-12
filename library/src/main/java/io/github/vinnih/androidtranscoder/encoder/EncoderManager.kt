package io.github.vinnih.androidtranscoder.encoder

import io.github.vinnih.androidtranscoder.encoder.impl.Mp3Encoder
import io.github.vinnih.androidtranscoder.encoder.impl.WavEncoder
import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.androidtranscoder.types.AudioType
import java.io.File

internal class EncoderManager {
    companion object {
        suspend fun convert(
            reader: WavReader,
            to: AudioType,
            fileDir: String,
        ): File {
            val file =
                when (to) {
                    AudioType.MP3 -> Mp3Encoder(reader, fileDir).encode()
                    AudioType.WAV -> WavEncoder(reader, fileDir).encode()
                    AudioType.M4A -> TODO()
                    AudioType.AAC -> TODO()
                    AudioType.FLAC -> TODO()
                }

            return file
        }

        fun checkCompatibility(file: File): Boolean {
            val extension = file.extension
            return AudioType.entries
                .map { it.value.removePrefix(".") }
                .any { it.equals(extension, ignoreCase = true) }
        }
    }
}
