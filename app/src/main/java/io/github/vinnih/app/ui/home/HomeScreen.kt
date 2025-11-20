package io.github.vinnih.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import io.github.vinnih.app.ui.components.SongCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel) {
    val context = LocalContext.current
    val state = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val files = viewModel.files.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.refreshFiles(context)
    }

    Scaffold { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    viewModel.refreshFiles(context)
                    isRefreshing = false
                }
            },
            isRefreshing = isRefreshing,
        ) {
            if (files.value.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text("There's no files here yet.")
                }

                return@PullToRefreshBox
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                FlowRow(
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fraction = 0.98f)
                            .verticalScroll(rememberScrollState()),
                    horizontalArrangement =
                        Arrangement.spacedBy(9.5.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    maxItemsInEachRow = 3,
                ) {
                    files.value.forEach {
                        SongCard(
                            title = it.nameWithoutExtension,
                            extension = it.extension,
                            file = it,
                            viewModel = viewModel,
                        )
                    }
                }
            }
        }
    }
}
