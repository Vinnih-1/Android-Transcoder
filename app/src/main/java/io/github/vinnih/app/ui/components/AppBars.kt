package io.github.vinnih.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.app.R
import io.github.vinnih.app.ui.player.FakePlayerController
import io.github.vinnih.app.ui.player.PlayerController
import io.github.vinnih.app.ui.theme.AppTheme
import io.github.vinnih.app.utils.formatTime
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    CenterAlignedTopAppBar(title = { Text(text = stringResource(R.string.app_name)) })
}

@Composable
fun AppBottomBar(
    controller: PlayerController,
    onClick: () -> Unit,
) {
    val context = LocalContext.current
    val player = controller.player.collectAsState().value!!
    val playing = player.currentMediaItem
    var songPosition by remember { mutableFloatStateOf(0f) }
    val currentPositionMs = (songPosition * player.duration).toLong()
    val totalDurationMs = player.duration

    LaunchedEffect(Unit) {
        while (true) {
            if (player.isPlaying) {
                val current = player.currentPosition.toFloat()
                val duration = player.duration.toFloat()

                songPosition = (current / duration)
            }
            delay(500)
        }
    }

    Column {
        LinearProgressIndicator(
            modifier = Modifier.fillMaxWidth(),
            progress = { songPosition },
            drawStopIndicator = {},
        )
        BottomAppBar(modifier = Modifier.fillMaxWidth().height(128.dp)) {
            OutlinedButton(
                modifier = Modifier.fillMaxSize().height(128.dp),
                onClick = onClick,
                border = null,
                shape = RectangleShape,
                contentPadding = PaddingValues(0.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().height(128.dp),
                ) {
                    Text(
                        text =
                            (
                                if (playing !=
                                    null
                                ) {
                                    playing.mediaMetadata.title
                                } else {
                                    "Nothing playing"
                                }
                            ).toString(),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 20.dp).align(Alignment.CenterStart),
                    )
                    Text(
                        text = "${currentPositionMs.formatTime()} / ${totalDurationMs.formatTime()}",
                        modifier = Modifier.padding(end = 20.dp).align(Alignment.CenterEnd),
                    )
                }
            }
        }
    }
}

@Preview(showSystemUi = false, showBackground = true)
@Composable
fun AppTopBarPreview() {
    AppTheme {
        AppTopBar()
    }
}

@Preview(showBackground = true)
@Composable
fun AppBottomBarPreview() {
    AppTheme {
        AppBottomBar(controller = FakePlayerController(), onClick = {})
    }
}
