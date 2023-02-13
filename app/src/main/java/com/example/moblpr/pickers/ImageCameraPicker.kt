package com.example.moblpr.pickers

import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ImageCameraPicker(registry: ActivityResultRegistry) {

    private val mutableSelectedItem = MutableLiveData<Uri>()

    private var imageUri: Uri? = null

    val selectedItem: LiveData<Uri> get() = mutableSelectedItem

    private val getContent = registry.register("OPEN CAMERA", ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            imageUri.let {
                mutableSelectedItem.value = it
            }
        }
    }

    fun select(activity: Activity) {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Image Plate Car")
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image Plate Car To Scan")

        imageUri = activity.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        getContent.launch(imageUri)
    }

}