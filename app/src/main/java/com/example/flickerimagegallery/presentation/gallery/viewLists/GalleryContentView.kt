package com.example.flickerimagegallery.presentation.gallery.viewLists

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.AttributeSet
import android.view.LayoutInflater
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
        CoroutineScope(Dispatchers.Main).launch {
            val originalBitmap = getOriginalBitmapAsync(model.media.image).await()
            originalBitmap.let {
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