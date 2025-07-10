package com.jakubn.codequizapp.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.domain.model.CustomState
import com.jakubn.codequizapp.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileEditViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository
) : ViewModel() {

    private val _userState = MutableStateFlow<CustomState<User>>(CustomState.Idle)
    val userState: StateFlow<CustomState<User>> = _userState.asStateFlow()

    private val _newName = MutableStateFlow("")
    val newName: StateFlow<String> = _newName.asStateFlow()

    private val _newDescription = MutableStateFlow("")
    val newDescription: StateFlow<String> = _newDescription.asStateFlow()

    private val _newAvatarUri = MutableStateFlow<Uri?>(null)
    val newAvatarUri: StateFlow<Uri?> = _newAvatarUri.asStateFlow()

    private val _updateOperationState = MutableStateFlow<CustomState<Unit>>(CustomState.Idle)
    val updateOperationState: StateFlow<CustomState<Unit>> = _updateOperationState.asStateFlow()

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        viewModelScope.launch {
            _userState.value = CustomState.Loading
            try {
                userDataRepository.getUserData().collect { currentUser ->
                    _userState.value = CustomState.Success(currentUser)
                    _newName.value = currentUser.name.orEmpty()
                    _newDescription.value = currentUser.description.orEmpty()
                    _newAvatarUri.value = currentUser.imageUri?.let { Uri.parse(it) }
                }
            } catch (e: Exception) {
                _userState.value = CustomState.Failure(e.localizedMessage ?: "Unknown error fetching user data")
            }
        }
    }

    fun onNameChange(name: String) {
        _newName.value = name
    }

    fun onDescriptionChange(description: String) {
        _newDescription.value = description
    }

    fun onAvatarUriChange(uri: Uri?) {
        _newAvatarUri.value = uri
    }

    fun updateProfile() {
        viewModelScope.launch {
            _updateOperationState.value = CustomState.Loading
            try {
                val currentUser = (_userState.value as? CustomState.Success)?.result

                if (currentUser == null) {
                    _updateOperationState.value = CustomState.Failure("User not authenticated or data not loaded.")
                    return@launch
                }

                var finalImageUri: String? = currentUser.imageUri

                val selectedNewAvatarUri = _newAvatarUri.value
                if (selectedNewAvatarUri != null && selectedNewAvatarUri.toString() != currentUser.imageUri) {
                    finalImageUri = userDataRepository.uploadProfileImage(selectedNewAvatarUri)
                }

                val updatedUser = currentUser.copy(
                    name = _newName.value.ifEmpty { null },
                    description = _newDescription.value.ifEmpty { null },
                    imageUri = finalImageUri
                )
                userDataRepository.updateUserProfile(updatedUser)
                _updateOperationState.value = CustomState.Success(Unit)

            } catch (e: Exception) {
                _updateOperationState.value = CustomState.Failure("Failed to update profile: ${e.localizedMessage}")
            }
        }
    }
}