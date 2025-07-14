package com.jakubn.codequizapp.ui.settings.editProfile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileEditViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val maxLengthName = 13
    private val maxDescriptionLength = 200

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    private val _userState = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val userState: StateFlow<CustomState<User>> = _userState.asStateFlow()

    private val _newName = MutableStateFlow("")
    val newName: StateFlow<String> = _newName.asStateFlow()

    private val _newDescription = MutableStateFlow("")
    val newDescription: StateFlow<String> = _newDescription.asStateFlow()

    private val _selectedLocalAvatarUri = MutableStateFlow<Uri?>(null)
    val selectedLocalAvatarUri: StateFlow<Uri?> = _selectedLocalAvatarUri.asStateFlow()

    private val _uploadedAvatarUrl = MutableStateFlow<String?>(null)

    private val _imageUploadState = MutableStateFlow<CustomState<String>>(CustomState.Idle)
    val imageUploadState: StateFlow<CustomState<String>> = _imageUploadState.asStateFlow()

    private val _updateOperationState = MutableStateFlow<CustomState<Unit>>(CustomState.Idle)
    val updateOperationState: StateFlow<CustomState<Unit>> = _updateOperationState.asStateFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userDataRepository.getUserData()
                .onStart { _userState.value = CustomState.Loading }
                .catch { e ->
                    _userState.value = CustomState.Failure(e.message ?: "Failed to load user data")
                    _toastEvent.emit("Error loading user data: ${e.message}")
                }
                .collect { user ->
                    _userState.value = CustomState.Success(user)
                    _newName.value = user.name ?: ""
                    _newDescription.value = user.description ?: ""
                    _uploadedAvatarUrl.value = user.imageUri
                    _selectedLocalAvatarUri.value = user.imageUri?.let { Uri.parse(it) }
                }
        }
    }

    fun onNameChange(name: String) {
        if (name.length <= maxLengthName) {
            _newName.value = name
        } else {
            if (_newName.value.length < name.length) {
                _toastEvent.tryEmit("Name cannot exceed $maxLengthName characters")
            }
            _newName.value = name.take(maxLengthName)
        }
    }

    fun onDescriptionChange(description: String) {
        if (description.length <= maxDescriptionLength) {
            _newDescription.value = description
        } else {
            if (_newDescription.value.length < description.length) {
                _toastEvent.tryEmit("Description cannot exceed $maxDescriptionLength characters")
            }
            _newDescription.value = description.take(maxDescriptionLength)
        }
    }

    fun onNewAvatarSelected(uri: Uri?) {
        _selectedLocalAvatarUri.value = uri
        if (uri != null) {
            uploadAvatarImage(uri)
        } else {
            _uploadedAvatarUrl.value = null
            _imageUploadState.value = CustomState.Idle
            viewModelScope.launch {
                _toastEvent.emit("Avatar selection cleared.")
            }
        }
    }

    private fun uploadAvatarImage(imageUri: Uri) {
        viewModelScope.launch {
            _imageUploadState.value = CustomState.Loading
            try {
                val downloadUrl = userDataRepository.uploadProfileImage(imageUri)
                _uploadedAvatarUrl.value = downloadUrl
                _imageUploadState.value = CustomState.Success(downloadUrl)
                _toastEvent.emit("Profile image uploaded successfully!")
            } catch (e: Exception) {
                _imageUploadState.value = CustomState.Failure(
                    e.message ?: "Failed to upload profile image."
                )
                _toastEvent.emit("Error uploading image: ${e.message}")
            }
        }
    }

    val isUpdateProfileButtonEnabled: StateFlow<Boolean> = combine(
        _userState,
        _newName,
        _newDescription,
        _uploadedAvatarUrl,
        _updateOperationState,
        _imageUploadState
    ) { args ->
        val userState = args[0] as CustomState<User>
        val newName = args[1] as String
        val newDescription = args[2] as String
        val uploadedAvatarUrl = args[3] as String?
        val updateState = args[4] as CustomState<Unit>
        val imageUploadState = args[5] as CustomState<String>

        val currentLoadedUser = (userState as? CustomState.Success)?.result

        val isNameValid = newName.length in 1..maxLengthName
        val isDescriptionValid = newDescription.length <= maxDescriptionLength

        val isImageUploadReady = imageUploadState !is CustomState.Loading && imageUploadState !is CustomState.Failure

        val hasChanges = currentLoadedUser?.let { user ->
            newName != (user.name ?: "") ||
                    newDescription != (user.description ?: "") ||
                    uploadedAvatarUrl != (user.imageUri ?: "")
        } ?: false

        userState is CustomState.Success &&
                updateState !is CustomState.Loading &&
                isNameValid &&
                isDescriptionValid &&
                hasChanges &&
                isImageUploadReady
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun updateProfile() {
        viewModelScope.launch {
            if (_userState.value !is CustomState.Success) {
                _updateOperationState.value = CustomState.Failure("User data not loaded. Cannot update profile.")
                _toastEvent.emit("Error: User data not loaded for update.")
                return@launch
            }

            if (_imageUploadState.value is CustomState.Loading) {
                _toastEvent.emit("Please wait for the profile image to finish uploading.")
                return@launch
            }
            if (_imageUploadState.value is CustomState.Failure) {
                _toastEvent.emit("Profile image upload failed. Please try again or select a different image.")
                return@launch
            }

            val currentUser = (_userState.value as CustomState.Success).result

            val updatedUser = currentUser.copy(
                name = _newName.value,
                description = _newDescription.value,
                imageUri = _uploadedAvatarUrl.value
            )

            userDataRepository.updateUserProfile(updatedUser)
                .onStart {
                    _updateOperationState.value = CustomState.Loading
                }
                .catch { e ->
                    _updateOperationState.value = CustomState.Failure(
                        e.message ?: "An unknown error occurred during profile update."
                    )
                    _toastEvent.emit("Error updating profile: ${e.message}")
                }
                .collect {
                    _updateOperationState.value = CustomState.Success(Unit)
                    _toastEvent.emit("Profile updated successfully!")
                    loadCurrentUser()
                }
        }
    }
}