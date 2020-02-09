package com.example.flickerimagegallery.presentation.uploadimage.viewlists

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageView
import java.lang.IllegalArgumentException

class ImageUploadAdapter(
    private val contentManager: ContentManager,
    private val contentViewCreator: ContentViewCreator,
    private val bindListener: OnBindListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ContentManager {
        fun getItemCount(): Int
        fun getItemViewType(position: Int): Int
    }

    interface OnBindListener {
        fun onBindContentView(
            holder: ImageContentItemViewHolder,
            position: Int
        )

        fun onBindPreviewView(
            holder: ImagePreviewItemViewHolder,
            position: Int
        )
    }

    interface ContentViewCreator {
        fun createImageContentView(context: Context): ImageContentView
        fun createImagePreviewView(context: Context): ImageUploadView
    }

    override fun getItemCount() = contentManager.getItemCount()

    override fun getItemViewType(position: Int): Int = contentManager.getItemViewType(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UploadImageView.ViewType.IMAGE_CONTENT.type -> ImageContentItemViewHolder(
                contentViewCreator.createImageContentView(parent.context)
            )
            UploadImageView.ViewType.IMAGE_PREVIEW.type -> ImagePreviewItemViewHolder(
                contentViewCreator.createImagePreviewView(parent.context)
            )
            else -> throw IllegalArgumentException()
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageContentItemViewHolder ->
                bindListener.onBindContentView(holder, position)
            is ImagePreviewItemViewHolder ->
                bindListener.onBindPreviewView(holder, position)
        }
    }


    class ImageContentItemViewHolder(itemView: ImageContentView) :
        RecyclerView.ViewHolder(itemView) {
        fun getView() = itemView as? ImageContentView
    }

    class ImagePreviewItemViewHolder(itemView: ImageUploadView) :
        RecyclerView.ViewHolder(itemView) {
        fun getView() = itemView as? ImageUploadView
    }
}