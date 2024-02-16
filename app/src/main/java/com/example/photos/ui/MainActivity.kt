package com.example.photos.ui

import android.app.SearchManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.example.photos.R
import com.example.photos.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var cursorAdapter: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolBar()
        setupSearchView()
    }

    private fun setupToolBar() {
        setSupportActionBar(binding.toolBar.apply {
            title = getString(R.string.app_name)
        })
    }

    private fun setupSearchView() = with(binding) {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(android.R.id.text1)

        cursorAdapter = SimpleCursorAdapter(
            this@MainActivity,
            android.R.layout.simple_list_item_1,
            null,
            from,
            to,
            SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        svPhoto.suggestionsAdapter = cursorAdapter

        svPhoto.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}
