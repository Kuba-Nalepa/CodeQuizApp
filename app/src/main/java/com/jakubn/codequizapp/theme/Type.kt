package com.jakubn.codequizapp.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.jakubn.codequizapp.R

val ZenDotsFamily = FontFamily(Font(R.font.zendots_regular, FontWeight.Black))

// Set of Material typography styles to start with
val Typography = Typography(
    titleLarge = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 52.sp,  // Extended large dimension for title
        letterSpacing = 0.sp,
        color = Primary
        
    ),
    titleMedium = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 24.sp,  // Medium dimension for title
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        color = Color.White
        
    ),
    titleSmall = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 18.sp,  // Small dimension for title
        lineHeight = 24.sp,
        letterSpacing = 0.sp,
        color = Color.White
        
    ),

    // Body text
    bodyLarge = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 20.sp,  // Large dimension for body text
        lineHeight = 28.sp,
        letterSpacing = 0.5.sp,
        color = Color.White
        
    ),
    bodyMedium = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 16.sp,  // Medium dimension for body text
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        color = Color.White
        
    ),
    bodySmall = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 14.sp,  // Small dimension for body text
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        color = Color.White
        
    ),

    // Labels
    labelLarge = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 16.sp,  // Large dimension for label (e.g., button text)
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,
        color = Color.White
        
    ),
    labelMedium = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 14.sp,  // Medium dimension for label
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        color = Color.White
        
    ),
    labelSmall = TextStyle(
        fontFamily = ZenDotsFamily,
        fontSize = 12.sp,  // Small dimension for label
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        color = Color.White
        
    )
)

