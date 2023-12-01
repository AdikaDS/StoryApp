package com.adika.storyapp.view.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import com.adika.storyapp.DataDummy
import com.adika.storyapp.data.local.repo.StoryRepository
import com.adika.storyapp.data.remote.response.ListStoryItem
import com.adika.storyapp.getOrAwaitValue
import com.adika.storyapp.view.adapter.StoryAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }

    @Mock
    private lateinit var repository: StoryRepository

    @Test
    fun `when get Story Should Not Null and Return Story`() = runBlocking {
        val storyItems = DataDummy.generateDummyStoryResponse()
        val story: PagingData<ListStoryItem> = PagingData.from(storyItems)
        val dataStory = MutableLiveData<PagingData<ListStoryItem>>()
        dataStory.value = story

        Mockito.`when`(repository.getStory()).thenReturn(dataStory)
        val viewModel = MainViewModel(repository)
        val actualStory: PagingData<ListStoryItem> = viewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        val snapshot = differ.snapshot()
        Assert.assertNotNull(snapshot)

        val expectedSize = storyItems.size
        Assert.assertEquals(expectedSize, snapshot.size)

        Assert.assertEquals(storyItems[0], snapshot[0])
    }

    @Test
    fun `when no Story data Should Return Empty PagingData`() = runBlocking {
        val emptyStory: PagingData<ListStoryItem> = PagingData.empty()
        val emptyDataStory = MutableLiveData<PagingData<ListStoryItem>>()
        emptyDataStory.value = emptyStory

        Mockito.`when`(repository.getStory()).thenReturn(emptyDataStory)
        val viewModel = MainViewModel(repository)
        val actualStory: PagingData<ListStoryItem> = viewModel.story.getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = Dispatchers.Main,
        )
        differ.submitData(actualStory)

        val snapshot = differ.snapshot()
        Assert.assertNotNull(snapshot)
        Assert.assertEquals(0, snapshot.size)
    }
}

val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}
