package com.jakubn.codequizapp.ui.settings.editProfile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.jakubn.codequizapp.R
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.theme.Typography
import java.util.Locale

@Composable
fun UserProfileEditScreen(
    navController: NavController,
    viewModel: UserProfileEditViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val userState by viewModel.userState.collectAsState()
    val newName by viewModel.newName.collectAsState()
    val newDescription by viewModel.newDescription.collectAsState()
    val newAvatarUri by viewModel.newAvatarUri.collectAsState()
    val updateOperationState by viewModel.updateOperationState.collectAsState()
    val isUpdateProfileButtonEnabled by viewModel.isUpdateProfileButtonEnabled.collectAsState()

    val isLoading = userState is CustomState.Loading || updateOperationState is CustomState.Loading

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(updateOperationState) {
        when (val currentUpdateState = updateOperationState) {
            is CustomState.Success -> {
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
            is CustomState.Failure -> {
                val message = currentUpdateState.message ?: "Unknown error"
                Toast.makeText(context, "Failed to update profile: $message", Toast.LENGTH_LONG).show()
            }
            else -> { }
        }
    }

    LaunchedEffect(userState) {
        val currentUserState = userState
        if (currentUserState is CustomState.Failure) {
            val message = currentUserState.message ?: "Unknown error loading profile"
            Toast.makeText(context, "Error loading profile: $message", Toast.LENGTH_LONG).show()
        }
    }

    val gradientColors = arrayOf(
        0.06f to MaterialTheme.colorScheme.primary,
        0.22f to Color(0xff74B583),
        0.39f to Color(0xff58959A),
        0.62f to Color(0xff003963),
        0.95f to Color(0xff000226)
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onAvatarUriChange(uri)
    }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colorStops = gradientColors))
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "<CODE/QUIZ>",
            style = Typography.bodyMedium,
            color = Color(0xff7BAFC4),
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomEnd
        ) {
            Image(
                painter = if (newAvatarUri != null) {
                    rememberAsyncImagePainter(newAvatarUri)
                } else {
                    painterResource(R.drawable.generic_avatar)
                },
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
            )
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.Black,
                modifier = Modifier
                    .offset(x = (-8).dp, y = (-8).dp)
                    .background(Color(0xffA3FF0D), CircleShape)
                    .clip(CircleShape)
                    .clickable(enabled = !isLoading && userState is CustomState.Success) {
                        imagePickerLauncher.launch("image/*")
                    }
                    .padding(8.dp)
                    .size(24.dp)
            )
        }

        when (val currentUserStateForName = userState) {
            is CustomState.Success -> {
                currentUserStateForName.result.name?.let {
                    Text(
                        text = it.lowercase(Locale.ROOT),
                        style = Typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } ?: Text(
                    text = "Unnamed User",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            CustomState.Loading -> {
                Text(
                    text = "Loading...",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            is CustomState.Failure -> {
                Text(
                    text = "Error loading user data",
                    style = Typography.titleMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            CustomState.Idle -> { }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape = RoundedCornerShape(20.dp))
                .background(Color(0x52D9D9D9))
                .padding(vertical = 10.dp, horizontal = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            when (val currentUserStateForStats = userState) {
                is CustomState.Success -> {
                    val currentUser = currentUserStateForStats.result
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${currentUser.wins}", style = Typography.titleMedium, color = Color.White)
                        Text(text = "wins", style = Typography.bodySmall, color = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "${currentUser.score}", style = Typography.titleMedium, color = Color(0xffA3FF0D))
                        Text(text = "score", style = Typography.bodySmall, color = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = (currentUser.gamesPlayed - currentUser.wins).toString(), style = Typography.titleMedium, color = Color.White)
                        Text(text = "losses", style = Typography.bodySmall, color = Color.White)
                    }
                }
                CustomState.Loading -> {
                    repeat(3) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "-", style = Typography.titleMedium, color = Color.Gray)
                            Text(text = "...", style = Typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
                is CustomState.Failure -> {
                    repeat(3) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "N/A", style = Typography.titleMedium, color = Color.Gray)
                            Text(text = "error", style = Typography.bodySmall, color = Color.Gray)
                        }
                    }
                }
                CustomState.Idle -> { }
            }
        }

        EditableField(
            label = "NAME",
            value = newName,
            onValueChange = viewModel::onNameChange,
            modifier = Modifier.padding(vertical = 16.dp),
            enabled = userState is CustomState.Success && !isLoading
        )

        EditableField(
            label = "DESCRIPTION",
            value = newDescription,
            onValueChange = viewModel::onDescriptionChange,
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.padding(bottom = 32.dp),
            enabled = userState is CustomState.Success && !isLoading
        )

        Button(
            onClick = viewModel::updateProfile,
            enabled = isUpdateProfileButtonEnabled,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                when (updateOperationState) {
                    CustomState.Loading -> "UPDATING..."
                    else -> "UPDATE PROFILE".uppercase(Locale.ROOT)
                },
                color = Color.White,
                style = Typography.bodyLarge
            )
        }
    }
}

@Composable
fun EditableField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    maxLines: Int = 1,
    enabled: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(Locale.ROOT),
            style = Typography.labelSmall,
            color = Color(0xffA3FF0D)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = minLines == 1 && maxLines == 1,
            minLines = minLines,
            maxLines = maxLines,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            textStyle = Typography.bodyMedium.copy(color = Color.White),
            colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xffA3FF0D),
                unfocusedBorderColor = Color.White.copy(alpha = 0.6f),
                cursorColor = Color.White,
                focusedLabelColor = Color(0xffA3FF0D),
                unfocusedLabelColor = Color.White.copy(alpha = 0.8f),
                disabledBorderColor = Color.Gray.copy(alpha = 0.5f),
                disabledTextColor = Color.Gray,
                disabledLabelColor = Color.Gray.copy(alpha = 0.8f)
            )
        )
    }
}