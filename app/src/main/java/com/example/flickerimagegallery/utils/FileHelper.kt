package com.example.flickerimagegallery.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.flickerimagegallery.BuildConfig
import java.io.*
import java.io.File.separator
import java.util.*
import java.util.concurrent.ExecutionException


object FileHelper {

    private var currentPhotoPath: String? = null
    const val IMAGE_FILE_NAME = "documents_temp"
    const val IMAGE_FILE_DIRECTORIES = "documents"
    const val IMAGE_FILE_EXTENSION = ".jpg"
    const val LOG_TAG = "FILE"

    fun getFileName(): String =
        Calendar.getInstance().get(Calendar.MILLISECOND).toString().plus("_").plus(IMAGE_FILE_NAME).plus(IMAGE_FILE_EXTENSION)
    //Calendar.getInstance().get(Calendar.MILLISECOND).toString().plus("_").plus(

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val file = getPhotoFileUri(context, getFileName())
        currentPhotoPath = file.absolutePath
        return file

    }

    fun getImagePath(context: Context, uri: Uri?): String {
        uri?.let {
            currentPhotoPath = convertMediaUriToPath(context, uri)
            Log.e("URI", "path:: $currentPhotoPath")
        }
        return currentPhotoPath ?: ""
    }

    fun clearCache(context: Context) {
        val mediaStorageDir = File(context.filesDir.absolutePath.plus(separator).plus("Pictures"))
        Log.e("File", "FilesDir size before:: ${mediaStorageDir.list()?.size}")
        mediaStorageDir?.list()?.iterator()?.forEach {
            val filePath = mediaStorageDir.absolutePath.plus(separator).plus(it)
            File(filePath).delete()
        }
        Log.e("File", "cache size:: ${mediaStorageDir.list()?.size}")
    }

    @Throws(IOException::class, FileNotFoundException::class)
    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
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
        val file = getPhotoFileUri(context, getFileName())

        try {
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
            //Refreshing image on gallery
            MediaScannerConnection.scanFile(
                context, arrayOf(file.path), null
            ) { _, _ -> }
            Log.e("FILE", "File Saved::--->" + file.absolutePath)

            return file.absolutePath
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

    fun getImgCachePath(context: Context, url: String): Uri? {
        //.apply( RequestOptions().signature( ObjectKey(System.currentTimeMillis())))
        val futureTarget =
            Glide.with(context)
                .downloadOnly()
                .load(url)
                .submit(SIZE_ORIGINAL, SIZE_ORIGINAL)
        try {
            val file = futureTarget.get()
            Log.e("FILE_ABS", "" + file.absolutePath)
            return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                file
            )
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }
        return null
    }

    fun downloadImage(context: Context, uri: Uri?, localDocumentPath: String?) {
        uri ?: return
        localDocumentPath ?: return
        try {
            var test = getImgCachePath(context, localDocumentPath)
            try {

                val outStream: OutputStream = context.contentResolver.openOutputStream(uri)!!
                val inStream: InputStream =
                    test?.let { context.contentResolver.openInputStream(it) }!!

                outStream.use { out ->
                    inStream.use { inpt ->
                        inpt.copyTo(out)
                    }
                }

                /*val mimeType =
                    MimeTypeMap.getSingleton().getMimeTypeFromExtension(test.lastPathSegment)

                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(uri.path),
                    arrayOf(mimeType ?: "image/jpg"),
                    null
                )*/
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(context: Context, fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir = File(context.filesDir, "Pictures")

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(LOG_TAG, "failed to create directory")
        }

        return File(mediaStorageDir.path + separator + fileName)
    }
}