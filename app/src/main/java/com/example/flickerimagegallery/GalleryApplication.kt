package com.example.flickerimagegallery

import android.app.Application
import com.example.flickerimagegallery.data.RetrofitClientInstance
import com.example.flickerimagegallery.di.AppComponent
import com.example.flickerimagegallery.di.DaggerAppComponent
import com.example.flickerimagegallery.utils.BaseScheduler
import com.google.firebase.FirebaseApp


class GalleryApplication : Application() {

    private var clientInstance: RetrofitClientInstance? = null
    private var scheduler: BaseScheduler? = null

    override fun onCreate() {
        super.onCreate()
        clientInstance = RetrofitClientInstance.getInstance()
        scheduler = BaseScheduler()
        appComponent = DaggerAppComponent
            .builder()
            .application(this)
            .build()
        FirebaseApp.initializeApp(this)
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

    companion object {
        private lateinit var appComponent: AppComponent
        fun getAppComponent(): AppComponent {
            return appComponent
        }
    }
}