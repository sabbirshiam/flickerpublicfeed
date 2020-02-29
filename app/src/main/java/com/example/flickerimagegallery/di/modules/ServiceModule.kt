package com.example.flickerimagegallery.di.modules

import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.example.flickerimagegallery.services.MessagingService
import dagger.Module
import dagger.Provides

@Module
class ServiceModule {
    @Provides
    fun providesMessagingService(uploadFile: UploadFile): MessagingService {
        return MessagingService(uploadFile)
    }
}