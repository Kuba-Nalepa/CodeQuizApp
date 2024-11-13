package com.jakubn.codequizapp.ui.authorization


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.navigation.Navigate
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.uiComponents.CustomTextField

@Composable
fun LoginScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val colors = arrayOf(
        0f to Color(0xff004469),
        0.5f to Color(0xff3D8B8C),
        1f to Color(0xffA3FF0D)
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colorStops = colors))
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            modifier = Modifier
                .fillMaxHeight(0.2f),
            painter = painterResource(R.drawable.icon_login), contentDescription = "Logo"
        )

        Text(
            style = Typography.titleLarge,
            text = "Hello".uppercase(),
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Column(
            modifier = Modifier
                .fillMaxHeight(0.7f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomTextField(
                modifier = Modifier
                    .fillMaxWidth(0.7f),
                label = "Email",
                value = email,
                onValueChange = { email = it }
            )

            CustomTextField(
                modifier = Modifier
                    .fillMaxWidth(0.5f),
                label = "Password",
                value = password,
                onValueChange = { password = it },
                isPassword = true
            )

            Text(
                modifier = Modifier.padding(vertical = 20.dp),
                style = Typography.bodyMedium,
                text = "Forgot password?",
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }

        Text(
            modifier = Modifier.padding(vertical = 20.dp),
            style = Typography.bodyMedium,
            text = "Lorem ipsum dolor sit amet",
            color = Color(0xff044560),
            textAlign = TextAlign.Center
        )

        Button(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.5f)
            .shadow(
                elevation = 5.dp,
                shape = RoundedCornerShape(50.dp),
                spotColor = Color.Black
            )
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.Crop
            ),
            colors = ButtonDefaults.buttonColors(Color.Transparent),
            onClick = {
                navController.navigate(route = Navigate.Home.route)
            }
        ) {
            Text(text = "Sign In".uppercase(), color = Color(0xffA3FF0D), style = Typography.bodyLarge)
        }
    }
}

@Preview
@Composable
private fun PreviewLoginScreen() {
    CodeQuizAppTheme {
        val navController = rememberNavController()
        LoginScreen(navController)
    }
}
