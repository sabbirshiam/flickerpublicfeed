package com.example.flickerimagegallery.data.repositories.file_upload

import android.content.Context
import android.util.Log
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File

class FileUploadRepository(private val context: Context) {
    private val application: GalleryApplication = context as GalleryApplication

    suspend fun uploadFile(path: String): Response<FileUploadResponse> {
        val file = File(path)
        Log.e("File", "Files exists..")

        //context.contentResolver.getType(file.toUri())

        // if we want to create a builder
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("name", "hoiyagese")

        if (file.exists()) {
            builder.addFormDataPart(
                "avatar",
                file.name,
                RequestBody.create(MediaType.parse("image/*"), file)
            )
        }

        // MultipartBody.Part is used to send also the actual file name
        return application.getRetrofitIntance().fileUploadService.uploadFile(builder.build())
    }
}