package com.example.asknitt.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.asknitt.data.repository.*

class MainViewModelFactory(
    private val doubtRepository: DoubtRepository,
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val answerRepository: AnswerRepository,
    private val aiChatRepository: AiChatRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> 
                MainViewModel(doubtRepository, userRepository, socialRepository, answerRepository) as T
            modelClass.isAssignableFrom(DoubtsViewModel::class.java) -> 
                DoubtsViewModel(doubtRepository, userRepository) as T
            modelClass.isAssignableFrom(AnswerViewModel::class.java) -> 
                AnswerViewModel(answerRepository) as T
            modelClass.isAssignableFrom(ExploreViewModel::class.java) -> 
                ExploreViewModel(socialRepository) as T
            modelClass.isAssignableFrom(AiViewModel::class.java) -> 
                AiViewModel(aiChatRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
