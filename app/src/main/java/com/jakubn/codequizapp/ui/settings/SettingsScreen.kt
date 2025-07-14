package com.jakubn.codequizapp.ui.settings

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.User
import com.jakubn.codequizapp.navigation.Screen
import com.jakubn.codequizapp.theme.Typography
import com.jakubn.codequizapp.ui.uiComponents.ClickableCustomText
import com.jakubn.codequizapp.ui.uiComponents.helperDialogs.ConfirmationDialog
import com.jakubn.codequizapp.ui.uiComponents.helperDialogs.InfoDialog
import com.jakubn.codequizapp.ui.uiComponents.helperDialogs.SelectionDialog


@Composable
fun SettingsScreen(
    user: User,
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel(),
    onLogoutConfirmed: () -> Unit
) {

    val colors = arrayOf(
        0.06f to MaterialTheme.colorScheme.primary,
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
                onClick = { navController.navigate(Screen.UserProfileEdit.route) },
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
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape),
                        painter = if (user.imageUri != null) {
                            rememberAsyncImagePainter(user.imageUri)
                        } else {
                            painterResource(R.drawable.generic_avatar)
                        },
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

            var showLanguageDialog by remember { mutableStateOf(false) }
            var showAboutDialog by remember { mutableStateOf(false) }
            var showHelpDialog by remember { mutableStateOf(false) }
            var showDeleteDialog by remember { mutableStateOf(false) }
            var showSignOutDialog by remember { mutableStateOf(false) }
            var selectedLanguage by remember { mutableStateOf("English") }

            val context = LocalContext.current

            val textLabelsWithActions = mapOf(
                "Language" to { showLanguageDialog = true },
                "About" to { showAboutDialog = true },
                "Help" to { showHelpDialog = true },
                "Delete account" to { showDeleteDialog = true },
                "Sign out" to { showSignOutDialog = true }
            )

            textLabelsWithActions.forEach { (label, action) ->
                ClickableCustomText(label) {
                    action()
                }
            }

            if (showLanguageDialog) {
                SelectionDialog(
                    text = "Choose language",
                    options = listOf("English","Polish"),
                    currentOption = selectedLanguage,
                    onDismiss = { showLanguageDialog = false },
                    onConfirm = {
                        selectedLanguage = it
                        Toast.makeText(context, "Selected: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (showAboutDialog) {
                InfoDialog(
                    title = "About",
                    message = "CodeQuiz v1.0\nCreated by Jakub N.",
                    onDismiss = { showAboutDialog = false }
                )
            }

            if (showHelpDialog) {
                InfoDialog(
                    title = "Help",
                    message = "Need help?\nContact support@codequiz.app",
                    onDismiss = { showHelpDialog = false }
                )
            }

            if (showDeleteDialog) {
                ConfirmationDialog(
                    title = "Delete Account",
                    message = "Are you sure you want to permanently delete your account?",
                    confirmText = "Delete",
                    onConfirm = {
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show()
                        showDeleteDialog = false
                    },
                    onDismiss = { showDeleteDialog = false }
                )
            }

            if (showSignOutDialog) {
                ConfirmationDialog(
                    title = "Sign Out",
                    message = "Do you really want to sign out?",
                    confirmText = "Sign Out",
                    onConfirm = {
                        viewModel.signOut()
                        onLogoutConfirmed()
                        Toast.makeText(context, "Signed out", Toast.LENGTH_SHORT).show()
                        showSignOutDialog = false
                    },
                    onDismiss = { showSignOutDialog = false }
                )
            }
        }
    }
}