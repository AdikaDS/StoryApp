package com.adika.storyapp.view.adapter

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.adika.storyapp.data.remote.response.ListStoryItem
import com.adika.storyapp.databinding.ItemStoryBinding
import com.adika.storyapp.view.detail.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.ListViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class ListViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem) {
            binding.apply {
                tvItemUsername.text = story.name
                tvItemDetail.text = story.description
                Glide.with(root.context)
                    .load(story.photoUrl)
                    .into(imgItemPhoto)

            }


            binding.root.setOnClickListener {
                val intentDetail = Intent(binding.root.context, DetailStoryActivity::class.java)
                val transitionName = "storyTransition"
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    binding.root.context as Activity,
                    Pair(binding.imgItemPhoto, transitionName),
                    Pair(binding.tvItemUsername, transitionName),
                    Pair(binding.tvItemDetail, transitionName)
                ).toBundle()

                intentDetail.putExtra(DetailStoryActivity.EXTRA_ID, story.id)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_TRANSITION_NAME, transitionName)
                binding.root.context.startActivity(intentDetail, options)
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }


}