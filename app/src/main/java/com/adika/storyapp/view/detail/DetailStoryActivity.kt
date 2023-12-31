package com.adika.storyapp.view.detail

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.adika.storyapp.databinding.ActivityDetailStoryBinding
import com.adika.storyapp.utils.withDateFormat
import com.adika.storyapp.view.StoryModelFactory
import com.bumptech.glide.Glide

class DetailStoryActivity : AppCompatActivity() {
    val viewModel by viewModels<DetailViewModel> {
        StoryModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getDetailStory()

        transitionActivity()

    }

    private fun transitionActivity() {
        val transitionName = intent.getStringExtra(EXTRA_TRANSITION_NAME)
        ViewCompat.setTransitionName(binding.imageDetailStory, transitionName)
        ViewCompat.setTransitionName(binding.tvName, transitionName)
        ViewCompat.setTransitionName(binding.tvDetail, transitionName)
    }

    private fun getDetailStory() {
        val id = intent.getStringExtra(EXTRA_ID)
        if (id != null) {
            viewModel.getDetailStory(id)
            viewModel.detailStory.observe(this) { story ->
                binding.apply {
                    tvCreatedAt.text = story.createdAt.withDateFormat()
                    tvName.text = story.name
                    tvDetail.text = story.description
                    Glide.with(root.context)
                        .load(story.photoUrl)
                        .into(imageDetailStory)
                }
            }

            viewModel.loading.observe(this) { loading ->
                showLoading(loading)
            }
        }

        viewModel.status.observe(this) { isSucess ->
            if (isSucess) {
                Toast.makeText(this, "Detail Story berhasil dimuat", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.error.observe(this) { errorMessage ->
                    if (errorMessage != null) {
                        Toast.makeText(this, "$errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }


    companion object {
        const val EXTRA_ID = "extra_user_id"
        const val EXTRA_TRANSITION_NAME = "extra_transition_name"
    }
}