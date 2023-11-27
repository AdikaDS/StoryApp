package com.adika.storyapp.view.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.adika.storyapp.R
import com.adika.storyapp.data.local.pref.UserPreference
import com.adika.storyapp.data.local.pref.dataStore
import com.adika.storyapp.databinding.ActivityMainBinding
import com.adika.storyapp.view.StoryModelFactory
import com.adika.storyapp.view.addstory.AddStoryActivity
import com.adika.storyapp.view.recyclerview.StoryAdapter
import com.adika.storyapp.view.welcome.WelcomeActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModels<MainViewModel> {
        StoryModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter

    private val handler = Handler()
    private val refreshRunnable = object : Runnable {
        override fun run() {
            viewModel.getStory()

            handler.postDelayed(this, 10000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddStoryActivity::class.java))
        }

        storyAdapter = StoryAdapter(emptyList())
        showRecyclerList()

        viewModel.listStory.observe(this) { listStory ->
            storyAdapter.updateData(listStory)
        }

        viewModel.loading.observe(this) { loading ->
            showLoading(loading)
        }

        handler.post(refreshRunnable)

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStop(owner: LifecycleOwner) {
                handler.removeCallbacks(refreshRunnable)
            }
        })

        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onStart(owner: LifecycleOwner) {
                handler.post(refreshRunnable)
            }
        })


        viewModel.getStory()
    }

    private fun showRecyclerList() {
        val layoutManager = LinearLayoutManager(this)
        binding.rvStory.layoutManager = layoutManager
        binding.rvStory.adapter = storyAdapter
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)
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

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        // Hentikan auto-refresh saat aplikasi dihancurkan
        handler.removeCallbacks(refreshRunnable)
    }

}