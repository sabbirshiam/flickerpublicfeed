package com.example.flickerimagegallery.data

import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.repositories.FlickerService
import com.example.flickerimagegallery.data.repositories.file_upload.FileUploadService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory


class RetrofitClientInstance {

    companion object {
        private var _instance: RetrofitClientInstance? = null
        fun getInstance(): RetrofitClientInstance {
            _instance = _instance ?: RetrofitClientInstance()
            return _instance!!
        }
    }

    private val retrofitInstance: Retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BuildConfig.API_PREFIX)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val retrofitInstanceWithFile: Retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BuildConfig.FILE_UPLOAD_API_PREFIX)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val flickerService: FlickerService =
        retrofitInstance.create(FlickerService::class.java)

    val fileUploadService: FileUploadService =
        retrofitInstanceWithFile.create(FileUploadService::class.java)
}