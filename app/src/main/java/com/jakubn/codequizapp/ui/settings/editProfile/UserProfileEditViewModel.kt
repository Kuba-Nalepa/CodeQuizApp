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

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            userDataRepository.getUserData()
                .onStart { _userState.value = CustomState.Loading }
                .catch { e -> _userState.value = CustomState.Failure(e.message) }
                .collect { user ->
                    _userState.value = CustomState.Success(user)
                    _newName.value = user.name ?: ""
                    _newDescription.value = user.description ?: ""
                    _newAvatarUri.value = user.imageUri?.let { Uri.parse(it) }
                }
        }
    }

    fun onNameChange(name: String) {
        val currentNameLength = _newName.value.length
        if (name.length <= maxLengthName) {
            _newName.value = name
        } else {
            if (currentNameLength == maxLengthName) {
                viewModelScope.launch {
                    _toastEvent.emit("Name cannot exceed $maxLengthName characters")
                }
            }
            _newName.value = name.take(maxLengthName)
        }
    }

    fun onDescriptionChange(description: String) {
        val currentDescriptionLength = _newDescription.value.length
        if (description.length <= maxDescriptionLength) {
            _newDescription.value = description
        } else {
            if (currentDescriptionLength == maxDescriptionLength) {
                viewModelScope.launch {
                    _toastEvent.emit("Description cannot exceed $maxDescriptionLength characters")
                }
            }
            _newDescription.value = description.take(maxDescriptionLength)
        }
    }

    fun onAvatarUriChange(uri: Uri?) {
        _newAvatarUri.value = uri
    }

    val isUpdateProfileButtonEnabled: StateFlow<Boolean> = combine(
        _userState,
        _newName,
        _newDescription,
        _newAvatarUri,
        _updateOperationState
    ) { userState, newName, newDescription, newAvatarUri, updateState ->
        val currentLoadedUser = (userState as? CustomState.Success)?.result

        val isNameValid = newName.length in 1..maxLengthName
        val isDescriptionValid = newDescription.length <= maxDescriptionLength

        val hasChanges = currentLoadedUser?.let { user ->
            newName != (user.name ?: "") ||
                    newDescription != (user.description ?: "") ||
                    newAvatarUri?.toString() != (user.imageUri ?: "")
        } ?: false

        userState is CustomState.Success &&
                updateState !is CustomState.Loading &&
                isNameValid &&
                isDescriptionValid &&
                hasChanges
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    fun updateProfile() {
        viewModelScope.launch {
            if (_userState.value !is CustomState.Success) {
                _updateOperationState.value =
                    CustomState.Failure("User data not loaded. Cannot update profile.")
                return@launch
            }

            val currentUser = (_userState.value as CustomState.Success).result

            val updatedUser = currentUser.copy(
                name = _newName.value,
                description = _newDescription.value,
                imageUri = _newAvatarUri.value?.toString()
            )

            userDataRepository.updateUserProfile(updatedUser)
                .onStart {
                    _updateOperationState.value = CustomState.Loading

                }
                .catch { e ->
                    _updateOperationState.value = CustomState.Failure(
                        e.message ?: "An unknown error occurred during profile update."
                    )
                }
                .collect {
                    _updateOperationState.value = CustomState.Success(Unit)
                    loadCurrentUser()
                }
        }
    }
}