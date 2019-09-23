package com.example.flickerimagegallery.mockdatas

import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.data.entities.Media
import com.example.flickerimagegallery.data.entities.PublicFeed
import java.util.*

fun generateRandomMedia(): Media {
    return Media(Random().nextInt().toString())
}

fun generateRandomItem(): Item {
    val random = Random()
    return Item(
        random.nextInt().toString(),
        random.nextInt().toString(),
        random.nextInt().toString(),
        random.nextInt().toString(),
        random.nextInt().toString(),
        generateRandomMedia(),
        random.nextInt().toString(),
        random.nextInt().toString(),
        random.nextInt().toString()
    )
}

fun generateRandomPublicFeeds(items: List<Item>): PublicFeed {
    val random = Random()

    return PublicFeed(
        random.nextInt().toString(),
        random.nextInt().toString(),
        items,
        random.nextInt().toString(),
        random.nextInt().toString(),
        random.nextInt().toString()
    )
}