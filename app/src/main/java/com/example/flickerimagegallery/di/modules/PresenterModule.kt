package com.example.flickerimagegallery.di.modules

import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.example.flickerimagegallery.presentation.uploadimage.UploadImagePresenter
import com.example.flickerimagegallery.presentation.uploadimage.UploadImagePresenterImpl
import dagger.Module
import dagger.Provides

@Module
class PresenterModule {
    @Provides
    fun provideUploadImagePresenter(uploadFile: UploadFile): UploadImagePresenter {
        return UploadImagePresenterImpl(uploadFile)
    }
}
