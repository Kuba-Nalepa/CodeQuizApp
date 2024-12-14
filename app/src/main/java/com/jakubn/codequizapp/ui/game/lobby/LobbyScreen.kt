package com.jakubn.codequizapp.ui.game.lobby

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.theme.CodeQuizAppTheme
import com.jakubn.codequizapp.theme.Typography

@Composable
fun LobbyScreen() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Image(
            modifier = Modifier
                .fillMaxHeight(0.2f)
                .zIndex(1f)
                .padding(top = 20.dp),
            painter = painterResource(R.drawable.icon_login), contentDescription = "Logo"
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(2f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {

            // TODO: fetching valid data
            PlayerContainer("Booba", 2, 100, null)

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "VS",
                style = Typography.titleLarge,
                textAlign = TextAlign.Center
            )

            // TODO: fetching valid data
            PlayerContainer("Marczan", 1, 50, null)

        }
    }
}

@Composable
fun PlayerContainer(
    userName: String,
    userRanking: Int,
    userWinRatio: Int,
    userImgUri: String?
) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .background(Color(0xB33495AC), RoundedCornerShape(20.dp))
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .offset(y = (-20).dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xB33495AC))
                .padding(20.dp)
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Max)
                .zIndex(1f),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            AsyncImage(
                modifier = Modifier
                    .size(125.dp)
                    .shadow(5.dp, shape = CircleShape)
                    .border(1.dp, Color.Black, CircleShape),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(userImgUri)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.sample_avatar),
                contentDescription = stringResource(R.string.app_name),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Text(text = userName, style = Typography.labelMedium, softWrap = true)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_rank),
                        contentDescription = "Rank",
                        tint = Color(0xFFA3FF0D)
                    )
                    Text(
                        text = " $userRanking",
                        color = Color(0xFFA3FF0D),
                        style = Typography.labelLarge,
                        softWrap = true
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_ranking_up),
                        contentDescription = "Rank"
                    )
                    Text(text = " $userWinRatio %", style = Typography.labelLarge, softWrap = true)

                }

            }
        }

    }
}

@Preview
@Composable
fun LobbyScreenPreview() {
    CodeQuizAppTheme {
        LobbyScreen()
    }
}