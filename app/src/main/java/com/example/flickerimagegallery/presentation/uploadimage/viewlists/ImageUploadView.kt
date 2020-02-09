package com.example.flickerimagegallery.presentation.uploadimage.viewlists

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.domain.models.ImagePreviewModel
import kotlinx.android.synthetic.main.image_upload_list_preview.view.*

class ImageUploadView: ConstraintLayout {

    constructor(context: Context) : super(context, null)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        LayoutInflater.from(context).inflate(R.layout.image_upload_list_preview, this, true)
        layoutParams = LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    fun bindImagePrevContent(model: ImagePreviewModel) {
        Glide.with(this)
            .load(model.image)
            .centerCrop()
            .into(imagePreview)
    }
}