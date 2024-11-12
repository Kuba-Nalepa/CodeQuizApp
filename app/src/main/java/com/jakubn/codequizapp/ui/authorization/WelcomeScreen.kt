package com.jakubn.codequizapp.ui.authorization


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.navigation.Navigate
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.uiComponents.CustomButton

@Composable
fun WelcomeScreen(navController: NavController) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            )

    ) {
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.8f)
                .fillMaxHeight(0.75f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            WelcomeText("Code Quiz".uppercase())

            Text(
                textAlign = TextAlign.Center,
                style = Typography.bodyMedium,
                text = "Dive into code, break barriers, and rack up those points!"
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(25.dp, 25.dp, 0.dp, 0.dp))
                .background(Color(0xE6648097))
                .fillMaxHeight(0.25f),
            verticalArrangement = Arrangement.spacedBy(15.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF7F9FB8),
                text = "Sign Up",
                textColor = Color(0xFFA3FF0D),
                onClick = {
                    navController.navigate(Navigate.Registration.route)
                }
            )

            CustomButton(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFF7F9FB8),
                text = "Log In",
                textColor = Color(0xFFFFFFFF),
                onClick = {
                    navController.navigate(Navigate.Login.route)
                }
            )
        }

    }
}

@Composable
fun WelcomeText(text: String) {
    Text(
        modifier = Modifier.padding(bottom = 24.dp),
        textAlign = TextAlign.Center,
        text = text,
        style = Typography.titleLarge
    )
}

//@Preview(showBackground = true)
//@Composable
//private fun WelcomeScreenPreview() {
//    CodeQuizAppTheme {
//        WelcomeScreen()
//
//    }
//}