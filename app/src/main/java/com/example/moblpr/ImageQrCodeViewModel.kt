package com.example.moblpr

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ImageQrCodeViewModel : ViewModel() {
    private val mutableSelectedItem = MutableLiveData<Bitmap>()

    val selectedItem: LiveData<Bitmap> get() = mutableSelectedItem

    fun selectItem(image: Bitmap) {
        mutableSelectedItem.value = image
    }
}