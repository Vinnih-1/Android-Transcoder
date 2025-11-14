package io.github.vinnih.androidtranscoder.encoder

import io.github.vinnih.androidtranscoder.status.StatusProgress
import java.io.File

internal abstract class EncoderBase {
    abstract fun encode(progress: StatusProgress): File
}
