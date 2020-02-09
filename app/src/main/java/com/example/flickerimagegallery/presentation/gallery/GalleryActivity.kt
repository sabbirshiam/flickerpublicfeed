package com.example.flickerimagegallery.presentation.gallery

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryContentView
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter.*
import kotlinx.android.synthetic.main.activity_gallery.*
import com.example.flickerimagegallery.domain.usecases.UploadFile
import com.example.flickerimagegallery.presentation.gallery.GalleryView.Companion.IMAGE_CAPTURE_REQUEST_CODE
import com.example.flickerimagegallery.presentation.gallery.GalleryView.Companion.STORAGE_STORAGE_REQUEST_CODE
import com.example.flickerimagegallery.utils.FileHelper

interface GalleryView {
    fun notifyDataSetChanged()
    fun onBindGalleryItemViewHolder(holder: GalleryItemViewHolder, model: Item)
    fun openInBrowser(model: Item)
    fun openImageChooser()
    fun clearCacheFiles()

    companion object {
        const val STORAGE_STORAGE_REQUEST_CODE: Int  = 1010
        const val IMAGE_CAPTURE_REQUEST_CODE: Int = 1000
    }

}

class GalleryActivity : AppCompatActivity(), GalleryView {
    private lateinit var presenter: GalleryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        presenter = GalleryPresenterImpl(
            UploadFile(applicationContext),
            GetFlickerPublicInfo(applicationContext)
        )
        galleryView.layoutManager = GridLayoutManager(this, 2)
        galleryView.adapter = initGalleryAdapter()
        floatingActionButton.setOnClickListener { presenter.onReloadClick() }
    }

    override fun onStart() {
        super.onStart()
        presenter.takeView(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.dropView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sortByPublished -> presenter.onClickSortByPublished()
            R.id.sortByDateTaken -> presenter.onClickSortByDateTaken()
            R.id.uploadImage -> presenter.onClickUploadImage()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun openInBrowser(model: Item) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(model.media.image))

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun notifyDataSetChanged() {
        galleryView.adapter?.notifyDataSetChanged()
    }

    override fun onBindGalleryItemViewHolder(holder: GalleryItemViewHolder, model: Item) {
        holder.getView()?.bindGalleryItem(model)
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

    private fun initGalleryAdapter(): GalleryItemsAdapter =
        GalleryItemsAdapter(
            contentManager = object : ContentManager {
                override fun getItemCount(): Int = presenter.getGalleryItemCount()
            },
            contentViewCreator = object : ContentViewCreator {
                override fun createGalleryItemView(context: Context): GalleryContentView {
                    return GalleryContentView(context).apply {
                        setOnClickListener {
                            (getTag(R.id.VIEW_TAG_POSITION) as? Int)?.let { position ->
                                presenter.onClickImage(position)
                            }
                        }

                    }
                }

            },
            bindListener = object : OnBindListener {
                override fun onBindGalleryItemView(
                    holder: GalleryItemViewHolder,
                    position: Int
                ) {
                    holder.getView()?.setTag(R.id.VIEW_TAG_POSITION, holder.adapterPosition)
                    presenter.onBindGalleryItemView(holder, position)
                }

            })
}
