package com.example.flickerimagegallery.data.repositories.file_upload

import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface FileUploadService {
    @Multipart
    @POST(BuildConfig.FILE_UPLOAD_API_PREFIX.plus("upload-avatar"))
    suspend fun uploadFile(
        @Part("avatar") file: RequestBody
    ): retrofit2.Response<FileUploadResponse>
}