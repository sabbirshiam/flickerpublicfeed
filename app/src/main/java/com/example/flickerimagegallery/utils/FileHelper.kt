package com.example.flickerimagegallery.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

object FileHelper {

    private var currentPhotoPath: String? = null

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun getImagePath(context: Context, uri: Uri?): String {
        uri?.let {
            clearCache(context)
                .also { currentPhotoPath = null }
                .also {
                    currentPhotoPath = convertMediaUriToPath(context, uri)
                    Log.e("URI", "path:: $currentPhotoPath")
                }
        }
        return currentPhotoPath ?: ""
    }

    fun clearCache(context: Context) {
        context.cacheDir.list()?.iterator()?.forEach {
            context.deleteFile(it)
        }
        Log.e("File", "cache size:: ${context.cacheDir.list()?.size}")
        currentPhotoPath?.let { path ->
            val file = File(path)
            file?.delete()
            Log.e("data", file.exists().toString())
        }
    }

    @Throws(IOException::class, FileNotFoundException::class)
    private fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")
        parcelFileDescriptor?.let {
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        }

        return null
    }

    private fun saveImage(context: Context, bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val cacheDir = File(context.cacheDir.absolutePath.toString())
        // have the object build the directory structure, if needed.
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        try {
            Log.d("heel", cacheDir.toString())
            val f = File(
                cacheDir, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".png")
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
//            MediaScannerConnection.scanFile(
//                context,
//                arrayOf(f.path),
//                arrayOf("image/jpeg"), null
//            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)

            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }

    private fun convertMediaUriToPath(context: Context, uri: Uri): String {
        val image = getBitmapFromUri(context, uri)
        image?.let {
            return saveImage(context, image)
        }
        return ""
    }
}