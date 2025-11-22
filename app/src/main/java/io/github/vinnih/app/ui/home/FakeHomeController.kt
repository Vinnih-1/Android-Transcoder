package io.github.vinnih.app.ui.home

import android.content.Context
import io.github.vinnih.app.ui.components.SongType
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

class FakeHomeController : HomeController {
    override val files: MutableStateFlow<List<File>>
        get() = MutableStateFlow(listOf(File("F:\\Android\\AndroidTranscoder\\Audio Example.MP3")))

    override suspend fun refreshFiles(context: Context) {
        TODO("Not yet implemented")
    }

    override fun convertFile(
        context: Context,
        file: File,
        to: SongType,
        progress: (Int) -> Unit,
        success: (File) -> Unit,
    ) {
        TODO("Not yet implemented")
    }
}
