package com.example.flickerimagegallery.presentation.uploadimage

import android.net.Uri
import com.example.flickerimagegallery.domain.models.DataModel
import com.example.flickerimagegallery.domain.models.ImageContentModel
import com.example.flickerimagegallery.domain.models.ImagePreviewModel
import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.example.flickerimagegallery.presentation.uploadimage.viewlists.ImageUploadAdapter.*
import com.example.flickerimagegallery.utils.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface UploadImagePresenter {
    fun takeView(view: UploadImageView)
    fun dropView()
    fun onClickImage(position: Int)
    fun onClickUploadImage()
    fun uploadFile(filePath: String)
    fun getItemCount(): Int
    fun getItemViewType(position: Int): Int
    fun onBindContentView(holder: ImageContentItemViewHolder, position: Int)
    fun onBindImagePreviewView(holder: ImagePreviewItemViewHolder, position: Int)
    fun onClickImageDelete()
    fun onClickShareImage()
    fun onClickDownloadImage()
    fun saveImageIntoLocation(directoryUrl: Uri?)
}

class UploadImagePresenterImpl(
    private val uploadFile: UploadFile,
    private val contextPool: CoroutineContextProvider
    = CoroutineContextProvider()
) : UploadImagePresenter {

    private var view: UploadImageView? = null

    private var dataList = mutableListOf<DataModel>()

    override fun takeView(view: UploadImageView) {
        this.view = view
        initData()
    }

    private fun initData() {
        dataList.add(ImageContentModel("First Text"))
        dataList.add(ImageContentModel("Second Text"))
        dataList.add(ImagePreviewModel("Second Text", "https://scontent-nrt1-1.xx.fbcdn.net/v/t1.0-9/86390776_10221959335002078_6512294456627036160_o.jpg?_nc_cat=105&_nc_ohc=4W0jrMaYgxwAX8rdjCi&_nc_ht=scontent-nrt1-1.xx&oh=305e53738d680ea85f559e3dcb587256&oe=5EC94881"))
    }

    override fun dropView() {
        view = null
    }

    override fun getItemCount(): Int = 3

    override fun getItemViewType(position: Int): Int =
        if (position == 2) {
            UploadImageView.ViewType.IMAGE_PREVIEW.type
        } else
            UploadImageView.ViewType.IMAGE_CONTENT.type

    override fun onBindContentView(holder: ImageContentItemViewHolder, position: Int) {
        view?.onBindContentViewHolder(holder, dataList[position] as ImageContentModel)

    }

    override fun onBindImagePreviewView(holder: ImagePreviewItemViewHolder, position: Int) {
        view?.onBindPreviewViewHolder(holder, dataList[position] as ImagePreviewModel)
    }

    override fun onClickImage(position: Int) {
        (dataList[position] as? ImagePreviewModel)?.let {
            view?.openImageChooser() //it.image?.let { navigateToImagePreview() } ?: view?.openImageChooser()
        }
    }

    private fun navigateToImagePreview() {
        // TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onClickUploadImage() {
        view?.openImageChooser()
    }

    override fun onClickImageDelete() {
        setImage(null)
        view?.clearCacheFiles()
    }

    override fun onClickShareImage() {
        view?.openImageShare(dataList.filterIsInstance<ImagePreviewModel>().first().image)
    }

    override fun uploadFile(filePath: String) {
        setImage(filePath)

        CoroutineScope(contextPool.IO).launch {
            val isSuccess = uploadFile.uploadFile(filePath)
            withContext(contextPool.Main) {
            }
        }
    }

    private fun setImage(path: String?) {
        dataList.filterIsInstance<ImagePreviewModel>().first().apply {
            this.image = path
        }
        view?.notifyItemChanged()
    }

    override fun onClickDownloadImage() {
        view?.openFileCreateDocument(dataList.filterIsInstance<ImagePreviewModel>().first().image)
    }

    override fun saveImageIntoLocation(directoryUrl: Uri?) {
        var localFilePath = dataList.filterIsInstance<ImagePreviewModel>().first().image
        CoroutineScope(contextPool.IO).launch {
            view?.saveImageIntoLocation(directoryUrl, localFilePath)
            withContext(contextPool.Main) {
            }
        }

    }
}