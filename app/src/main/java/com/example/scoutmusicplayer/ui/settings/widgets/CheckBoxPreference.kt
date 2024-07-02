package com.example.scoutmusicplayer.ui.settings.widgets


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun CheckBoxPreference(
    title: String,
    summary: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        Column() {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            Text(text = summary, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCheckBoxPreference() {
    val checkedState = remember { mutableStateOf(true) }

    CheckBoxPreference(
        title = "Enable Logging",
        summary = "Logs additional information for debugging.",
        checked = checkedState.value,
        onCheckedChange = { checkedState.value = it }
    )
}
