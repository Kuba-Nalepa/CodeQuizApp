package com.jakubn.codequizapp.ui.uiComponents.helperDialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jakubn.codequizapp.theme.Typography

@Composable
fun SelectionDialog(
    text: String,
    options: List<String>,
    currentOption: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedOption by remember { mutableStateOf(currentOption) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text, style = Typography.bodyLarge)
        },
        text = {
            Column {
                options.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedOption = option
                            }
                            .padding(vertical = 4.dp)
                    ) {
                        RadioButton(
                            selected = (selectedOption == option),
                            onClick = { selectedOption = option }
                        )
                        Text(text = option)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                onConfirm(selectedOption)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun InfoDialog(title: String, message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = Typography.bodyLarge) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) { Text("OK") }
        }
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, style = Typography.bodyLarge) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) { Text(confirmText) }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
