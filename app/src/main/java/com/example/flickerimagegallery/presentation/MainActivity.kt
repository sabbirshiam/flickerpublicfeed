package com.example.flickerimagegallery.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.services.MessagingService
import com.example.flickerimagegallery.utils.CoroutineContextProvider
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
