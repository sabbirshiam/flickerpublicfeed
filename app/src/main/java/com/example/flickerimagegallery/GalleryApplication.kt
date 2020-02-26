package com.example.flickerimagegallery

import android.app.Application
import android.app.Service
import com.example.flickerimagegallery.data.RetrofitClientInstance
import com.example.flickerimagegallery.di.DaggerAppComponent
import com.example.flickerimagegallery.utils.BaseScheduler
import com.example.flickerimagegallery.di.AppComponent
import com.example.flickerimagegallery.services.MessagingService
import com.google.firebase.FirebaseApp
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import dagger.android.AndroidInjector
import dagger.android.HasServiceInjector
import dagger.android.DispatchingAndroidInjector
import javax.inject.Inject



class GalleryApplication : Application(), HasServiceInjector {

    private var clientInstance: RetrofitClientInstance? = null
    private var scheduler: BaseScheduler? = null


    @set:Inject
    lateinit var dispatchingServiceInjector: DispatchingAndroidInjector<Service>

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


    override fun serviceInjector(): AndroidInjector<Service> {
        return dispatchingServiceInjector
    }
}