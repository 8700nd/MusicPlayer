package com.example.scoutmusicplayer.ui.settings.widgets

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun SwitchPreference(
    title: String,
    state: Boolean,
    onStateChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.padding(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = state,
            onCheckedChange = onStateChanged
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SwitchPreferencePreview() {
    SwitchPreference(
        title = "123",
        state = true,
        onStateChanged = {}
    )
}