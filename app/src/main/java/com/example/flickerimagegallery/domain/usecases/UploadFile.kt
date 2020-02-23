package com.example.flickerimagegallery.domain.usecases

import android.content.Context
import com.example.flickerimagegallery.data.entities.FileUploadResponse
import com.example.flickerimagegallery.data.repositories.file_upload.FileUploadRepository
import io.reactivex.Single

class UploadFile(private val context: Context) {

    private val fileUploadRepository: FileUploadRepository = FileUploadRepository(context)

    fun uploadFile(filePath: String): Single<FileUploadResponse> {
        //        if (publicFeeds.isSuccessful) {
//            Log.e("File", "Uploading is success")
//            Log.e("File", "${publicFeeds.body()}")
//        } else {
//            Log.e("File", "Uploading failed:: ${publicFeeds.body().toString()}")
//        }
        return fileUploadRepository.uploadFile(filePath)
    }
}
