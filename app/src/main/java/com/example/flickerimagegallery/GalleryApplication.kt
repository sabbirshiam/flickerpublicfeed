package com.example.flickerimagegallery

import android.app.Application
import com.example.flickerimagegallery.data.RetrofitClientInstance

class GalleryApplication: Application() {

    private var clientInstance:RetrofitClientInstance?= null

    override fun onCreate() {
        super.onCreate()
        clientInstance = RetrofitClientInstance.getInstance()
    }

    fun getRetrofitIntance(): RetrofitClientInstance = clientInstance ?: RetrofitClientInstance.getInstance()

    override fun getApplicationContext(): GalleryApplication {
        return this
    }
}