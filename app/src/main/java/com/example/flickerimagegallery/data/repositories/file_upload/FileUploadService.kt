package com.example.flickerimagegallery.data.repositories.file_upload

import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.POST

interface FileUploadService {
    @POST(BuildConfig.FILE_UPLOAD_API_PREFIX.plus("upload-avatar"))
    fun uploadFile(
        @Body body: MultipartBody
    ): Single<FileUploadResponse>
}