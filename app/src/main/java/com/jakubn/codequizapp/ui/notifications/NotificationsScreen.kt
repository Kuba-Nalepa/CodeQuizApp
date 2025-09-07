package com.jakubn.codequizapp.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.theme.Typography

@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel = hiltViewModel(),
    userId: String
) {
    LaunchedEffect(userId) {
        notificationsViewModel.fetchNotifications(userId)
    }

    val notificationsState by notificationsViewModel.notificationsState.collectAsState()

    when (notificationsState) {
        is CustomState.Success -> {
            // Display the list of notifications
        }

        is CustomState.Failure -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Failed to fetch notifications",
                    textAlign = TextAlign.Center,
                    style = Typography.bodyLarge
                )
            }
        }

        is CustomState.Loading -> {
            CircularProgressIndicator()
        }
        
        CustomState.Idle -> {
            // Nothing
        }
    }
}