package io.github.vinnih.androidtranscoder.utils

import io.github.vinnih.androidtranscoder.extractor.AudioExtractor
import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.androidtranscoder.status.StatusProgress
import java.io.File

fun File.toWavReader(cacheDir: File): WavReader =
    AudioExtractor(
        this,
        cacheDir.absolutePath,
    ).extract(
        progress =
            StatusProgress(-1, progress = {
            }),
    )
