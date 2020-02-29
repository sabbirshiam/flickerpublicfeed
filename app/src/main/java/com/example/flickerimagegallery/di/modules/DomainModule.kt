package com.example.flickerimagegallery.di.modules

import android.content.Context
import com.example.flickerimagegallery.domain.usecases.UploadFile
import dagger.Module
import dagger.Provides

@Module
class DomainModule {
    @Provides
    fun providesUploadFile(context: Context): UploadFile {
        return UploadFile(context)
    }
}