package io.github.vinnih.androidtranscoder.extractor

import android.util.Log
import java.io.File
import java.io.RandomAccessFile

internal class WavReader(
    val channels: Int,
    val sampleRate: Int,
    val dataSize: Int,
    val data: File,
) {
    private val randomAccessFile = RandomAccessFile(data, "rw")

    init {
        writeHeader()
    }

    private fun writeHeader() {
        val header = ByteArray(44)
        val bitsPerSample = 16
        val totalDataLength = dataSize + 36
        val byteRate = sampleRate * channels * (bitsPerSample / 8)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        header[4] = (totalDataLength and 0xff).toByte()
        header[5] = ((totalDataLength shr 8) and 0xff).toByte()
        header[6] = ((totalDataLength shr 16) and 0xff).toByte()
        header[7] = ((totalDataLength shr 24) and 0xff).toByte()
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        header[16] = 16
        header[17] = 0
        header[18] = 0
        header[19] = 0
        header[20] = 1
        header[21] = 0
        header[22] = channels.toByte()
        header[23] = 0
        header[24] = (sampleRate and 0xff).toByte()
        header[25] = ((sampleRate shr 8) and 0xff).toByte()
        header[26] = ((sampleRate shr 16) and 0xff).toByte()
        header[27] = ((sampleRate shr 24) and 0xff).toByte()
        header[28] = (byteRate and 0xff).toByte()
        header[29] = ((byteRate shr 8) and 0xff).toByte()
        header[30] = ((byteRate shr 16) and 0xff).toByte()
        header[31] = ((byteRate shr 24) and 0xff).toByte()
        header[32] = (channels * bitsPerSample / 8).toByte()
        header[33] = 0
        header[34] = bitsPerSample.toByte()
        header[35] = 0
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        header[40] = (dataSize and 0xff).toByte()
        header[41] = ((dataSize shr 8) and 0xff).toByte()
        header[42] = ((dataSize shr 16) and 0xff).toByte()
        header[43] = ((dataSize shr 24) and 0xff).toByte()

        randomAccessFile.seek(0)
        randomAccessFile.write(header)
        Log.d(TAG, "WAV header written successfully.")
    }

    fun read(buf: ByteArray): Int = randomAccessFile.read(buf)
}
