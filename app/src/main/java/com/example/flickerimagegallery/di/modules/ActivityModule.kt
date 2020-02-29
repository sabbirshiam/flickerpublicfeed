package com.example.flickerimagegallery.di.modules

import com.example.flickerimagegallery.di.ActivityScoped
import com.example.flickerimagegallery.presentation.gallery.GalleryActivity
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ActivityScoped
    @ContributesAndroidInjector
    abstract fun galleryActivity(): GalleryActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [ServiceModule::class])
    abstract fun uploadImageActivity(): UploadImageActivity
}