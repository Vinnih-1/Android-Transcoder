package io.github.vinnih.androidtranscoder.converter

import io.github.vinnih.androidtranscoder.converter.impl.WavConverter
import io.github.vinnih.androidtranscoder.types.AudioType
import java.io.File

class ConverterManager {
    companion object {
        suspend fun convert(input: File, output: File, to: AudioType, progress: (Int) -> Unit, success: (File) -> Unit, error: () -> Unit) {
            when(to) {
                AudioType.MP3 -> TODO()
                AudioType.WAV -> WavConverter(input, output, progress, success, error).execute()
            }
        }
    }
}