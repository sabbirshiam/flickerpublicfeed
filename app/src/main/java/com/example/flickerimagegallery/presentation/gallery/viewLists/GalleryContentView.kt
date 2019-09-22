package com.example.flickerimagegallery.presentation.gallery.viewLists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.data.entities.Item
import kotlinx.android.synthetic.main.gallery_list_item.view.*
import kotlinx.coroutines.*
import java.net.URL

class GalleryContentView : ConstraintLayout {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.gallery_list_item, this, true)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun bindGalleryItem(model: Item) {
        title.text = if (model.title.isNotBlank()) { model.title } else { context.getString(R.string.no_title) }
        published.text = model.published
        CoroutineScope(Dispatchers.Main).launch {
            val originalBitmap = getOriginalBitmapAsync(model.media.image).await()
            originalBitmap.let {
                progressBar.visibility = View.GONE
                galleryItemImage.setImageBitmap(it)
            }
        }
    }

    private fun getOriginalBitmapAsync(imageUrlString: String): Deferred<Bitmap> =
        CoroutineScope(Dispatchers.IO).async {
            URL(imageUrlString).openStream().use {
                return@async BitmapFactory.decodeStream(it)
            }
        }
}