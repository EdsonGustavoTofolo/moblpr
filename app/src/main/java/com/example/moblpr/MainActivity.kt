package com.example.moblpr

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.example.moblpr.databinding.ActivityMainBinding
import com.example.moblpr.fragments.ImageFragment
import com.example.moblpr.fragments.QrCodeFragment
import com.example.moblpr.fragments.VehicleFragment
import com.example.moblpr.pickers.ImageCameraPicker
import com.example.moblpr.pickers.ImageGalleryPicker
import com.example.moblpr.viewmodels.ImageQrCodeViewModel
import com.example.moblpr.viewmodels.ImageUriViewModel
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var imageCameraPicker: ImageCameraPicker
    private lateinit var imageGalleryPicker: ImageGalleryPicker

    private val imageViewModel: ImageUriViewModel by viewModels()
    private val imageQrCodeViewModel: ImageQrCodeViewModel by viewModels()

    private lateinit var qrCodeImage: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        imageQrCodeViewModel.selectedItem.observe(this) { image ->
            qrCodeImage = image
        }

        imageCameraPicker = ImageCameraPicker(this.activityResultRegistry)
        imageCameraPicker.selectedItem.observe(this) { uri ->
            selectedImageUri(uri)
        }

        imageGalleryPicker = ImageGalleryPicker(this.activityResultRegistry)
        imageGalleryPicker.selectedItem.observe(this) { uri ->
            selectedImageUri(uri)
        }

        binding.menuItemCamera.setOnClickListener {
            pickImageCameraExecute()
        }

        binding.menuItemGallery.setOnClickListener {
            pickImageGalleryExecute()
        }

        binding.menuItemScan.setOnClickListener {
            scanImage()
        }

        binding.menuItemShareQrCode.setOnClickListener {
            shareQrCodeImage()
        }

        binding.menuItemGenerateQrCode.setOnClickListener {
            generateQrCode()
        }

        initStateButtons()
    }

    private fun initStateButtons() {
        binding.menuItemCamera.visibility = View.VISIBLE
        binding.menuItemGallery.visibility = View.VISIBLE
        binding.menuItemScan.visibility = View.GONE
        binding.menuItemGenerateQrCode.visibility = View.GONE
        binding.menuItemShareQrCode.visibility = View.GONE
    }

    fun visibilityButtonsImageFrag(visibilityScan: Int) {
        binding.menuItemCamera.visibility = View.VISIBLE
        binding.menuItemGallery.visibility = View.VISIBLE
        binding.menuItemScan.visibility = visibilityScan
        binding.menuItemGenerateQrCode.visibility = View.GONE
        binding.menuItemShareQrCode.visibility = View.GONE
    }

    fun visibilityButtonsVehicleFrag() {
        binding.menuItemCamera.visibility = View.GONE
        binding.menuItemGallery.visibility = View.GONE
        binding.menuItemScan.visibility = View.GONE
        binding.menuItemGenerateQrCode.visibility = View.VISIBLE
        binding.menuItemShareQrCode.visibility = View.GONE
    }

    fun visibilityButtonsQrCodeFrag() {
        binding.menuItemCamera.visibility = View.GONE
        binding.menuItemGallery.visibility = View.GONE
        binding.menuItemScan.visibility = View.GONE
        binding.menuItemGenerateQrCode.visibility = View.GONE
        binding.menuItemShareQrCode.visibility = View.VISIBLE
    }

    private fun scanImage() {
        progressBarShow()
        getActiveFragment().also { fragment ->
            if (fragment is ImageFragment) {
                fragment.scan()
            }
        }
    }

    private fun generateQrCode() {
        progressBarShow()
        getActiveFragment().also { fragment ->
            if (fragment is VehicleFragment) {
                fragment.generateQrCode()
            }
        }
    }

    private fun getActiveFragment(): Fragment? {
        val frag = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        return frag.childFragmentManager.fragments[0]
    }

    private fun shareQrCodeImage() {
        val os = ByteArrayOutputStream();

        qrCodeImage.compress(Bitmap.CompressFormat.PNG, 100, os)

        val path = MediaStore.Images.Media.insertImage(contentResolver, qrCodeImage, "QRCode", "QRCode with car info")

        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.type = "image/*"
        intentShare.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intentShare.putExtra(Intent.EXTRA_STREAM, Uri.parse(path))

        startActivity(intentShare)
    }

    fun showGenerateQrCodeOption() {
        binding.menuItemGenerateQrCode.visibility = View.VISIBLE
    }

    fun showShareQrCodeOption() {
        binding.menuItemShareQrCode.visibility = View.VISIBLE
    }

    fun progressBarShow() {
        binding.progressbar.visibility = ProgressBar.VISIBLE
    }

    fun progressBarHide() {
        binding.progressbar.visibility = ProgressBar.GONE
    }

    private fun selectedImageUri(uri: Uri) {
        imageViewModel.selectItem(uri)
        binding.menuItemScan.visibility = View.VISIBLE
    }

    private fun pickImageGalleryExecute() {
        if (Permissions.checkStoragePermission(this)) {
            imageGalleryPicker.select()
        } else {
            Permissions.requestStoragePermission(this)
        }
    }

    private fun pickImageCameraExecute() {
        if (Permissions.checkCameraPermission(this)) {
            imageCameraPicker.select(this)
        } else {
            Permissions.requestCameraPermissions(this)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode) {
            Permissions.CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    if (cameraAccepted && storageAccepted) {
                        pickImageCameraExecute()
                    } else {
                        Snackbar.make(binding.coordinatorLayout, "Permissões de Camera & Armazenamento são obrigatórios", Snackbar.LENGTH_LONG)
                            .setAction("Permitir") {
                                Permissions.requestCameraPermissions(this)
                            }.show()
                    }
                }
            }
            Permissions.STORAGE_REQUEST_CODE -> {
                if (grantResults.isNotEmpty()) {
                    val storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    if (storageAccepted) {
                        pickImageGalleryExecute()
                    } else {
                        Snackbar.make(binding.coordinatorLayout, "Permissão de Armazenamento é obrigatório", Snackbar.LENGTH_LONG)
                            .setAction("Permitir") {
                                Permissions.requestStoragePermission(this)
                            }.show()
                    }
                }
            }
        }
    }

    fun snackBarShow(text: String) {
        Snackbar.make(binding.coordinatorLayout, text, Snackbar.LENGTH_LONG).show()
    }

    fun snackBar(text: String) : Snackbar {
        return Snackbar.make(binding.coordinatorLayout, text, Snackbar.LENGTH_LONG)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}