package com.example.kkbox_open_api.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

const val HIGH_RESOLUTION = "1000x1000"
const val LOW_RESOLUTION = "300x300"

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