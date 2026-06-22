package com.example.asknitt.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asknitt.data.local.Entities.AiChatEntity
import com.example.asknitt.data.repository.AiChatRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AiViewModel(
    private val aiChatRepository: AiChatRepository
) : ViewModel() {

    val chatHistory: StateFlow<List<AiChatEntity>> = aiChatRepository.chatHistory.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    var isTyping by mutableStateOf(false)
        private set

    fun askAI(prompt: String, onFinish: (Boolean, String) -> Unit) {
        isTyping = true
        viewModelScope.launch {
            val result = aiChatRepository.askAI(prompt)
            isTyping = false
            if (result.isSuccess) {
                onFinish(true, result.getOrNull() ?: "")
            } else {
                onFinish(false, result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun clearHistory(onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val result = aiChatRepository.clearHistory()
            onFinish(result.isSuccess, result.getOrNull() ?: result.exceptionOrNull()?.message ?: "")
        }
    }
}
