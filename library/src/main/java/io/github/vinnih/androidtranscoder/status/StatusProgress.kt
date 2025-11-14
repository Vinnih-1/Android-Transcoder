package io.github.vinnih.androidtranscoder.status

import kotlin.math.ceil

internal class StatusProgress(
    val stepsValue: Int,
    val progress: (Int) -> Unit,
) {
    private var previousDecode: Int = 0
    private var previousEncode: Int = 0
    private var decodeProgress: Int = 0
    private var encodeProgress: Int = 0
    private var status: Status = Status.DECODING

    fun updateProgressValue() {
        if (status == Status.DECODING) {
            if (previousDecode == decodeProgress) return
        }
        if (status == Status.ENCODING) {
            if (previousEncode == encodeProgress) return
        }
        previousDecode = decodeProgress
        previousEncode = encodeProgress

        progress(decodeProgress + encodeProgress)
    }

    fun calculeProgress(
        totalValue: Long,
        currentValue: Long,
    ): Int {
        updateProgressValue()

        return ceil(((currentValue.toFloat() / totalValue.toFloat()) * 100)).toInt()
    }

    fun updateEncodeProgress(
        totalValue: Long,
        currentValue: Long,
    ) {
        encodeProgress = calculeProgress(totalValue, currentValue) / stepsValue
        status = Status.ENCODING
    }

    fun updateDecodeProgress(
        totalValue: Long,
        currentValue: Long,
    ) {
        decodeProgress = calculeProgress(totalValue, currentValue) / stepsValue
        status = Status.DECODING
    }
}
