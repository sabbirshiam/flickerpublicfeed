package com.example.flickerimagegallery.domain.usecases

import android.content.Context
import android.util.Log
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import com.example.flickerimagegallery.data.repositories.file_upload.FileUploadRepository
import okhttp3.ResponseBody

class UploadFile(private val context: Context) {

    private val fileUploadRepository: FileUploadRepository = FileUploadRepository(context)

    suspend fun uploadFile(filePath: String) {
        val publicFeeds: retrofit2.Response<FileUploadResponse> = fileUploadRepository.uploadFile(filePath)
        if (publicFeeds.isSuccessful) {
            Log.e("File", "Uploading is success")
            Log.e("File", "${publicFeeds.body()}")

        } else {
            Log.e("File", "Uploading failed:: ${publicFeeds.body().toString()}")
        }
    }
}
