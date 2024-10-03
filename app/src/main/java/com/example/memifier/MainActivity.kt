package com.example.memifier

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.memifier.ui.theme.MemifierTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MemifierTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "splash_screen") {
                    composable("splash_screen") { SplashScreen(navController) }
                    composable("main_screen") { MainScreen(navController) }
                    composable("video_selection") { VideoSelectionScreen(navController) }
                    composable("video_playback/{uri}") { backStackEntry ->
                        // Obtendo o URI passado como argumento
                        val uriString = backStackEntry.arguments?.getString("uri")
                        uriString?.let { uri ->
                            VideoPlaybackScreen(navController, Uri.parse(uri))
                        }
                    }
                }
            }
        }
    }
}
