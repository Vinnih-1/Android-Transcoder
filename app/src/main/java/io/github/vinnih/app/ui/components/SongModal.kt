package io.github.vinnih.app.ui.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.vinnih.app.R
import io.github.vinnih.app.ui.home.FakeHomeController
import io.github.vinnih.app.ui.home.HomeController
import io.github.vinnih.app.ui.theme.AppTheme
import java.io.File

@Composable
fun SongModal(
    songType: SongType,
    file: File,
    onDismiss: () -> Unit,
    controller: HomeController,
) {
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(SongType.entries.first { it.name != songType.name }) }
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var isConverting by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Dialog(
        onDismissRequest =
            if (!isConverting) {
                onDismiss
            } else {
                {}
            },
    ) {
        Card(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(230.dp),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceAround,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            42.dp,
                            Alignment.CenterHorizontally,
                        ),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        modifier = Modifier.size(56.dp),
                        painter = painterResource(songType.icon),
                        contentDescription = songType.contentDescription,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Icon(
                        modifier = Modifier.size(42.dp),
                        painter = painterResource(R.drawable.arrow_right),
                        contentDescription = "Arrow right",
                        tint = Color.Black,
                    )
                    Icon(
                        modifier = Modifier.size(56.dp),
                        painter = painterResource(selected.icon),
                        contentDescription = selected.contentDescription,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
                TextButton(
                    shape = RectangleShape,
                    onClick = { expanded = !expanded },
                ) {
                    Text(
                        text = if (isConverting) file.nameWithoutExtension else selected.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        SongType.entries
                            .filter { it.name != songType.name }
                            .forEach {
                                DropdownMenuItem(text = { Text(it.name) }, onClick = {
                                    selected = it
                                    expanded = false
                                })
                            }
                    }
                }
                LoadingButton(
                    onClick = {
                        isConverting = true
                        Toast
                            .makeText(
                                context,
                                "Converting to ${selected.name} format...",
                                Toast.LENGTH_SHORT,
                            ).show()
                        controller
                            .convertFile(context = context, file = file, to = selected, progress = {
                                currentProgress = it.toFloat() / 100
                            }) {
                                isConverting = false
                                currentProgress = 0f
                                onDismiss()
                            }
                    },
                    text = "Convert to ${selected.name}",
                    progress = currentProgress,
                    enable = !isConverting,
                    modifier =
                        Modifier
                            .height(
                                52.dp,
                            ).width(256.dp),
                )
            }
        }
    }
}

@Preview
@Composable
fun SongModalPreview() {
    val audioFile = File("F:\\Android\\AndroidTranscoder\\Audio Example.MP3")

    AppTheme {
        SongModal(songType = SongType.MP3, file = audioFile, onDismiss = {
        }, controller = FakeHomeController())
    }
}
