package com.example.flickerimagegallery.domain.usecases

import android.content.Context
import android.util.Log
import com.example.flickerimagegallery.data.entities.PublicFeed
import com.example.flickerimagegallery.data.repositories.FlickerFeedRepository
import retrofit2.Response

/**
 * Flicker Public info related data handling class.
 */
class GetFlickerPublicInfo(private val context: Context) {

    private val flickerFeedRepository: FlickerFeedRepository = FlickerFeedRepository(context)

    suspend fun getPublicPhotos(): ArrayList<String> {
        val publicFeeds: Response<PublicFeed> = flickerFeedRepository.getPublicFeeds()
        return if(publicFeeds.isSuccessful) {
            publicFeeds.body()?.items?.let {
                ArrayList(it.map { item ->
                    Log.i("RESPONSE", item.media.image)
                    item.media.image
                })
            } ?: arrayListOf()

        } else {
            arrayListOf()
        }
    }
}