package com.example.dog_identifier.ui.dogfinder

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DogFinderViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "dogfind page"
    }
    val text: LiveData<String> = _text
}