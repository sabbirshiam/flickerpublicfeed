package com.example.flickerimagegallery.data.repositories.file_upload

import android.content.Context
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.File

class FileUploadRepository(private val context: Context) {
    private val application: GalleryApplication = context as GalleryApplication

    suspend fun uploadFile(path: String): Response<FileUploadResponse> {
        val file = File(path)

        val requestFile: RequestBody = RequestBody.create(
            MediaType.parse(file.absolutePath),
            file
        )

        // MultipartBody.Part is used to send also the actual file name
        val body : MultipartBody.Part = MultipartBody.Part.createFormData("picture", file.name, requestFile)
        return application.getRetrofitIntance().fileUploadService.uploadFile(requestFile)
    }
}