package io.github.vinnih.androidtranscoder

import android.content.Context
import io.github.vinnih.androidtranscoder.encoder.EncoderManager
import io.github.vinnih.androidtranscoder.types.AudioType
import java.io.File

const val TAG = "AndroidTranscoder"

class AndroidTranscoder(
    val file: File,
    val to: AudioType,
    val context: Context,
    val progress: (progress: Int) -> Unit,
) {
    suspend fun convert(): File = EncoderManager(file, context, to, progress).convert()
}
