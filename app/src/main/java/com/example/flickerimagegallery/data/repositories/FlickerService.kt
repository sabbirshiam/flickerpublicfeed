package com.example.flickerimagegallery.data.repositories

import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.entities.PublicFeed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickerService {

    @GET(BuildConfig.API_PREFIX.plus("services/feeds/photos_public.gne"))
    fun getPublicFeed(@Query("format") format: String,
                      @Query("nojsoncallback") rawJson: String): Call<PublicFeed>
}