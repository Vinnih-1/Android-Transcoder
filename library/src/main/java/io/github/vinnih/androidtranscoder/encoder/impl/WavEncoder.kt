package io.github.vinnih.androidtranscoder.encoder.impl

import io.github.vinnih.androidtranscoder.encoder.EncoderBase
import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.androidtranscoder.status.StatusProgress
import java.io.File

internal class WavEncoder(
    val reader: WavReader,
    fileDir: String,
) : EncoderBase() {
    val outputFile =
        File(fileDir, reader.data.name)
            .apply { createNewFile() }

    override fun encode(progress: StatusProgress): File {
        reader.data
            .copyTo(outputFile, overwrite = true)

        return outputFile
    }
}
