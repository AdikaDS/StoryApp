package com.adika.storyapp.view.addstory

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.adika.storyapp.R
import com.adika.storyapp.databinding.ActivityAddStoryBinding
import com.adika.storyapp.utils.getImageUri
import com.adika.storyapp.utils.reduceFileImage
import com.adika.storyapp.utils.uriToFile
import com.adika.storyapp.view.StoryModelFactory
import com.adika.storyapp.view.addstory.CameraActivity.Companion.CAMERAX_RESULT
import com.adika.storyapp.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import pub.devrel.easypermissions.EasyPermissions

class AddStoryActivity : AppCompatActivity() {
    val viewModel by viewModels<AddStoryViewModel> {
        StoryModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddStoryBinding

    private var currentImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (!checkPermissionCameraGranted() || !checkPermissionLocationGranted()) {
            requestPermissionsIfNeeded()
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.cameraXButton.setOnClickListener { startCameraX() }
        binding.uploadButton.setOnClickListener {
            uploadImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri)
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCameraX() {
        val intent = Intent(this, CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERAX_RESULT) {
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }


    @SuppressLint("MissingPermission")
    private fun uploadImage() {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.etDescription.text.toString()

            if (binding.checkBoxIncludeLocation.isChecked) {
                if (checkPermissionLocationGranted() && checkPermissionCameraGranted()) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                        if (location != null){
                            val lat = location.latitude
                            val lon = location.longitude
                            viewModel.uploadImageWithLocation(imageFile, description, lat, lon)
                        }
                    }
                } else {
                    requestPermissionsIfNeeded()
                }
            } else {
                // Lokasi tidak diminta oleh pengguna, lakukan tindakan lainnya
                viewModel.uploadImage(imageFile, description)
            }

            viewModel.loading.observe(this) {
                showLoading(it)
            }
        } ?: showToast(getString(R.string.empty_image_warning))

        viewModel.status.observe(this) { isSucess ->
            if (isSucess) {
                showToast("Story berhasil diunggah")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            } else {
                viewModel.error.observe(this) { errorMessage ->
                    if (errorMessage != null) {
                        showToast("$errorMessage")
                    }
                }
            }
        }
    }

    private fun checkPermissionCameraGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION_CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun checkPermissionLocationGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissionsIfNeeded() {
        val permissions = mutableListOf<String>()

        if (!checkPermissionCameraGranted()) {
            permissions.add(REQUIRED_PERMISSION_CAMERA)
        }

        if (!checkPermissionLocationGranted()) {
            permissions.add(REQUIRED_PERMISSION_LOCATION)
        }

        if (permissions.isNotEmpty()) {
            EasyPermissions.requestPermissions(
                this,
                "Aplikasi membutuhkan izin",
                PERMISSION_REQUEST_CODE,
                *permissions.toTypedArray()
            )
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUIRED_PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val REQUIRED_PERMISSION_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
        private const val PERMISSION_REQUEST_CODE = 123
    }
}