package com.jakubn.codequizapp.ui.uiComponents

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.theme.Typography

@Composable
fun CustomButton(
    modifier: Modifier,
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Button(
        modifier = modifier
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
        onClick = onClick,
    ) {

        Text(modifier = Modifier.padding(top = 2.dp, bottom = 2.dp),color = textColor, text = text.uppercase(), style = Typography.bodyLarge)
    }
}