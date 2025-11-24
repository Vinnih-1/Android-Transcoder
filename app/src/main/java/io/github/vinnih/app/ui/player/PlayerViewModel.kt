package io.github.vinnih.app.ui.player

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File

class PlayerViewModel :
    ViewModel(),
    PlayerController {
    private var _player: MutableStateFlow<Player?> = MutableStateFlow(null)
    override val player = _player.asStateFlow()

    override fun init(context: Context) =
        _player.update {
            ExoPlayer.Builder(context).build()
        }

    override fun setMedia(file: File) {
        val metadata: MediaMetadata =
            MediaMetadata
                .Builder()
                .setTitle(file.name)
                .setDisplayTitle(
                    file.nameWithoutExtension,
                ).build()
        val mediaItem =
            MediaItem
                .fromUri(
                    Uri.fromFile(file),
                ).buildUpon()
                .setMediaMetadata(metadata)
                .build()

        player.value?.setMediaItem(mediaItem)
        player.value?.prepare()
    }

    override fun play() {
        player.value?.play()
    }

    override fun seekTo(position: Float) {
        if (player.value == null) return

        val duration = player.value!!.duration.toFloat() * position
        player.value!!.seekTo(duration.toLong())
    }

    override fun seekToPrevious() {
        player.value?.seekToPrevious()
    }

    override fun seekToNext() {
        player.value?.seekToNext()
    }
}
