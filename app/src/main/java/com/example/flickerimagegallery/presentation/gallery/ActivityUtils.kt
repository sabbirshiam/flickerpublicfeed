package com.example.flickerimagegallery.presentation.gallery

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.os.Parcelable
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.FileProvider
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.os.Build

fun Activity.getPickIntent(requestCode: Int) {
    val intents = ArrayList<Intent>()
    intents.add(
        Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
    )
    setCameraIntents(intents)

    if (intents.isEmpty()) return
    val result = Intent.createChooser(intents.removeAt(0), null)
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
                createImageFile()
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

private var currentPhotoPath: String? = null

@Throws(IOException::class)
fun Activity.createImageFile(): File {
    // Create an image file name
    val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
    val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(
        "JPEG_${timeStamp}_", /* prefix */
        ".jpg", /* suffix */
        storageDir /* directory */
    ).apply {
        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = absolutePath
    }
}

fun Activity.getImagePath(uri: Uri?): String {
    uri?.let {
        deleteImageFile()
            .also { currentPhotoPath = null }
            .also {
                currentPhotoPath = convertMediaUriToPath(uri)
                Log.e("URI", "path:: $currentPhotoPath")
            }
    }
    return currentPhotoPath ?: ""
}

private fun deleteImageFile() {
    currentPhotoPath?.let { path ->
        val file = File(path)
        file?.delete()
        Log.e("data", file.exists().toString())
    }
}

fun Activity.convertMediaUriToPath(uri: Uri): String {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(uri, proj, null, null, null)
    val column_index = cursor?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
    cursor?.moveToFirst()
    val path = column_index?.run { cursor?.getString(this) } ?: ""
    cursor?.close()
    return path
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