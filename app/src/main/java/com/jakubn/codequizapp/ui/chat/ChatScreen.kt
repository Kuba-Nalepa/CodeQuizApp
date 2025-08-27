package com.jakubn.codequizapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.Message
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.theme.Typography


@Composable
fun ChatScreen(
    user: User,
    friend: Friend,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val messagesState by viewModel.messages.collectAsState()
    val listState = rememberLazyListState()

    LaunchedEffect(key1 = friend.uid) {
        user.uid?.let {
            user.uid.let { viewModel.startChat(it, friend) }
        }
    }

    LaunchedEffect(messagesState) {
        if (messagesState is CustomState.Success) {
            listState.animateScrollToItem(0)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painterResource(R.drawable.background_auth),
                contentScale = ContentScale.FillBounds,
            )
            .background(Color(0x66000000))
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        when (val state = messagesState) {
            is CustomState.Success -> {
                user.uid?.let {
                    Box(modifier = Modifier.weight(1f)) {
                        ChatHistory(
                            messageList = state.result,
                            myUid = user.uid,
                            listState = listState
                        )
                    }
                }
            }

            is CustomState.Failure -> {
                Text(text = "Error: ${state.message}", color = Color.Red, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            is CustomState.Loading -> {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))

                }
            }

            CustomState.Idle -> {
                // Nothing
            }
        }

        MessageInputField(
            friend = friend,
            onSendMessage = { messageText ->
                user.uid?.let { viewModel.sendMessage(it, messageText) }
            }
        )
    }
}

@Composable
fun ChatHistory(messageList: List<Message>, myUid: String, listState: LazyListState) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
        reverseLayout = true,
        state = listState
    ) {
        items(messageList) { message ->
            ChatMessage(
                message = message,
                myUid = myUid
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ChatMessage(message: Message, myUid: String) {
    val isMe = message.senderId == myUid

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {

        Text(
            text = message.text,
            style = Typography.bodySmall,
            color = Color.White,
            modifier = Modifier.background(
                color = if (isMe) Color(0xFF003963) else Color(0xFFB7B7B7),
                shape = MaterialTheme.shapes.medium
            ).padding(8.dp)
        )
    }
}

@Composable
fun MessageInputField(
    friend: Friend,
    onSendMessage: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF003963), shape = RoundedCornerShape(20.dp))
    ) {
        OutlinedTextField(
            modifier = Modifier.weight(1f),
            value = text,
            onValueChange = { text = it },
            placeholder = { Text(text = "Message ${friend.name}", color = Color(0xFFB7B7B7), style = Typography.bodyMedium) },
            maxLines = 3,
            shape = RoundedCornerShape(20.dp),
            textStyle = Typography.bodyMedium
        )

        IconButton(onClick = {
            if (text.isNotBlank()) {
                onSendMessage(text)
                text = ""
            }
        }) {
            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
        }
    }
}