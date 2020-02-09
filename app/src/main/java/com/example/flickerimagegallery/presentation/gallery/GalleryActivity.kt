package com.example.flickerimagegallery.presentation.gallery

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryContentView
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter.*
import com.example.flickerimagegallery.presentation.uploadimage.UploadImageActivity
import kotlinx.android.synthetic.main.activity_gallery.*

interface GalleryView {
    fun notifyDataSetChanged()
    fun onBindGalleryItemViewHolder(holder: GalleryItemViewHolder, model: Item)
    fun openInBrowser(model: Item)
    fun navigateToUploadImage()
}

class GalleryActivity : AppCompatActivity(), GalleryView {
    private lateinit var presenter: GalleryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        presenter = GalleryPresenterImpl(GetFlickerPublicInfo(applicationContext))
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

    override fun navigateToUploadImage() {
        val intent = Intent(this, UploadImageActivity::class.java)
        startActivity(intent)
    }

    override fun notifyDataSetChanged() {
        galleryView.adapter?.notifyDataSetChanged()
    }

    override fun onBindGalleryItemViewHolder(holder: GalleryItemViewHolder, model: Item) {
        holder.getView()?.bindGalleryItem(model)
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
