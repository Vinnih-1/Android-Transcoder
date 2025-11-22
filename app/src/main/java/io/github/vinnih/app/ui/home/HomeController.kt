package io.github.vinnih.app.ui.home

import android.content.Context
import io.github.vinnih.app.ui.components.SongType
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File

interface HomeController {
    val files: MutableStateFlow<List<File>>

    suspend fun refreshFiles(context: Context)

    fun convertFile(
        context: Context,
        file: File,
        to: SongType,
        progress: (Int) -> Unit,
        success: (File) -> Unit,
    )
}
