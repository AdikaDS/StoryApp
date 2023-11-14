package com.adika.storyapp.view.recyclerview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.RecyclerView
import com.adika.storyapp.data.remote.response.ListStoryItem
import com.adika.storyapp.databinding.ItemStoryBinding
import com.adika.storyapp.view.detail.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter(private var listUser: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<ListStoryItem>) {
        listUser = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val user = listUser[position]
        holder.bind(user, holder.itemView.context)
    }

    override fun getItemCount(): Int = listUser.size

    class ListViewHolder(var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: ListStoryItem, context: Context) {
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


}