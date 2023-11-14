package com.adika.storyapp.view.recyclerview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adika.storyapp.data.remote.response.ListStoryItem
import com.adika.storyapp.databinding.ItemStoryBinding
import com.adika.storyapp.view.detail.DetailStoryActivity
import com.bumptech.glide.Glide

class StoryAdapter(private var listUser: List<ListStoryItem>) :
    RecyclerView.Adapter<StoryAdapter.ListViewHolder>() {

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
                intentDetail.putExtra(DetailStoryActivity.EXTRA_ID, story.id)
                binding.root.context.startActivity(intentDetail)
            }
        }
    }


}