package com.example.flickerimagegallery.data.repositories

import com.example.flickerimagegallery.BuildConfig
import com.example.flickerimagegallery.data.entities.PublicFeed
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickerService {

    /**
     * getPublicFeed(format: String, rawJson: String)
     *
     * parameter: format
     * The format of the feed
     * accepted formats :
     *  csv, json, sql, yaml,
     * rss_200 or rss2, atom_1 or atom, rss_091, rss_092 or rss, rss_100 or rdf, rss_200_enc
     *
     * parameter: rawJson
     * nojsoncallback= 1 is query to return only raw json .eg { "title": "title"}
     * nojsoncallback= 0 , eg. jsonFlickrFeed({"title": "Uploads from everyone" }
     *
     */
    @GET(BuildConfig.API_PREFIX.plus("services/feeds/photos_public.gne"))
    suspend fun getPublicFeed(@Query("format") format: String,
                      @Query("nojsoncallback") rawJson: String): Response<PublicFeed>
}