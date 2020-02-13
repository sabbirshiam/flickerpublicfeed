package com.example.flickerimagegallery.data.repositories.file_upload

import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import okhttp3.MultipartBody
import retrofit2.http.*

interface FileUploadService {
    @POST(BuildConfig.FILE_UPLOAD_API_PREFIX.plus("upload-avatar"))
    suspend fun uploadFile(
        @Body body: MultipartBody
    ): retrofit2.Response<FileUploadResponse>
}