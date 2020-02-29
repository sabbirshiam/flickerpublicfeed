package com.example.flickerimagegallery.presentation.uploadimage

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.PopupMenu
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.domain.models.ImageContentModel
import com.example.flickerimagegallery.domain.models.ImagePreviewModel
import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.example.flickerimagegallery.presentation.BaseActivity
import com.example.flickerimagegallery.presentation.BaseView
import com.example.flickerimagegallery.presentation.gallery.askPermission
import com.example.flickerimagegallery.presentation.gallery.getContentIntent
import com.example.flickerimagegallery.presentation.gallery.isHasPermission
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageView.Companion.IMAGE_CAPTURE_REQUEST_CODE
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageView.Companion.REQ_CREATE_DOCUMENT
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageView.Companion.STORAGE_STORAGE_REQUEST_CODE
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageContentView
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadAdapter
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadAdapter.ImageContentItemViewHolder
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadAdapter.ImagePreviewItemViewHolder
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadView
import com.example.flickerimagegallery.services.MessagingService
import com.example.flickerimagegallery.utils.CoroutineContextProvider
import com.example.flickerimagegallery.utils.FileHelper
import com.example.flickerimagegallery.utils.FileHelper.getDownloadFileName
import com.example.flickerimagegallery.utils.showDefaultPopupMenu
import kotlinx.android.synthetic.main.activity_upload_image.*
import kotlinx.android.synthetic.main.image_upload_list_preview.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface UploadImageView: BaseView {
    fun openImageChooser()
    fun clearCacheFiles()
    fun onBindContentViewHolder(holder: ImageContentItemViewHolder, dataModel: ImageContentModel)
    fun onBindPreviewViewHolder(holder: ImagePreviewItemViewHolder, dataModel: ImagePreviewModel)
    fun notifyItemChanged()
    fun showImageEditView()
    fun openImageShare(image: String?)
    fun openFileCreateDocument(url: String?)
    fun saveImageIntoLocation(directoryUrl: Uri?, localFilePath: String?)

    enum class ViewType(val type: Int) {
        IMAGE_CONTENT(1),
        IMAGE_PREVIEW(2);
    }

    companion object {
        const val STORAGE_STORAGE_REQUEST_CODE: Int = 1010
        const val IMAGE_CAPTURE_REQUEST_CODE: Int = 1000
        const val REQ_CREATE_DOCUMENT = 1001
    }
}

class UploadImageActivity : BaseActivity(), UploadImageView {

    @Inject
    lateinit var messagingService: MessagingService

    @Inject
    lateinit var presenter: UploadImagePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_image)
        GalleryApplication.getAppComponent().inject(this)
        imageUploadView.adapter = initImageUploadAdapter()
    }

    override fun onStart() {
        super.onStart()
        presenter.takeView(this)
        presenter.initData()
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

    override fun openImageShare(image: String?) {
        val contextPool = CoroutineContextProvider()
        CoroutineScope(contextPool.IO).launch {
            image?.let { imageUrl->
                val imagePath = FileHelper.getImgCachePath(applicationContext, imageUrl)
                withContext(contextPool.Main) {
                    imagePath?.let { share(it) }
                }
            }
        }
    }

    private fun share(result: Uri) {
        var intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "image/jpeg"
            putExtra(Intent.EXTRA_TITLE, "Testing")
            putExtra(Intent.EXTRA_TEXT, "testing text")
            putExtra(MediaStore.EXTRA_OUTPUT, result)
            putExtra(Intent.EXTRA_STREAM, result)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Share image"))
    }

    override fun openFileCreateDocument(url: String?) {
        url?.let {
            val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                putExtra(Intent.EXTRA_TITLE, getDownloadFileName())
                type = "image/*"//DocumentsContract.Document.
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            }
            startActivityForResult(intent, REQ_CREATE_DOCUMENT)
        }
    }

    override fun showImageEditView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
                        var isSuccess: String? = null
                        var contextPool = CoroutineContextProvider()
                        CoroutineScope(contextPool.IO).launch {
                                isSuccess = FileHelper.getImagePath(applicationContext, it)
                            }.invokeOnCompletion {
                            CoroutineScope(contextPool.Main).launch {
                                isSuccess?.let { presenter.uploadFile(it) }
                            }
                        }

                    }
                }

                REQ_CREATE_DOCUMENT -> {
                    data?.data.let {
                        presenter.saveImageIntoLocation(it)
                    }
                }
            }
        }
    }

    override fun saveImageIntoLocation(directoryUrl: Uri?, localFilePath: String?) {
        FileHelper.downloadImage(applicationContext, directoryUrl, localFilePath)
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
                        imagePreview.setOnClickListener {
                            (getTag(R.id.VIEW_TAG_POSITION) as? Int)?.let { position ->
                                presenter.onClickImage(position)
                            }
                        }
                        setOnEditClickListener(View.OnClickListener {
                            (getTag(R.id.VIEW_TAG_POSITION) as? Int)?.let { position ->
                                it.showDefaultPopupMenu(PopupMenu.OnMenuItemClickListener { item ->
                                    when (item?.itemId) {
                                        R.id.delete -> presenter.onClickImageDelete()
                                        R.id.share -> presenter.onClickShareImage()
                                        R.id.download -> presenter.onClickDownloadImage()
                                    }
                                    true
                                })
                            }
                        })

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

    override fun onResume() {
        super.onResume()
        val contextPool = CoroutineContextProvider()
        CoroutineScope(contextPool.IO).launch {
            messagingService.onNewToken("testing")
        }.invokeOnCompletion {
            CoroutineScope(contextPool.Main).launch {
            }
        }
    }
}