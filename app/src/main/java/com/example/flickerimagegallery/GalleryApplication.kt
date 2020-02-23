package com.example.flickerimagegallery

import android.app.Application
import com.example.flickerimagegallery.data.RetrofitClientInstance
import com.example.flickerimagegallery.utils.BaseScheduler

class GalleryApplication : Application() {

    private var clientInstance: RetrofitClientInstance? = null
    private var scheduler: BaseScheduler? = null

    override fun onCreate() {
        super.onCreate()
        clientInstance = RetrofitClientInstance.getInstance()
        scheduler = BaseScheduler()
    }

    fun getRetrofitIntance(): RetrofitClientInstance =
        clientInstance ?: RetrofitClientInstance.getInstance()

    fun getScheduler(): BaseScheduler {
        if (scheduler == null) {
            scheduler = BaseScheduler()

        }
        return scheduler as BaseScheduler
    }


    override fun getApplicationContext(): GalleryApplication {
        return this
    }
}