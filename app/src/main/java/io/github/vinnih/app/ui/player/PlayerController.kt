package io.github.vinnih.app.ui.player

import android.content.Context
import androidx.media3.common.Player
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface PlayerController {
    val player: StateFlow<Player?>

    fun init(context: Context)

    fun setMedia(file: File)

    fun play()

    fun seekTo(position: Float)

    fun seekToPrevious()

    fun seekToNext()
}
