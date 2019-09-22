package com.example.flickerimagegallery.domain.usecases

import android.content.Context
import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.data.entities.PublicFeed
import com.example.flickerimagegallery.data.repositories.FlickerFeedRepository
import retrofit2.Response

/**
 * Flicker Public info related data handling class.
 */
class GetFlickerPublicInfo(private val context: Context) {

    private val flickerFeedRepository: FlickerFeedRepository = FlickerFeedRepository(context)

    suspend fun getPublicPhotos(): ArrayList<Item> {
        val publicFeeds: Response<PublicFeed> = flickerFeedRepository.getPublicFeeds()
        return if(publicFeeds.isSuccessful) {
            publicFeeds.body()?.items?.let {
                ArrayList(it)
            } ?: arrayListOf()

        } else {
            arrayListOf()
        }
    }
}