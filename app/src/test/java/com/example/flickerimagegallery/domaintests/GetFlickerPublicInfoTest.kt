package com.example.flickerimagegallery.domaintests

import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.data.repositories.FlickerFeedRepository
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import com.example.flickerimagegallery.mockdatas.generateRandomItem
import com.example.flickerimagegallery.mockdatas.generateRandomPublicFeeds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnit
import retrofit2.Response

class GetFlickerPublicInfoTest {

    @get:Rule
    var mockitoRule = MockitoJUnit.rule()


    var context: GalleryApplication = GalleryApplication()
    var getFlickerPublicInfo: GetFlickerPublicInfo = GetFlickerPublicInfo(context)


    /**
     * Response onSuccess test
     *
     * test criteria:
     * provided listof items.
     * assertion equality check from response with provided values.
     */
    @Test
    fun getPublicPhotosTest() {
        val item1 = generateRandomItem()
        val item2 = generateRandomItem()

        val content = ArrayList<Item>()
        content.add(item1)
        content.add(item2)
        val publicFeeds = generateRandomPublicFeeds(content)
        CoroutineScope(Dispatchers.IO).launch {
            Mockito.`when`(FlickerFeedRepository(context).getPublicFeeds())
                .thenReturn(Response.success(publicFeeds))
            val response = getFlickerPublicInfo.getPublicPhotos()
            Assert.assertEquals(content, response)
        }
    }
}