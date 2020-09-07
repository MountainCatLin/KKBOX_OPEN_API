package com.example.kkbox_open_api.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.kkbox_open_api.AppInfo.HIGH_RESOLUTION
import com.example.kkbox_open_api.AppInfo.LOW_RESOLUTION

class PageViewModel : ViewModel() {
    private val resolutions = arrayOf(HIGH_RESOLUTION, LOW_RESOLUTION)
    private val _index = MutableLiveData<Int>()
    val text: LiveData<String> = Transformations.map(_index) {
        resolutions[it - 1]
    }

    fun setIndex(index: Int) {
        _index.value = index
    }
}