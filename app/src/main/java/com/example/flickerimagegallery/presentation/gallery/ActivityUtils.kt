package com.example.flickerimagegallery.presentation.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.*
import com.example.flickerimagegallery.utils.FileHelper
import java.io.*

fun Activity.getContentIntent(requestCode: Int) {
    val mimeTypes = arrayOf(
        "image/jpeg",
        "image/png"
    )
    val intents = ArrayList<Intent>()
    intents.add(
        Intent(
            Intent.ACTION_GET_CONTENT,
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