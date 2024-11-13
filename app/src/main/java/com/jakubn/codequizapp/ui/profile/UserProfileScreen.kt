package com.jakubn.codequizapp.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.jakubn.codequizapp.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jakubn.codequizapp.theme.Typography

@Composable
fun UserProfileScreen() {
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
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 20.dp),
                text = "<CODE/QUIZ>",
                style = Typography.bodyMedium,
                color = Color(0xff7BAFC4)
            )

            Image(
                modifier = Modifier
                    .size(125.dp)
                    .shadow(5.dp, shape = CircleShape)
                    .border(1.dp, Color.Black, CircleShape),
                painter = painterResource(R.drawable.sample_avatar),
                contentScale = ContentScale.Crop,
                contentDescription = "Avatar Image"
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 5.dp),
                text = "user name",
                style = Typography.bodyMedium,
            )
            Text(
                modifier = Modifier
                    .padding(bottom = 20.dp),
                text = "# 213",
                style = Typography.bodyMedium,
                color = Color(0xffA3FF0D)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(intrinsicSize = IntrinsicSize.Min)
                .padding(top = 20.dp, bottom = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x52FFFFFF)),
                shape = RoundedCornerShape(20.dp),
                onClick = {

                },
            ) {
                Text(
                    text = "Add Friend".uppercase(),
                    textAlign = TextAlign.Center,
                    color = Color(0xffA3FF0D),
                    style = Typography.bodyMedium,
                    softWrap = true
                )
            }
            Button(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                shape = RoundedCornerShape(20.dp),
                onClick = {

                },
            ) {
                Text(
                    text = "Chalenge".uppercase(),
                    textAlign = TextAlign.Center,
                    color = Color(0xff000000),
                    style = Typography.bodyMedium,
                    softWrap = true

                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, bottom = 20.dp)
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color(0x52D9D9D9)),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "50", style = Typography.titleMedium, fontSize = 44.sp)
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
                    text = "25", style = Typography.titleMedium, color = Color(0xffA3FF0D)
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
                    text = "25", style = Typography.titleMedium
                )
                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = "losses",
                    style = Typography.titleSmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewProfileScreen() {
    UserProfileScreen()
}
