package com.example.flickerimagegallery.data.repositories

import android.util.Log
import com.example.flickerimagegallery.data.RetrofitClientInstance
import com.example.flickerimagegallery.data.entities.PublicFeed
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FlickerFeedRepository {

    fun getPublicPhotos() {
        RetrofitClientInstance.getInstance().flickerService.getPublicFeedPhotos("json", "1")
            .enqueue(object : Callback<PublicFeed> {
                override fun onResponse(call: Call<PublicFeed>, response: Response<PublicFeed>) {
                    //data.value = response.body()
                    Log.i("RESPONSE", response.body().toString())
                }

                // Error case is left out for brevity.
                override fun onFailure(call: Call<PublicFeed>, t: Throwable) {
                    Log.e("ERROR", "" + t.printStackTrace())
                }
            })
    }
}