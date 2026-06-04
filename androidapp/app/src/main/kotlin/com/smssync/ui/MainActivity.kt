package com.smssync.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import android.content.Context
import com.smssync.ui.logs.LogsScreen
import com.smssync.ui.logs.LogsViewModel
import com.smssync.ui.settings.SettingsScreen
import com.smssync.ui.settings.SettingsViewModel

enum class Screen {
    LOGS, SETTINGS
}

@Composable
fun MainScreen(context: Context) {
    val currentScreen = remember { mutableStateOf(Screen.LOGS) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen.value) {
            Screen.LOGS -> {
                LogsScreen(
                    viewModel = LogsViewModel(context),
                    onSettingsClick = { currentScreen.value = Screen.SETTINGS }
                )
            }

            Screen.SETTINGS -> {
                SettingsScreen(
                    viewModel = SettingsViewModel(context),
                    onBack = { currentScreen.value = Screen.LOGS }
                )
            }
        }
    }
}
