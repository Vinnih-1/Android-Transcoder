package io.github.vinnih.app.ui.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.vinnih.androidtranscoder.AndroidTranscoder
import io.github.vinnih.androidtranscoder.types.AudioType
import io.github.vinnih.app.ui.components.SongType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class HomeViewModel : ViewModel() {
    var files = MutableStateFlow(listOf<File>())

    suspend fun refreshFiles(context: Context) =
        withContext(Dispatchers.IO) {
            delay(500)
            files.update {
                context.filesDir
                    .listFiles { filter ->
                        SongType.entries.any {
                            it.name.equals(filter.extension, ignoreCase = true)
                        }
                    }!!
                    .map { it }
            }
        }

    fun convertFile(
        context: Context,
        file: File,
        progress: (Int) -> Unit,
        success: (File) -> Unit,
    ) {
        viewModelScope.launch {
            val file =
                AndroidTranscoder(
                    context = context,
                    file = file,
                    to = AudioType.MP3,
                    progress = progress,
                ).convert()

            success(file)
        }
    }
}
