package com.example.flickerimagegallery.data.repositories

import android.util.Log
import com.example.flickerimagegallery.data.RetrofitClientInstance
import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.data.entities.PublicFeed
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlickerFeedRepository {

    fun getPublicPhotos(): List<Item> {
        var items = arrayListOf<Item>()
        RetrofitClientInstance.getInstance().flickerService.getPublicFeed("json", "1")
            .enqueue(object : Callback<PublicFeed> {
                override fun onResponse(call: Call<PublicFeed>, response: Response<PublicFeed>) {
                    response.body()?.let { feeds ->
                        items = ArrayList(feeds.items)
                        items.map { item ->
                            item.media.image
                            Log.i("RESPONSE", item.media.image)
                        }
                    }
                }

                override fun onFailure(call: Call<PublicFeed>, t: Throwable) {
                    Log.e("ERROR", "" + t.printStackTrace())
                }
            })

        return items
    }
}