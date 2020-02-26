package com.example.flickerimagegallery.di.modules

import com.example.flickerimagegallery.services.MessagingService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

    @ContributesAndroidInjector(modules = [])
    internal abstract fun providesMessagingService(): MessagingService
}