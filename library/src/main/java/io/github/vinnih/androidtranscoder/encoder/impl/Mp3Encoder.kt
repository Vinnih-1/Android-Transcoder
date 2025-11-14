package io.github.vinnih.androidtranscoder.encoder.impl

import android.util.Log
import com.naman14.androidlame.AndroidLame
import com.naman14.androidlame.LameBuilder
import io.github.vinnih.androidtranscoder.TAG
import io.github.vinnih.androidtranscoder.encoder.EncoderBase
import io.github.vinnih.androidtranscoder.extractor.WavReader
import io.github.vinnih.androidtranscoder.status.StatusProgress
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

internal class Mp3Encoder(
    val reader: WavReader,
    fileDir: String,
) : EncoderBase() {
    val outputFile: File =
        File(fileDir, "${reader.data.nameWithoutExtension}.mp3")
            .apply {
                this.createNewFile()
            }
    val fileOutputStream: FileOutputStream = FileOutputStream(outputFile)
    val androidLame: AndroidLame =
        LameBuilder()
            .apply {
                setInSampleRate(reader.sampleRate)
                setOutChannels(reader.channels)
                setOutBitrate(128)
                setOutSampleRate(reader.sampleRate)
                setQuality(5)
            }.build()

    override fun encode(progress: StatusProgress): File {
        val pcmBuffer = ByteArray(8192)
        val mp3Buffer = ByteArray(8192)
        var (bytesRead, totalBytesRead) = Pair(0, 0)

        Log.d(TAG, "Reading ${reader.data.nameWithoutExtension} and encoding to mp3.")

        while (
            reader.read(pcmBuffer).also {
                bytesRead = it
                totalBytesRead += it
            } > 0
        ) {
            val byteBuffer =
                ByteBuffer
                    .wrap(pcmBuffer)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer()
            val shortBuffer = ShortArray(byteBuffer.remaining())

            byteBuffer.get(shortBuffer, 0, byteBuffer.remaining())
            progress.updateEncodeProgress(reader.data.length(), totalBytesRead.toLong())

            val encoded = androidLame.encodeBufferInterLeaved(shortBuffer, (bytesRead / (2 * reader.channels)), mp3Buffer)

            fileOutputStream.write(mp3Buffer, 0, encoded)
        }
        val flush = androidLame.flush(mp3Buffer).also { Log.d(TAG, "Flushing final mp3 buffer.") }

        fileOutputStream.write(mp3Buffer, 0, flush)
        fileOutputStream.close()
        androidLame.close()

        return outputFile
    }
}
