package io.github.vinnih.app.ui.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.app.R
import io.github.vinnih.app.ui.components.SongType.AAC
import io.github.vinnih.app.ui.components.SongType.FLAC
import io.github.vinnih.app.ui.components.SongType.M4A
import io.github.vinnih.app.ui.components.SongType.MP3
import io.github.vinnih.app.ui.components.SongType.WAV
import io.github.vinnih.app.ui.home.FakeHomeController
import io.github.vinnih.app.ui.home.HomeController
import io.github.vinnih.app.ui.theme.AppTheme
import java.io.File

enum class SongType(
    val icon: Int,
    val contentDescription: String,
) {
    MP3(icon = R.drawable.mp3, contentDescription = ""),
    WAV(icon = R.drawable.wav, contentDescription = ""),
    M4A(icon = R.drawable.m4a, contentDescription = ""),
    AAC(icon = R.drawable.aac, contentDescription = ""),
    FLAC(icon = R.drawable.flac, contentDescription = ""),
}

fun getByExtension(extension: String): SongType =
    when (extension.lowercase()) {
        "mp3" -> MP3
        "wav" -> WAV
        "m4a" -> M4A
        "aac" -> AAC
        "flac" -> FLAC
        else -> throw IllegalArgumentException()
    }

@Composable
fun SongCard(
    title: String,
    extension: String,
    file: File,
    controller: HomeController,
    onClick: () -> Unit,
) {
    var openModal by remember { mutableStateOf(false) }
    val songType = getByExtension(extension)

    when {
        openModal ->
            SongModal(songType = songType, file = file, onDismiss = {
                openModal = false
            }, controller = controller)
    }

    Card(
        modifier =
            Modifier.width(128.dp).height(128.dp).pointerInput(Unit) {
                detectTapGestures(
                    onTap = {
                        onClick()
                    },
                    onLongPress = {
                        openModal = true
                    },
                )
            },
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                modifier = Modifier.size(48.dp),
                painter = painterResource(songType.icon),
                contentDescription = songType.contentDescription,
            )
            Text(
                modifier = Modifier.padding(top = 16.dp),
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview
@Composable
fun SongCardPreview() {
    val audioFile = File("F:\\Android\\AndroidTranscoder\\Audio Example.MP3")

    AppTheme {
        SongCard(
            title = audioFile.nameWithoutExtension,
            extension = audioFile.extension,
            file = audioFile,
            controller = FakeHomeController(),
            onClick = {},
        )
    }
}
