package com.example.asknitt.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.asknitt.data.MULTIPARTBODY_FILE_KEY
import com.example.asknitt.data.model.Answer
import com.example.asknitt.data.model.PostAnswerToDoubtItem
import com.example.asknitt.data.model.UploadFileItem
import com.example.asknitt.data.remote.api
import com.example.asknitt.data.repository.AnswerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class AnswerViewModel(
    private val answerRepository: AnswerRepository
) : ViewModel() {

    private val _currentQuestionId = MutableStateFlow<Int?>(null)
    
    val curQuestionAnswers: StateFlow<List<Answer>> = _currentQuestionId
        .flatMapLatest { id ->
            if (id != null) answerRepository.getAnswersForQuestion(id)
            else MutableStateFlow(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val answerFiles: MutableList<UploadFileItem> = mutableStateListOf()

    fun getAnswersByQuestionId(id: Int, onFinish: (Boolean, String) -> Unit) {
        _currentQuestionId.value = id
        viewModelScope.launch {
            val result = answerRepository.refreshAnswers(id)
            onFinish(result.isSuccess || curQuestionAnswers.value.isNotEmpty(), "")
        }
    }

    fun vote(answerId: Int, shouldDoUpvote: Boolean, isUpVoted: Boolean, isDownVoted: Boolean, changeUpVote: (Int) -> Unit, changeDownVote: (Int) -> Unit, onFinish: (Boolean, String) -> Unit) {
        var addToUpvote = 0; var addToDownvote = 0
        if (isUpVoted) { if (!shouldDoUpvote) { addToDownvote = 1; addToUpvote = -1 } }
        else if (isDownVoted) { if (shouldDoUpvote) { addToDownvote = -1; addToUpvote = 1 } }
        else { if (shouldDoUpvote) addToUpvote = 1 else addToDownvote = 1 }
        changeUpVote(addToUpvote); changeDownVote(addToDownvote)
        viewModelScope.launch {
            try {
                val response = api.VoteAnswer(com.example.asknitt.data.model.Vote(answerId, addToUpvote, addToDownvote))
                onFinish(response.isSuccessful, "")
            } catch (e: Exception) { onFinish(false, e.message ?: "Error") }
        }
    }

    fun postAnswer(questionId: Int, answer: String, username: String, onFinish: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = api.PostAnswer(PostAnswerToDoubtItem(questionId, answer, username))
                if (response.isSuccessful && response.body()?.error_msg.isNullOrEmpty()) {
                    if (answerFiles.isNotEmpty()) {
                        uploadFilesForAnswerInternal()
                    }
                    answerRepository.refreshAnswers(questionId)
                    onFinish(true, "")
                } else onFinish(false, response.body()?.error_msg ?: "Error")
            } catch (e: Exception) { onFinish(false, e.message ?: "Error") }
        }
    }

    private suspend fun uploadFilesForAnswerInternal(): Boolean {
        val fileParts = answerFiles.map { 
            MultipartBody.Part.createFormData(MULTIPARTBODY_FILE_KEY, it.filename, it.multipartBody.body) 
        }
        return try {
            val response = api.UploadFilesForAnswer(fileParts)
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    fun clearAnswerFiles() { answerFiles.clear() }
}
