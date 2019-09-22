package com.example.flickerimagegallery.data.entities

import com.squareup.moshi.Json

data class PublicFeed(
    @field:Json(name = "description") val description: String,
    @field:Json(name = "generator") val generator: String,
    @field:Json(name = "items") val items: List<Item>,
    @field:Json(name = "link") val link: String,
    @field:Json(name = "modified") val modified: String,
    @field:Json(name = "title") val title: String
)

data class Item(
    @field:Json(name = "author") val author: String,
    @field:Json(name = "author_id") val author_id: String,
    @field:Json(name = "date_taken") val date_taken: String,
    @field:Json(name = "description") val description: String,
    @field:Json(name = "link") val link: String,
    @field:Json(name = "media") val media: Media,
    @field:Json(name = "published") val published: String,
    @field:Json(name = "tags") val tags: String,
    @field:Json(name = "title") val title: String
)

data class Media(
    val m: String
)