package io.github.vinnih.app.ui.home

import android.widget.Toast
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
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.vinnih.app.ui.components.AppBottomBar
import io.github.vinnih.app.ui.components.AppTopBar
import io.github.vinnih.app.ui.components.SongCard
import io.github.vinnih.app.ui.player.FakePlayerController
import io.github.vinnih.app.ui.player.PlayerController
import io.github.vinnih.app.ui.player.PlayerScreen
import io.github.vinnih.app.ui.theme.AppTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    homeController: HomeController,
    playerController: PlayerController,
) {
    val context = LocalContext.current
    val state = rememberPullToRefreshState()
    val scope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    var showPlayerScreen by remember { mutableStateOf(false) }
    val files = homeController.files.collectAsState()
    val player = playerController.player.collectAsState().value!!

    LaunchedEffect(Unit) {
        homeController.refreshFiles(context)
    }

    Scaffold(topBar = {
        AppTopBar()
    }, bottomBar = {
        AppBottomBar(controller = playerController, onClick = {
            if (player.currentMediaItem == null) {
                Toast.makeText(context, "There's nothing playing yet!", Toast.LENGTH_SHORT).show()
            } else {
                showPlayerScreen = true
            }
        })
    }) { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onRefresh = {
                scope.launch {
                    isRefreshing = true
                    homeController.refreshFiles(context)
                    isRefreshing = false
                }
            },
            isRefreshing = isRefreshing,
        ) {
            HorizontalDivider(thickness = 2.dp, modifier = Modifier.fillMaxWidth())

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
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 10.dp),
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
                            controller = homeController,
                            onClick = {
                                playerController.setMedia(it)
                                playerController.play()
                                showPlayerScreen = true
                            },
                        )
                    }
                }
            }
        }

        if (showPlayerScreen) {
            PlayerScreen(controller = playerController) { showPlayerScreen = false }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    AppTheme {
        HomeScreen(homeController = FakeHomeController(), playerController = FakePlayerController())
    }
}
