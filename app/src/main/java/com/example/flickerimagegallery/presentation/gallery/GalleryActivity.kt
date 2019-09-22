package com.example.flickerimagegallery.presentation.gallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo

interface GalleryView {

}

class GalleryActivity : AppCompatActivity(), GalleryView {
    private var presenter: GalleryPresenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        presenter = GalleryPresenterImpl(GetFlickerPublicInfo())
    }

    override fun onStart() {
        super.onStart()
        presenter?.takeView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter?.dropView()
    }
}
