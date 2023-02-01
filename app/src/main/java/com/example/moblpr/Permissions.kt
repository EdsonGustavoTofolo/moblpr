package com.example.moblpr

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class Permissions {

    companion object {

        const val CAMERA_REQUEST_CODE = 100
        const val STORAGE_REQUEST_CODE = 101

        private var cameraPermissions: Array<String> = arrayOf(android.Manifest.permission.CAMERA, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        private var storagePermissions: Array<String> = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        fun checkStoragePermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        fun checkCameraPermission(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        }

        fun requestStoragePermission(activity: Activity) {
            ActivityCompat.requestPermissions(activity, storagePermissions, STORAGE_REQUEST_CODE)
        }

        fun requestCameraPermissions(activity: Activity) {
            ActivityCompat.requestPermissions(activity, cameraPermissions, CAMERA_REQUEST_CODE)
        }
    }

}