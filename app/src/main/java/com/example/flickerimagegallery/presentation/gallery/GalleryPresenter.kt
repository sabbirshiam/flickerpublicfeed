package com.example.flickerimagegallery.presentation.gallery

import androidx.recyclerview.widget.RecyclerView
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import kotlinx.coroutines.*


interface GalleryPresenter {
    fun takeView(view: GalleryView)
    fun dropView()
    fun getGalleryItemCount(): Int
    fun onBindGalleryItemView(
        holder: RecyclerView.ViewHolder,
        position: Int
    )
}
class GalleryPresenterImpl constructor(private val getFlickerPublicInfo: GetFlickerPublicInfo):
    GalleryPresenter {
    private var galleryView: GalleryView? = null
    private var flickerFeedPhotos: ArrayList<String> = ArrayList()

    override fun takeView(view: GalleryView) {
        galleryView = view
        fetchGalleryItems()
    }

    private fun fetchGalleryItems() {
        CoroutineScope(Dispatchers.IO).launch {
            flickerFeedPhotos  = getFlickerPublicInfo.getPublicPhotos()
            withContext(Dispatchers.Main){
                if (flickerFeedPhotos.isNotEmpty()) {
                    galleryView?.notifyDataSetChanged()
                }

            }
        }
    }

    override fun dropView() {
        galleryView = null
    }

    override fun getGalleryItemCount(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onBindGalleryItemView(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}