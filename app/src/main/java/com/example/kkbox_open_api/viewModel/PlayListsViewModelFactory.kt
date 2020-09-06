package com.example.kkbox_open_api.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kkbox_open_api.model.IPlayListsRepository

class PlayListsViewModelFactory(private val playListsRepository: IPlayListsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayListsViewModel::class.java)) {
            return PlayListsViewModel(playListsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}