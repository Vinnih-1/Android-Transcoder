package io.github.vinnih.androidtranscoder

import io.github.vinnih.androidtranscoder.converter.ConverterManager
import io.github.vinnih.androidtranscoder.types.AudioType
import java.io.File

class AndroidTranscoder(
    val file: File,
    val to: AudioType
){
    private val outputFile: File = File(file.parent, file.nameWithoutExtension.plus(to.value))

    suspend fun converter(
        progress: (Int) -> Unit = {},
        success: (File) -> Unit = {},
        error: () -> Unit = {}
    ) {
        if (!outputFile.exists()) outputFile.createNewFile()
        ConverterManager.convert(file, outputFile, to, progress, success, error)
    }
}