package com.example.flickerimagegallery.services

import android.content.Intent
import android.util.Log
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.android.AndroidInjection
import javax.inject.Inject

class MessagingService @Inject constructor(private val uploadFile: UploadFile) : FirebaseMessagingService() {

//    @Inject
//    lateinit var uploadFile: UploadFile

    override fun onCreate() {
        GalleryApplication.getAppComponent().inject(this)
        super.onCreate()
    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.e("token:: ", "" + p0)
        Log.e("token:: ", "" + uploadFile.hashCode().toString())
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
        Log.e("token:: ", uploadFile.hashCode().toString())
    }
}