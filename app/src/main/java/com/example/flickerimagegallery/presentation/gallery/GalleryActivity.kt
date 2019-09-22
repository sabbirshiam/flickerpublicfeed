package com.example.flickerimagegallery.presentation.gallery

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.example.flickerimagegallery.R
import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryContentView
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter
import com.example.flickerimagegallery.presentation.gallery.viewLists.GalleryItemsAdapter.*
import kotlinx.android.synthetic.main.activity_gallery.*

interface GalleryView {
    fun notifyDataSetChanged()
    fun onBindGalleryItemViewHolder(holder: GalleryItemViewHolder, model: Item)

}

class GalleryActivity : AppCompatActivity(), GalleryView {
    private lateinit var presenter: GalleryPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        presenter = GalleryPresenterImpl(GetFlickerPublicInfo(applicationContext))
        galleryView.layoutManager = GridLayoutManager(this, 3)
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
                override fun createGalleryItemView(context: Context): GalleryContentView =
                    GalleryContentView(context)
            },
            bindListener = object : OnBindListener {
                override fun onBindGalleryItemView(
                    holder: GalleryItemViewHolder,
                    position: Int
                ) = presenter.onBindGalleryItemView(holder, position)

            })
}
