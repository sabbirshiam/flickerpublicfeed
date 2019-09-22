package com.example.flickerimagegallery.presentation.gallery

import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo


interface GalleryPresenter {
    fun takeView(view: GalleryView)
    fun dropView()
}
class GalleryPresenterImpl constructor(private val getFlickerPublicInfo: GetFlickerPublicInfo):
    GalleryPresenter {
    private var galleryView: GalleryView? = null

    override fun takeView(view: GalleryView) {
        galleryView = view
        getFlickerPublicInfo.getPublicPhotos()
    }

    override fun dropView() {
        galleryView = null
    }
}