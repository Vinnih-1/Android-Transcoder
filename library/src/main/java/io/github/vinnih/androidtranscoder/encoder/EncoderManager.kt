package io.github.vinnih.androidtranscoder.encoder

import io.github.vinnih.androidtranscoder.types.AudioType
import java.io.File

class EncoderManager {
    companion object {
        suspend fun convert(
            input: File,
            output: File,
            to: AudioType,
            progress: (Int) -> Unit,
            success: (File) -> Unit,
            error: () -> Unit,
        ) {
            when (to) {
                AudioType.MP3 -> TODO()
                else -> {}
            }
        }
    }
}
