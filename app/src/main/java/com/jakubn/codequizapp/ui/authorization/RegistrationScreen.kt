package com.jakubn.codequizapp.ui.authorization


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.uiComponents.CustomButton
import com.jakubn.codequizapp.ui.uiComponents.CustomTextField

@Composable
fun RegistrationScreen(navHostController: NavHostController, viewModel: AuthViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val buttonState by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFA3FF0D)),
    ) {

        Column(
            modifier = Modifier
                .shadow(
                    elevation = 5.dp,
                    shape = RoundedCornerShape(0.dp, 0.dp, 50.dp, 50.dp),
                    spotColor = Color.Black
                )
                .clip(shape = RoundedCornerShape(0.dp, 0.dp, 50.dp, 50.dp))
                .paint(
                    painterResource(R.drawable.background_auth),
                    contentScale = ContentScale.FillBounds
                )
                .weight(0.85f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center,
                    text = "Create account".uppercase(),
                    style = Typography.titleLarge,
                    fontSize = 38.sp
                )

                Text(
                    textAlign = TextAlign.Center,
                    style = Typography.bodyMedium,
                    text = "Your mind is key to success",
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape = RoundedCornerShape(50.dp))
                    .background(Color(0xE6648097))
                    .weight(0.50f),
                verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    CustomTextField(
                        label = "Name",
                        value = name,
                        onValueChange = { name = it })
                    CustomTextField(
                        label = "Email",
                        value = email,
                        onValueChange = { email = it })
                    CustomTextField(
                        label = "Password",
                        value = password,
                        onValueChange = { password = it },
                        isPassword = true
                    )
                }

            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.15f),
            verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                backgroundColor = Color(0xFF002137),
                text = "Sign Up",
                textColor = Color(0xFFFFFFFF),
                enabled = authState !is CustomState.Loading,
                onClick = {
                    viewModel.signUpUser(name, email, password)
                }
            )
        }
    }

    LaunchedEffect(authState, context, buttonState) {
        when(authState) {
            is CustomState.Success -> {
                navHostController.navigate(route = Screen.Login.route)
                viewModel.resetState()
            }

            is CustomState.Failure -> {
                val message = (authState as CustomState.Failure).message

                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    when(authState) {
        CustomState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

            }
        }
        else -> {}
    }
}

@Preview(showBackground = true)
@Composable
private fun RegistrationScreenPreview() {
    CodeQuizAppTheme {
        val navController = rememberNavController()
        RegistrationScreen(navController)

    }
}