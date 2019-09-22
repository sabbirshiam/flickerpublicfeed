package com.example.flickerimagegallery.domain.usecases

import com.example.flickerimagegallery.data.repositories.FlickerFeedRepository

class GetFlickerPublicInfo {
    private val flickerFeedRepository:FlickerFeedRepository = FlickerFeedRepository()
    fun getPublicPhotos(): ArrayList<String> {
        //val response = get()
        flickerFeedRepository.getPublicPhotos()
        return arrayListOf()
    }
}