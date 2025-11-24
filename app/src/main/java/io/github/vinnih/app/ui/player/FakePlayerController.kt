package io.github.vinnih.app.ui.player

import android.content.Context
import androidx.media3.common.Player
import kotlinx.coroutines.flow.StateFlow
import java.io.File

class FakePlayerController : PlayerController {
    override val player: StateFlow<Player?>
        get() = TODO("Not yet implemented")

    override fun init(context: Context) {
        TODO("Not yet implemented")
    }

    override fun setMedia(file: File) {
        TODO("Not yet implemented")
    }

    override fun play() {
        TODO("Not yet implemented")
    }

    override fun seekTo(position: Float) {
        TODO("Not yet implemented")
    }

    override fun seekToPrevious() {
        TODO("Not yet implemented")
    }

    override fun seekToNext() {
        TODO("Not yet implemented")
    }
}
