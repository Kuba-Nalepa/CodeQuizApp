package com.jakubn.codequizapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jakubn.codequizapp.data.repositoryImpl.UserDataRepository
import com.jakubn.codequizapp.model.CustomState
import com.jakubn.codequizapp.model.Friend
import com.jakubn.codequizapp.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val userRepository: UserDataRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<CustomState<List<Message>>>(CustomState.Idle)
    val messages: StateFlow<CustomState<List<Message>>> = _messages

    private var currentChatId: String? = null

    fun startChat(myUserId: String, friend: Friend) {
        viewModelScope.launch {
            _messages.value = CustomState.Loading
            try {
                val chatId = friend.uid?.let { userRepository.createChat(myUserId, it) }
                currentChatId = chatId

                if (chatId != null) {
                    userRepository.observeChatMessages(chatId).collectLatest { messagesList ->
                        _messages.value = CustomState.Success(messagesList)
                    }
                }
            } catch (e: Exception) {
                _messages.value = CustomState.Failure(e.message ?: "Failed to start chat")
            }
        }
    }

    fun sendMessage(myUserId: String, messageText: String) {
        viewModelScope.launch {
            try {
                val chatId = currentChatId ?: run {
                    throw IllegalStateException("Chat ID not initialized.")
                }

                userRepository.sendMessage(chatId, myUserId, messageText)
            } catch (e: Exception) {
                _messages.value = CustomState.Failure(e.message ?: "Failed to send message")
            }
        }
    }
}