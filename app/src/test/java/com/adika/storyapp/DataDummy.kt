package com.adika.storyapp

import com.adika.storyapp.data.remote.response.ListStoryItem

object DataDummy {
    fun generateDummyStoryResponse(): List<ListStoryItem> {
        val story: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..10) {
            val stories = ListStoryItem(
                "https://fox59.com/wp-content/uploads/sites/21/2022/05/JOHN-DOE-CLAY.jpg",
                "Adika DS",
                "Ini adalah sebuah food",
                11.0000000f.toDouble(),
                i.toString(),
                -11.0000000f.toDouble(),
            )
            story.add(stories)
        }
        return story
    }
}