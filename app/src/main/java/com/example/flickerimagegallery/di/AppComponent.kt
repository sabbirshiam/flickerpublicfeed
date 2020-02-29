package com.example.flickerimagegallery.di

import android.app.Application
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.di.modules.*
import com.example.flickerimagegallery.presentation.BaseActivity
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageActivity
import com.example.flickerimagegallery.services.MessagingService
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        ActivityModule::class,
        DomainModule::class,
        ServiceModule::class,
        PresenterModule::class
    ]
)
interface AppComponent : AndroidInjector<GalleryApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }

    fun inject(messagingService: MessagingService)
    fun inject(activity: UploadImageActivity)
}