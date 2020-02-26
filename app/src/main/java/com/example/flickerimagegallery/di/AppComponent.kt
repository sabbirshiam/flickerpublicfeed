package com.example.flickerimagegallery.di

import android.app.Application
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.di.modules.AppModule
import com.example.flickerimagegallery.di.modules.DomainModule
import com.example.flickerimagegallery.di.modules.ServiceModule
import com.example.flickerimagegallery.services.MessagingService
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class,
        DomainModule::class,
        ServiceModule::class
    ]
)
interface AppComponent: AndroidInjector<GalleryApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

//        @BindsInstance // you'll call this when setting up Dagger
//        fun bindMessagingService(messagingService: MessagingService): Builder
        fun build(): AppComponent
    }
    fun inject(messagingService: MessagingService)
}