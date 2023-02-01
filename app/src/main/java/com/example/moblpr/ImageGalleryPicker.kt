package com.example.moblpr

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ImageGalleryPicker(registry: ActivityResultRegistry) {
    private val mutableSelectedItem = MutableLiveData<Uri>()

    private var imageUri: Uri? = null

    val selectedItem: LiveData<Uri> get() = mutableSelectedItem

    private val getContent = registry.register("OPEN GALLERY", ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            imageUri = data!!.data
            imageUri.let {
                mutableSelectedItem.value = it
            }
        }
    }

    fun select() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*";
        getContent.launch(intent)
    }
}