package com.example.flickerimagegallery.di.modules

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module
import javax.inject.Singleton
import dagger.Provides


@Module
abstract class AppModule {

    @Binds
    internal abstract fun bindContext(application: Application): Context
}