package com.example.moblpr

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageUriViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<Uri>()

    val selectedItem: LiveData<Uri> get() = mutableSelectedItem

    fun selectItem(uri: Uri) {
        mutableSelectedItem.value = uri
    }
}