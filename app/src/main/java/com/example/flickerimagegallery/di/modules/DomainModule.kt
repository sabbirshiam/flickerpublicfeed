package com.example.flickerimagegallery.di.modules

import android.content.Context
import com.example.flickerimagegallery.domain.usecases.UploadFile
import dagger.Module
import dagger.Provides
import javax.inject.Inject

@Module
class DomainModule {
    @Inject
    fun providesUploadFile(context: Context): UploadFile {
        return UploadFile(context)
    }
}