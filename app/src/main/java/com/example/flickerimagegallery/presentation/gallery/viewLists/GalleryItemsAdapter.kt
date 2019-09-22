package com.example.flickerimagegallery.presentation.gallery.viewLists

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class GalleryItemsAdapter(
    private val contentManager: ContentManager,
    private val contentViewCreator: ContentViewCreator,
    private val bindListener: OnBindListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ContentManager {
        fun getItemCount(): Int
    }

    interface OnBindListener {
        fun onBindGalleryItemView(
            holder: GalleryItemViewHolder,
            position: Int
        )
    }

    interface ContentViewCreator {
        fun createGalleryItemView(context: Context): GalleryContentView
    }

    override fun getItemCount() = contentManager.getItemCount()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GalleryItemViewHolder(contentViewCreator.createGalleryItemView(parent.context))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        bindListener.onBindGalleryItemView(holder as GalleryItemViewHolder, position)


    class GalleryItemViewHolder(itemView: GalleryContentView) : RecyclerView.ViewHolder(itemView) {
        fun getView() = itemView as? GalleryContentView
    }
}