package com.jakubn.codequizapp.ui.settings.editProfile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.compose.material3.CircularProgressIndicator
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
import com.jakubn.codequizapp.model.User
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
    val selectedLocalAvatarUri by viewModel.selectedLocalAvatarUri.collectAsState()
    val imageUploadState by viewModel.imageUploadState.collectAsState()
    val updateOperationState by viewModel.updateOperationState.collectAsState()
    val isUpdateProfileButtonEnabled by viewModel.isUpdateProfileButtonEnabled.collectAsState()
    val isLoadingUser = userState is CustomState.Loading
    val isUploadingImage = imageUploadState is CustomState.Loading
    val isUpdatingProfile = updateOperationState is CustomState.Loading
    val isAnyOperationInProgress = isLoadingUser || isUploadingImage || isUpdatingProfile

    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(updateOperationState) {
        if (updateOperationState is CustomState.Success) {
            navController.popBackStack()
        }
    }

    val gradientColors = arrayOf(
        0.06f to Color(0xffA3FF0D),
        0.22f to Color(0xff74B583),
        0.39f to Color(0xff58959A),
        0.62f to Color(0xff003963),
        0.95f to Color(0xff000226)
    )

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        viewModel.onNewAvatarSelected(uri)
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

        ProfilePictureSection(
            userState = userState,
            selectedLocalAvatarUri = selectedLocalAvatarUri,
            isUploadingImage = isUploadingImage,
            isAnyOperationInProgress = isAnyOperationInProgress,
            onEditClick = {
                imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        UserNameDisplay(userState = userState)

        StatsSection(userState = userState)

        Spacer(modifier = Modifier.height(24.dp))

        EditableField(
            label = "NAME",
            value = newName,
            onValueChange = viewModel::onNameChange,
            modifier = Modifier.padding(bottom = 16.dp),
            enabled = userState is CustomState.Success && !isAnyOperationInProgress
        )

        EditableField(
            label = "DESCRIPTION",
            value = newDescription,
            onValueChange = viewModel::onDescriptionChange,
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.padding(bottom = 32.dp),
            enabled = userState is CustomState.Success && !isAnyOperationInProgress
        )

        Button(
            onClick = viewModel::updateProfile,
            enabled = isUpdateProfileButtonEnabled && !isAnyOperationInProgress,
            shape = RoundedCornerShape(20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text(
                when {
                    isUpdatingProfile -> "UPDATING PROFILE..."
                    isUploadingImage -> "UPLOADING IMAGE..."
                    else -> "UPDATE PROFILE".uppercase(Locale.ROOT)
                },
                style = Typography.bodyLarge
            )
        }
    }
}

@Composable
private fun ProfilePictureSection(
    userState: CustomState<User>,
    selectedLocalAvatarUri: Uri?,
    isUploadingImage: Boolean,
    isAnyOperationInProgress: Boolean,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier.size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = when {
                selectedLocalAvatarUri != null -> rememberAsyncImagePainter(selectedLocalAvatarUri)
                userState is CustomState.Success && !(userState.result.imageUri.isNullOrEmpty()) ->
                    rememberAsyncImagePainter(Uri.parse(userState.result.imageUri))
                else -> painterResource(R.drawable.sample_avatar)
            },
            contentDescription = "Profile Picture",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )

        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit Profile Picture",
            tint = Color.Black,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 8.dp, y = 8.dp)
                .background(Color(0xffA3FF0D), CircleShape)
                .clip(CircleShape)
                .clickable(enabled = !isAnyOperationInProgress, onClick = onEditClick)
                .padding(8.dp)
                .size(24.dp)
        )

        if (isUploadingImage) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center).size(60.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun UserNameDisplay(userState: CustomState<User>) {
    when (userState) {
        is CustomState.Success -> {
            userState.result.name?.let {
                Text(
                    text = it.lowercase(Locale.ROOT),
                    style = Typography.titleLarge,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            } ?: Text(
                text = "Unnamed User",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        CustomState.Loading -> {
            Text(
                text = "Loading...",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        is CustomState.Failure -> {
            Text(
                text = "Error loading user data",
                style = Typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        CustomState.Idle -> { }
    }
}

@Composable
private fun StatsSection(userState: CustomState<User>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(20.dp))
            .background(Color(0x52D9D9D9))
            .padding(vertical = 12.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (val currentUserStateForStats = userState) {
            is CustomState.Success -> {
                val currentUser = currentUserStateForStats.result
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(text = "${currentUser.wins}", style = Typography.titleMedium, color = Color.White)
                    Text(text = "wins", style = Typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(text = "${currentUser.score}", style = Typography.titleMedium, color = Color(0xffA3FF0D))
                    Text(text = "score", style = Typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text(text = (currentUser.gamesPlayed - currentUser.wins).toString(), style = Typography.titleMedium, color = Color.White)
                    Text(text = "losses", style = Typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                }
            }
            CustomState.Loading -> {
                repeat(3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "...", style = Typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            is CustomState.Failure -> {
                repeat(3) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(text = "N/A", style = Typography.titleMedium, color = Color.Gray)
                        Text(text = "error", style = Typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            CustomState.Idle -> { }
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
                disabledLabelColor = Color.Gray.copy(alpha = 0.8f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent
            )
        )
    }
}