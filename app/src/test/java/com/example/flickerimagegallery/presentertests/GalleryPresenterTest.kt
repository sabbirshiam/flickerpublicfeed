package com.example.flickerimagegallery.presentertests

import com.example.flickerimagegallery.GalleryApplication
import com.example.flickerimagegallery.data.entities.Item
import com.example.flickerimagegallery.data.entities.PublicFeed
import com.example.flickerimagegallery.data.repositories.FlickerFeedRepository
import com.example.flickerimagegallery.domain.usecases.GetFlickerPublicInfo
import com.example.flickerimagegallery.mockdatas.generateRandomItem
import com.example.flickerimagegallery.mockdatas.generateRandomPublicFeeds
import com.example.flickerimagegallery.presentation.gallery.GalleryPresenterImpl
import com.example.flickerimagegallery.presentation.gallery.GalleryView
import com.example.flickerimagegallery.utils.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Unconfined
import kotlinx.coroutines.launch
import org.junit.*
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

class GalleryPresenterTest {

    @get:Rule
    var mockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var galleryView: GalleryView
    var context: GalleryApplication = GalleryApplication()
    var getFlickerPublicInfo: GetFlickerPublicInfo = GetFlickerPublicInfo(context)

    private lateinit var galleryPresenter: GalleryPresenterImpl
    private val item1 = generateRandomItem()
    private val item2 = generateRandomItem()
    private val content = ArrayList<Item>()
    private lateinit var publicFeeds: PublicFeed
    private var testContextProvider= TestContextProvider()

    @Before
    fun setUp() {
        content.add(item1)
        content.add(item2)
        publicFeeds = generateRandomPublicFeeds(content)
        galleryPresenter = GalleryPresenterImpl(getFlickerPublicInfo, testContextProvider)
        galleryPresenter.takeView(galleryView)
    }

    @After
    fun end() {
        galleryPresenter.dropView()
    }

    @Test
    fun testOnClickImage() {
        CoroutineScope(testContextProvider.IO).launch {
            Mockito.`when`(FlickerFeedRepository(context).getPublicFeeds())
                .thenReturn(Response.success(publicFeeds))
            galleryPresenter.fetchGalleryItems()
            galleryPresenter.onClickImage(0)
            verify(galleryView).openInBrowser(item1)
        }

    }

    @Test
    fun testFetchGalleryItems() {
        CoroutineScope(testContextProvider.IO).launch {
            Mockito.`when`(FlickerFeedRepository(context).getPublicFeeds())
                .thenReturn(Response.success(publicFeeds))
            Mockito.`when`(getFlickerPublicInfo.getPublicPhotos())
                .thenReturn(content)
            galleryPresenter.fetchGalleryItems()
            verify(galleryView).notifyDataSetChanged()
        }
    }
}

    class TestContextProvider : CoroutineContextProvider() {
        override val Main: CoroutineContext = Unconfined
        override val IO: CoroutineContext = Unconfined
    }

