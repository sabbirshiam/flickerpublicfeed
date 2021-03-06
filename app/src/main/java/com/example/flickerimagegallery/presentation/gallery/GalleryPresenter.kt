package com.example.flickerimagegallery.presentation.gallery

import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.example.flickerimagegallery.presentation.BasePresenter
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter.*
import com.example.flickerimagegallery.utils.CoroutineContextProvider
import com.example.flickerimagegallery.utils.FileHelper
import kotlinx.coroutines.*


interface GalleryPresenter: BasePresenter<GalleryView> {
    fun getGalleryItemCount(): Int
    fun onBindGalleryItemView(holder: GalleryItemViewHolder, position: Int)
    fun onReloadClick()
    fun onClickSortByPublished()
    fun onClickSortByDateTaken()
    fun onClickImage(position: Int)
    fun onClickUploadImage()
}

class GalleryPresenterImpl constructor(
    private val getFlickerPublicInfo: GetFlickerPublicInfo,
    private val contextPool: CoroutineContextProvider
    = CoroutineContextProvider()
) :
    GalleryPresenter {
    private var galleryView: GalleryView? = null
    private var flickerFeedPhotos: ArrayList<Item> = ArrayList()

    override fun takeView(view: GalleryView) {
        galleryView = view
        fetchGalleryItems()
    }

    /**
     * only fetch new contents when list of Photos is empty.
     */
    fun fetchGalleryItems() {
        if (flickerFeedPhotos.isEmpty()) {
            CoroutineScope(contextPool.IO).launch {
              //  flickerFeedPhotos = getFlickerPublicInfo.getPublicPhotos()
                withContext(contextPool.Main) {
                    galleryView?.notifyDataSetChanged()
                }
            }
        }
    }

    override fun dropView() {
        galleryView = null
    }

    override fun getGalleryItemCount(): Int {
        return flickerFeedPhotos.count()
    }

    override fun onBindGalleryItemView(holder: GalleryItemViewHolder, position: Int) {
        galleryView?.onBindGalleryItemViewHolder(holder, flickerFeedPhotos[position])
    }

    override fun onReloadClick() {
        flickerFeedPhotos.clear()
        fetchGalleryItems()
    }

    override fun onClickSortByPublished() {
        flickerFeedPhotos.sortByDescending { item -> item.published }
        galleryView?.notifyDataSetChanged()
    }

    override fun onClickSortByDateTaken() {
        flickerFeedPhotos.sortByDescending { item -> item.date_taken }
        galleryView?.notifyDataSetChanged()
    }

    override fun onClickImage(position: Int) {
        galleryView?.openInBrowser(flickerFeedPhotos[position])
    }

    override fun onClickUploadImage() {
        galleryView?.navigateToUploadImage()
    }
}