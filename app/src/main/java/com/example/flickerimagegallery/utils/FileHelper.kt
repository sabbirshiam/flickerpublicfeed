package com.example.flickerimagegallery.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
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
    const val AUTHORITY = BuildConfig.APPLICATION_ID + ".fileprovider"

    fun getFileName(): String = IMAGE_FILE_NAME.plus(IMAGE_FILE_EXTENSION)
    //Calendar.getInstance().get(Calendar.MILLISECOND).toString().plus("_").plus(

    fun getDownloadFileName(): String =
        Calendar.getInstance()
            .get(Calendar.MILLISECOND).toString()
            .plus("_")
            .plus(IMAGE_FILE_NAME)
            .plus(IMAGE_FILE_EXTENSION)

    @Throws(IOException::class)
    fun createImageFile(context: Context): File {
        val file = getLocalFile(context, getFileName())
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
        //val mediaStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.e("File", "FilesDir size before:: ${mediaStorageDir?.list()?.size}")
        mediaStorageDir?.list()?.iterator()?.forEach {
            val filePath = mediaStorageDir.absolutePath.plus(separator).plus(it)
            File(filePath).delete()
        }
        Log.e("File", "cache size:: ${mediaStorageDir?.list()?.size}")
    }

    @Throws(IOException::class, FileNotFoundException::class)
    fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }
        val parcelFileDescriptor: ParcelFileDescriptor? =
            context.contentResolver.openFileDescriptor(uri, "r")
        parcelFileDescriptor?.let {
            val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
            val image: Bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
           // parcelFileDescriptor.close()
                Log.e("IMAGE_SIZE", " bytes "+image.byteCount)
                val scaledImage = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options).run {
                // Decode bitmap with inSampleSize set
                options.inJustDecodeBounds = false
                options.inSampleSize = calculateInSampleSize(options, 200  , 200)
                BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options)
            }
            parcelFileDescriptor.close()
            Log.e("IMAGE_SIZE", "scaledImage bytes " +  scaledImage.byteCount)
            var testScaledImage = Bitmap.createScaledBitmap(image, 1280, 720, false)
            Log.e("IMAGE_SIZE", "testScaledImage bytes " +  testScaledImage.byteCount)
            return testScaledImage
        }

        return null
    }

    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun saveImage(context: Context, bitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val file = getLocalFile(context, getFileName())

        try {
            val fo = FileOutputStream(file)
            fo.write(bytes.toByteArray())
            fo.close()
            //Refreshing image on gallery
            //insertIntoMediaStore(context, file)
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
            val localFile = getLocalFile(context, "image_1.jpg")
            try {
                val outStream: OutputStream = FileOutputStream(localFile)
                val inStream: InputStream = FileInputStream(file)
                outStream.use { out ->
                    inStream.use { input ->
                        input.copyTo(out)
                        input.close()
                    }
                    outStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            Log.e("FILE_ABS", "" + file.absolutePath)
            //insertIntoMediaStore(context, file)
            return FileProvider.getUriForFile(
                context,
                BuildConfig.APPLICATION_ID + ".fileprovider",
                localFile
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
            //insertIntoMediaStore
            var test = getImgCachePath(context, localDocumentPath)
            try {

                val outStream: OutputStream = context.contentResolver.openOutputStream(uri)!!
                val inStream: InputStream =
                    test?.let { context.contentResolver.openInputStream(it) }!!

                outStream.use { out ->
                    inStream.use { inpt ->
                        inpt.copyTo(out)
                        inpt.close()
                    }
                    outStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    fun getLocalFile(context: Context, fileName: String): File {
        val mediaStorageDir = File(context.filesDir, "Pictures")
        //val mediaStorageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir?.exists()!! && !mediaStorageDir?.mkdirs()) {
            Log.d(LOG_TAG, "failed to create directory")
        }

        return File(mediaStorageDir.path + separator + fileName)
    }

    fun insertIntoMediaStore(context: Context, file: File): Uri {
        val collection =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            ) else MediaStore.Images.Media.EXTERNAL_CONTENT_URI


        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, getFileName())
            // put(MediaStore.Images.Media.RELATIVE_PATH, "images/images")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            //  put(MediaStore.Images.Media.IS_PENDING, 1)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(collection, values)

        uri?.let {
            resolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(file.readBytes())
                outputStream.close()
            }

            // values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
            values.clear()
        } ?: throw RuntimeException("MediaStore failed for some reason")

        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf("image/jpeg"),
            null
        )

        return FileProvider.getUriForFile(context, AUTHORITY, file)
    }
}