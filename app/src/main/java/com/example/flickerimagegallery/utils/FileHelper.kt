package com.example.flickerimagegallery.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target.SIZE_ORIGINAL
import com.example.flickerimagegallery.BuildConfig
import java.io.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutionException


object FileHelper {

    private var currentPhotoPath: String? = null
    const val IMAGE_FILE_NAME = "documents_temp"
    const val IMAGE_FILE_DIRECTORIES = "documents"
    const val IMAGE_FILE_EXTENSION = ".png"

    fun getFileName(): String =
        //SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        Calendar.getInstance().get(Calendar.MILLISECOND).toString().plus(IMAGE_FILE_NAME).plus(IMAGE_FILE_EXTENSION)

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        // Create an image file name


        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            IMAGE_FILE_NAME, /* prefix */
            ".".plus(IMAGE_FILE_EXTENSION), /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    fun getImagePath(context: Context, uri: Uri?): String {
        uri?.let {
            //clearCache(context)
            currentPhotoPath = convertMediaUriToPath(context, uri)
            Log.e("URI", "path:: $currentPhotoPath")
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
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val cacheDir = File(context.filesDir.absolutePath.toString())
        // have the object build the directory structure, if needed.
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }

        try {

            Log.d("heel", cacheDir.toString())
            val f = File(
                cacheDir, getFileName()
            )
            f.createNewFile()
            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())

            //Refreshing image on gallery
//            MediaScannerConnection.scanFile(
//                context, arrayOf(f.path), null
//            ) { path, uri -> }
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

    fun getImgCachePath(context: Context, url: String): Uri? {
        val futureTarget =
            Glide.with(context).downloadOnly().load(url).submit(SIZE_ORIGINAL, SIZE_ORIGINAL)
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

//    fun writePictureToGalleryLegacy(context: Context, pictureUri: Uri, pictureName: String) {
//
//
//        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
//
//        val correctDir = File("${directory.absolutePath}${File.separator}WaiJuDuDisAndroid")
//        correctDir.mkdirs()
//
//        val file = File("${correctDir.absolutePath}${File.separator}${pictureUri.lastPathSegment}")
//
//        val contentResolver = context.contentResolver
//
//        val outStream: OutputStream = file.outputStream()
//        val inStream: InputStream = contentResolver.openInputStream(pictureUri)!!
//
//        outStream.use {out ->
//            inStream.use { inpt ->
//                inpt.copyTo(out)
//            }
//        }
//
//
//        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
//
//        MediaScannerConnection.scanFile(
//            context,
//            arrayOf(file.absolutePath),
//            arrayOf(mimeType?:"image/*"),
//            null
//        )
//
//    }

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
}