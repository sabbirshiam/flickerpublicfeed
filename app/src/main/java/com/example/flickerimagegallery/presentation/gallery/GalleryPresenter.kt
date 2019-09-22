package com.example.flickerimagegallery.presentation.gallery

import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter.*
import kotlinx.coroutines.*


interface GalleryPresenter {
    fun takeView(view: GalleryView)
    fun dropView()
    fun getGalleryItemCount(): Int
    fun onBindGalleryItemView(
        holder: GalleryItemViewHolder,
        position: Int
    )

    fun onReloadClick()
    fun onClickSortByPublished()
    fun onClickSortByDateTaken()
}

class GalleryPresenterImpl constructor(private val getFlickerPublicInfo: GetFlickerPublicInfo) :
    GalleryPresenter {
    private var galleryView: GalleryView? = null
    private var flickerFeedPhotos: ArrayList<Item> = ArrayList()

    override fun takeView(view: GalleryView) {
        galleryView = view
        fetchGalleryItems()
    }

    private fun fetchGalleryItems() {
        if (flickerFeedPhotos.isEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                flickerFeedPhotos = getFlickerPublicInfo.getPublicPhotos()
                withContext(Dispatchers.Main) {
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
}