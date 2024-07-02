package com.example.scoutmusicplayer.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scoutmusicplayer.ui.settings.widgets.CheckBoxPreference

@Composable
fun SettingsScreen(
    onLoggingEnabledChanged1: (Boolean) -> Unit = {},
    onLoggingEnabledChanged2: (Boolean) -> Unit = {},
) {
    val logEnabledState1 = remember { mutableStateOf(false) }
    val logEnabledState2 = remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.Start) {
        CheckBoxPreference(
            title = "Enable Logging1",
            summary = "Enable detailed logging of app activities",
            checked = logEnabledState1.value,
            onCheckedChange = {
                logEnabledState1.value = it
                onLoggingEnabledChanged1(it)
            }
        )

        CheckBoxPreference(
            title = "Enable Logging2",
            summary = "Enable detailed logging of app activities",
            checked = logEnabledState2.value,
            onCheckedChange = {
                logEnabledState2.value = it
                onLoggingEnabledChanged2(it)
            }
        )

    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}