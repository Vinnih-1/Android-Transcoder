package io.github.vinnih.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import io.github.vinnih.app.ui.home.HomeScreen
import io.github.vinnih.app.ui.home.HomeViewModel
import io.github.vinnih.app.ui.player.PlayerViewModel
import io.github.vinnih.app.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private val playerViewModel: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        playerViewModel.init(this.applicationContext)

        setContent {
            AppTheme {
                HomeScreen(
                    homeController = homeViewModel,
                    playerController = playerViewModel,
                )
            }
        }
    }
}
