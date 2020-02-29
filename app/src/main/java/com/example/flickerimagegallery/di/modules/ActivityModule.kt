package com.example.flickerimagegallery.di.modules

import com.example.flickerimagegallery.presentation.uploadimage.UploadImageActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector(modules = [ServiceModule::class])
    abstract fun uploadImageActivity(): UploadImageActivity
}