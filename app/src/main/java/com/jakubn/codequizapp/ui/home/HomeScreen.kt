package com.jakubn.codequizapp.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.theme.Typography
import java.util.Locale

@Composable
fun HomeScreen(
    user: User,
    homeViewModel: HomeViewModel = hiltViewModel(),
    createGame: () -> Unit,
    playGame: () -> Unit
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "<CODE/QUIZ>",
                style = Typography.bodyMedium,
                color = Color(0xff7BAFC4)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "hi ${user.name}".lowercase(Locale.ROOT),
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 2
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "you have already".lowercase(Locale.ROOT),
                    style = Typography.bodyLarge,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 20.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color(0x52D9D9D9)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${user.gamesPlayed}",
                style = Typography.titleMedium,
                fontSize = 44.sp
            )
            Text(
                modifier = Modifier.padding(vertical = 20.dp),
                text = "games\nplayed",
                style = Typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(Color(0x52D9D9D9)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    text = "${user.wins}",
                    style = Typography.titleMedium,
                    color = Color(0xffA3FF0D)
                )
                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = "wins",
                    style = Typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(20.dp))
                    .background(Color(0x52D9D9D9)),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp),
                    text = "${user.gamesPlayed - user.wins}",
                    style = Typography.titleMedium
                )

                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = "losses",
                    style = Typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
        }


        Button(
            modifier = Modifier.padding(vertical = 20.dp),
            onClick = {

            }
        ) {
            Image(
                modifier = Modifier,
                painter = painterResource(R.drawable.ic_players),
                contentDescription = "Friends icon"
            )
            Text(
                modifier = Modifier.padding(horizontal = 20.dp),
                text = "friends",
                textAlign = TextAlign.Center,
                color = Color.Black,
                style = Typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                modifier = Modifier,
                text = "${user.friends?.size ?: 0}",  // TODO but later
                textAlign = TextAlign.Center,
                color = Color.Black,
                style = Typography.titleMedium
            )
        }

        Text(
            modifier = Modifier.padding(bottom = 20.dp),
            text = "Ready to test your programming skills?",
            style = Typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x52FFFFFF)),
                shape = RoundedCornerShape(20.dp),
                onClick = createGame

            ) {
                Text(
                    text = "Create Game".uppercase(),
                    textAlign = TextAlign.Center,
                    color = Color(0xffA3FF0D),
                    style = Typography.bodyLarge,
                    softWrap = true
                )
            }
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                onClick = playGame,
            ) {
                Text(
                    text = "Play Now".uppercase(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF000000),
                    style = Typography.bodyLarge,
                    softWrap = true

                )
            }
        }
    }
}
