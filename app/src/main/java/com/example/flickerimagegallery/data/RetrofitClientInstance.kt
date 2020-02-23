package com.example.flickerimagegallery.data

import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.repositories.FlickerService
import com.example.flickerimagegallery.data.repositories.file_upload.FileUploadService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitClientInstance {

    companion object {
        private var _instance: RetrofitClientInstance? = null
        fun getInstance(): RetrofitClientInstance {
            _instance = _instance ?: RetrofitClientInstance()
            return _instance!!
        }
    }

    private var logging: HttpLoggingInterceptor = HttpLoggingInterceptor()
        .setLevel(HttpLoggingInterceptor.Level.BASIC)

    private var httpClient: OkHttpClient.Builder = OkHttpClient.Builder()
        .callTimeout(2, TimeUnit.MINUTES)
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(logging)

    private val retrofitInstance: Retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BuildConfig.API_PREFIX)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(httpClient.build())
        .build()

    private val retrofitInstanceWithFile: Retrofit = retrofit2.Retrofit.Builder()
        .baseUrl(BuildConfig.FILE_UPLOAD_API_PREFIX)
        .addConverterFactory(MoshiConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .client(httpClient.build())
        .build()

    val flickerService: FlickerService =
        retrofitInstance.create(FlickerService::class.java)

    val fileUploadService: FileUploadService =
        retrofitInstanceWithFile.create(FileUploadService::class.java)
}