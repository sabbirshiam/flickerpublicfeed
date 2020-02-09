package com.example.flickerimagegallery.domain.models

abstract class DataModel

data class ImageContentModel(var name: String) : DataModel()
data class ImagePreviewModel(var name: String, var image: String?) : DataModel()