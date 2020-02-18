package com.example.flickerimagegallery.data.entities

import com.squareup.moshi.Json

data class FileUploadResponse(
    @field:Json(name = "data") var data: Data,
    @field:Json(name = "message") var message: String,
    @field:Json(name = "status") var status: Boolean
)

data class Data(
    @field:Json(name = "mimetype") var mimetype: String,
    @field:Json(name = "name") var name: String,
    @field:Json(name = "size") var size: String,
    @field:Json(name = "url") var url: String
)