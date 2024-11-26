package com.jakubn.codequizapp.ui.chat


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
    import coil.compose.rememberAsyncImagePainter


@Composable
fun ChatScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111418))
            .padding(horizontal = 16.dp)
    ) {
        // Top bar
        TopBar()

        Spacer(modifier = Modifier.height(16.dp))

        // Chat history
        ChatHistory()

        Spacer(modifier = Modifier.weight(1f))

        // Message input
        MessageInputField()
    }
}

@Composable
fun TopBar() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        IconButton(onClick = { /* Handle back press */ }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Text(
            text = "Chat",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@Composable
fun ChatHistory() {
    Column {
        ChatMessage(
            message = "How are you doing?",
            sender = "Elon Musk"
        )
        Spacer(modifier = Modifier.height(8.dp))
        ChatMessage(
            message = "I'm ready to start the quiz.",
            sender = "Jeff Bezos"
        )
    }
}

@Composable
fun ChatMessage(message: String, sender: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(8.dp))
        BasicText(
            text = message,
            style = LocalTextStyle.current.copy(color = Color.White, fontSize = 16.sp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = sender,
            color = Color.Gray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MessageInputField() {
    var text by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .background(Color(0xFF1F1F1F), CircleShape)
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        UserAvatar(imageUrl = "https://path/to/avatar.jpg")

        Spacer(modifier = Modifier.width(8.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            modifier = Modifier.weight(1f),
            cursorBrush = SolidColor(Color.White),
            decorationBox = { innerTextField ->
                if (text.isEmpty()) {
                    Text(
                        text = "Message Jeff Bezos",
                        color = Color.Gray
                    )
                }
                innerTextField()
            }
        )

        IconButton(onClick = { /* Handle send message */ }) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
        }

        IconButton(onClick = { /* Handle add attachment */ }) {
            Icon(Icons.Filled.Add, contentDescription = "Add", tint = Color.White)
        }
    }
}

@Composable
fun UserAvatar(imageUrl: String) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = Color.LightGray
    ) {
        val painter = rememberAsyncImagePainter(imageUrl)
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen()
}
