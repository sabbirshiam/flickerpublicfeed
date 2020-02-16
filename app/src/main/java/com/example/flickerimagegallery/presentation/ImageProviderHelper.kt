package com.example.flickerimagegallery.presentation

import android.net.Uri

class ImageProviderHelper : androidx.core.content.FileProvider() {
    override fun getType(uri: Uri): String {
        return "image/jpeg"
    }
}