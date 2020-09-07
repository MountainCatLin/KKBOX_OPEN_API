package com.example.kkbox_open_api.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kkbox_open_api.model.IPlayListRepository

class PlayListViewModelFactory(private val playListRepository: IPlayListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayListViewModel::class.java)) {
            return PlayListViewModel(playListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}