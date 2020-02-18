package com.example.flickerimagegallery.presentation.gallery

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.flickerimagegallery.utils.FileHelper
import java.io.File
import java.io.IOException

fun Activity.getContentIntent(requestCode: Int) {
    val mimeTypes = arrayOf(
        "image/*"
    )
    val intents = ArrayList<Intent>()
    intents.add(
        Intent(
            Intent.ACTION_OPEN_DOCUMENT,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        ).apply {
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            addCategory(Intent.CATEGORY_OPENABLE)
        }
    )
    setCameraIntents(intents)

    if (intents.isEmpty()) return
    val result = Intent.createChooser(intents.removeAt(0), "Choose Images")
    if (intents.isNotEmpty()) {
        result.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents.toTypedArray<Parcelable>())
    }
    startActivityForResult(result, requestCode)
}

fun Activity.setCameraIntents(cameraIntents: MutableList<Intent>) {
    val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

    captureIntent.also { takePictureIntent ->
        // Ensure that there's a camera activity to handle the intent
        takePictureIntent.resolveActivity(packageManager)?.also {
            // Create the File where the photo should go
            val photoFile: File? = try {
                FileHelper.createImageFile(this)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Log.e("Error", "Error occured while creating the file")
                null
            }
            // Continue only if the File was successfully created
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "com.example.flickerimagegallery.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                MediaScannerConnection.scanFile(
//                    this,
//                    arrayOf(it.path),
//                    arrayOf("image/*"),
//                    null)
            }
        }
    }

    cameraIntents.add(captureIntent)
}

fun Activity.isHasPermission(permissions: Array<String>): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        var permissionFlag = true
        for (singlePermission in permissions) {
            permissionFlag =
                applicationContext.checkSelfPermission(singlePermission) == PackageManager.PERMISSION_GRANTED
        }
        return permissionFlag
    }
    return true
}

fun Activity.askPermission(requestCode: Int, permissions: Array<String>) {
    ActivityCompat.requestPermissions(this, permissions, requestCode)
}