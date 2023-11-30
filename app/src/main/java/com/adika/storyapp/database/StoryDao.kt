package com.adika.storyapp.database

import androidx.paging.PagingSource
import androidx.room.*
import com.adika.storyapp.data.remote.response.ListStoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ListStoryItem>)

    @Query("SELECT * FROM liststoryitemroom")
    fun getAllStory(): PagingSource<Int, ListStoryItem>

    @Query("DELETE FROM liststoryitemroom")
    suspend fun deleteAll()
}
