package com.example.flickerimagegallery.data.repositories

import android.content.Context
import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.data.entities.PublicFeed
import retrofit2.Response

class FlickerFeedRepository(private val context: Context) {
    private val application: GalleryApplication = context as GalleryApplication

    /**
     * getPublicFeeds() return list of public feed items from
     * https://www.flickr.com/services/feeds/photos_public.gne?format=json&nojsoncallback=1
     */
    suspend fun getPublicFeeds(): Response<PublicFeed> {
        return application.getRetrofitIntance().flickerService.getPublicFeed("json", "1")
    }
}