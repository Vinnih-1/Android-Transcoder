package io.github.vinnih.androidtranscoder.encoder.impl

import io.github.vinnih.androidtranscoder.extractor.WavReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

internal class WavEncoder(
    val reader: WavReader,
    fileDir: String,
) {
    val outputFile =
        File(fileDir, reader.data.name)
            .apply { createNewFile() }

    suspend fun encode(): File =
        withContext(Dispatchers.IO) {
            reader.data
                .copyTo(outputFile, overwrite = true)

            return@withContext outputFile
        }
}
