package com.jakubn.codequizapp.ui.uiComponents

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jakubn.codequizapp.theme.Typography

@Composable
fun ClickableCustomText(text: String, onCLick: () -> Unit) {
    Text(
        modifier = Modifier.clickable { onCLick() }.padding(2.dp),
        text = text,
        style = Typography.bodyMedium
    )
}