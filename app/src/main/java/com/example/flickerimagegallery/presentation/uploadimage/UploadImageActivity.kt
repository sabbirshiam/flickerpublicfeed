package com.example.flickerimagegallery.presentation.uploadimage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.domain.models.ImageContentModel
import com.example.flickerimagegallery.domain.models.ImagePreviewModel
import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageView.Companion.IMAGE_CAPTURE_REQUEST_CODE
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageView.Companion.STORAGE_STORAGE_REQUEST_CODE
import com.example.flickerimagegallery.presentation.gallery.askPermission
import com.example.flickerimagegallery.presentation.gallery.getContentIntent
import com.example.flickerimagegallery.presentation.gallery.isHasPermission
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageContentView
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadAdapter
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadAdapter.ImageContentItemViewHolder
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadAdapter.ImagePreviewItemViewHolder
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadView
import com.example.flickerimagegallery.utils.FileHelper
import kotlinx.android.synthetic.main.activity_upload_image.*

interface UploadImageView {
    fun openImageChooser()
    fun clearCacheFiles()
    fun onBindContentViewHolder(holder: ImageContentItemViewHolder, dataModel: ImageContentModel)
    fun onBindPreviewViewHolder(holder: ImagePreviewItemViewHolder, dataModel: ImagePreviewModel)
    fun notifyItemChanged()

    enum class ViewType(val type: Int) {
        IMAGE_CONTENT(1),
        IMAGE_PREVIEW(2);
    }

    companion object {
        const val STORAGE_STORAGE_REQUEST_CODE: Int = 1010
        const val IMAGE_CAPTURE_REQUEST_CODE: Int = 1000
    }
}

class UploadImageActivity : AppCompatActivity(), UploadImageView {

    private lateinit var presenter: UploadImagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        presenter = UploadImagePresenterImpl(
            UploadFile(applicationContext)
        )
        imageUploadView.adapter = initImageUploadAdapter()
    }

    override fun onStart() {
        super.onStart()
        presenter.takeView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }

    override fun openImageChooser() {
        val permissions =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (!isHasPermission(permissions))
            askPermission(STORAGE_STORAGE_REQUEST_CODE, permissions)
        else {
            getContentIntent(IMAGE_CAPTURE_REQUEST_CODE)
        }
    }

    override fun clearCacheFiles() {
        FileHelper.clearCache(applicationContext)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_CAPTURE_REQUEST_CODE -> {
                    data?.data.let {
                        //FileHelper.getBitmapFromUri(this, it)
                        presenter.uploadFile(FileHelper.getImagePath(applicationContext, it))
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            STORAGE_STORAGE_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    openImageChooser()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
        }
    }

    private fun initImageUploadAdapter(): ImageUploadAdapter =
        ImageUploadAdapter(
            contentManager = object : ImageUploadAdapter.ContentManager {
                override fun getItemCount(): Int = presenter.getItemCount()
                override fun getItemViewType(position: Int): Int =
                    presenter.getItemViewType(position)
            },
            contentViewCreator = object : ImageUploadAdapter.ContentViewCreator {
                override fun createImageContentView(context: Context): ImageContentView {
                    return ImageContentView(context).apply {
                        setOnClickListener {
                            (getTag(R.id.VIEW_TAG_POSITION) as? Int)?.let { position ->
                                //presenter.onClickImage(position)
                            }
                        }

                    }
                }

                override fun createImagePreviewView(context: Context): ImageUploadView {
                    return ImageUploadView(context).apply {
                        setOnClickListener {
                            (getTag(R.id.VIEW_TAG_POSITION) as? Int)?.let { position ->
                                presenter.onClickImage(position)
                            }
                        }
                    }
                }
            },
            bindListener = object : ImageUploadAdapter.OnBindListener {
                override fun onBindContentView(
                    holder: ImageContentItemViewHolder,
                    position: Int
                ) {
                    holder.getView()?.setTag(R.id.VIEW_TAG_POSITION, holder.adapterPosition)
                    presenter.onBindContentView(holder, position)
                }

                override fun onBindPreviewView(
                    holder: ImagePreviewItemViewHolder,
                    position: Int
                ) {

                    holder.getView()?.setTag(R.id.VIEW_TAG_POSITION, holder.adapterPosition)
                    presenter.onBindImagePreviewView(holder, position)
                }
            })

    override fun onBindContentViewHolder(
        holder: ImageContentItemViewHolder,
        dataModel: ImageContentModel
    ) {
        holder.getView()?.bindImageContent(dataModel)
    }

    override fun onBindPreviewViewHolder(
        holder: ImagePreviewItemViewHolder,
        dataModel: ImagePreviewModel
    ) {
        holder.getView()?.bindImagePrevContent(dataModel)
    }

    override fun notifyItemChanged() {
        imageUploadView.adapter?.notifyDataSetChanged()
    }
}