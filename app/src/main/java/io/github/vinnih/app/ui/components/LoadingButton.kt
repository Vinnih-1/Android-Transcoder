package io.github.vinnih.app.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.app.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingButton(
    modifier: Modifier,
    onClick: () -> Unit,
    text: String,
    progress: Float,
    enable: Boolean,
) {
    val backgroundColor = MaterialTheme.colorScheme.primary
    val progressColor = MaterialTheme.colorScheme.secondary
    val contentColor = MaterialTheme.colorScheme.onPrimary

    Button(
        enabled = enable,
        modifier = modifier,
        onClick = onClick,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                disabledContainerColor = progressColor.copy(alpha = 0.4f),
            ),
        contentPadding = PaddingValues(0.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LinearProgressIndicator(
                trackColor = Color.Transparent,
                drawStopIndicator = {
                },
                progress = {
                    progress
                },
                modifier =
                    Modifier.fillMaxHeight().fillMaxWidth(
                        fraction = progress,
                    ),
                color = progressColor,
            )
            Text(text = text, Modifier.align(Alignment.Center), color = contentColor)
        }
    }
}

@Preview
@Composable
fun LoadingButtonPreview() {
    AppTheme {
        LoadingButton(
            onClick = {
            },
            text = "My Button",
            progress = 0.5f,
            enable = false,
            modifier =
                Modifier
                    .height(
                        64.dp,
                    ).width(256.dp),
        )
    }
}
