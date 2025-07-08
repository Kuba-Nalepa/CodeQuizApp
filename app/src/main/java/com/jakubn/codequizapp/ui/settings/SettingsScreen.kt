package com.jakubn.codequizapp.ui.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.uiComponents.ClickableCustomText


@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    logOut: () -> Unit
) {

    val colors = arrayOf(
        0.06f to Color(0xffA3FF0D),
        0.22f to Color(0xff74B583),
        0.39f to Color(0xff58959A),
        0.62f to Color(0xff003963),
        0.95f to Color(0xff000226)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colorStops = colors))
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "<CODE/QUIZ>",
            style = Typography.bodyMedium,
            color = Color(0xff7BAFC4)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(modifier = Modifier.size(44.dp),
                onClick = {}
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_music_off),
                    contentDescription = "Avatar image placeholder"
                )
            }


            IconButton(modifier = Modifier.size(54.dp),
                onClick = {}
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_notifications_off),
                    contentDescription = "Avatar image placeholder"
                )
            }

        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x66F1F1F1)),
                onClick = { },
                contentPadding = PaddingValues(horizontal = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Image(
                        modifier = Modifier.size(50.dp),
                        painter = painterResource(R.drawable.generic_avatar),
                        contentDescription = "Avatar image placeholder"
                    )

                    Text(
                        text = "Account",
                        textAlign = TextAlign.Center,
                        color = Color.White,
                        softWrap = true
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 68.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(34.dp)
        ) {

            val context = LocalContext.current

            val textLabelsWithActions = mapOf(
                "Language" to {
                    Toast.makeText(context, "Language clicked", Toast.LENGTH_SHORT).show()
                },
                "Dark Mode" to {
                    Toast.makeText(context, "Dark mode clicked", Toast.LENGTH_SHORT).show()
                },
                "About" to {
                    Toast.makeText(context, "About clicked", Toast.LENGTH_SHORT).show()
                },
                "Help" to {
                    Toast.makeText(context, "Help clicked", Toast.LENGTH_SHORT).show()
                },
                "Delete account" to {
                    Toast.makeText(context, "Account deletion not available", Toast.LENGTH_SHORT).show()
                },
                "Sign out" to {
                    Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                }
            )

            textLabelsWithActions.forEach { (label, action) ->
                ClickableCustomText(label) {
                    action()
                }
            }
        }
    }
}