package com.mehmetalan.voicerecorder

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.mehmetalan.voicerecorder.screens.AudioRecorderApp
import com.mehmetalan.voicerecorder.screens.VoiceList

@Composable
fun NavGraph(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable(route = "home") {
            AudioRecorderApp(
                navController = navController
            )
        }
        composable(route = "voiceList") {
            VoiceList(
                navController = navController
            )
        }
    }
}