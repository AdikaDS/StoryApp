package com.adika.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.adika.storyapp.R
import com.adika.storyapp.data.local.pref.UserPreference
import com.adika.storyapp.data.local.pref.dataStore
import com.adika.storyapp.databinding.ActivityMainBinding
import com.adika.storyapp.view.StoryModelFactory
import com.adika.storyapp.view.adapter.LoadingStateAdapter
import com.adika.storyapp.view.adapter.StoryAdapter
import com.adika.storyapp.view.addstory.AddStoryActivity
import com.adika.storyapp.view.maps.MapsActivity
import com.adika.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        StoryModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener {
            startActivityForResult(Intent(this, AddStoryActivity::class.java), ADD_STORY_REQUEST_CODE)
        }

        showRecyclerList()
        getStory()

        viewModel.loading.observe(this) { loading ->
            showLoading(loading)
        }

        lifecycleScope.launch {
            while (isActive) {
                delay(10000)
                getStory()
            }
        }
    }

    private fun getStory() {
        val adapter = StoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )
        viewModel.story.observe(this) {
            adapter.submitData(lifecycle, it)
        }
    }

    private fun showRecyclerList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val userPreference = UserPreference.getInstance(dataStore)
        return when (item.itemId) {
            R.id.logout -> {
                lifecycleScope.launch {
                    userPreference.logout()
                    startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
                    finish()
                }
                true
            }

            R.id.maps -> {
                startActivity(Intent(this@MainActivity, MapsActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_STORY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Cerita berhasil ditambahkan", Toast.LENGTH_SHORT).show()
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Gagal menambahkan cerita", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan auto-refresh saat aplikasi dihancurkan
        lifecycleScope.launch {
            cancel()
        }
    }

    companion object {
        private const val ADD_STORY_REQUEST_CODE = 123
    }

}