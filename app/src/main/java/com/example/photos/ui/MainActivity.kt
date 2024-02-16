package com.example.photos.ui

import android.app.SearchManager
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.cursoradapter.widget.SimpleCursorAdapter
import com.bumptech.glide.Glide
import com.example.photos.R
import com.example.photos.databinding.ActivityMainBinding
import com.example.photos.model.PhotosListItem
import com.example.photos.model.PlaceHolderJsonApi

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val photosList: MutableList<PhotosListItem> = mutableListOf()
    private var cursorAdapter: SimpleCursorAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupToolBar()
        setupSearchView()
        fetchPhotos()
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
                newText?.let {
                    filterPhotos(it)
                }
                return true
            }
        })

        svPhoto.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = cursorAdapter?.cursor
                if (cursor != null && cursor.moveToPosition(position)) {
                    val photoUrlIndex = cursor.getColumnIndex("photoUrl")
                    val thumbnailUrlIndex = cursor.getColumnIndex("thumbnailUrl")

                    verifyIndexAndLoadImages(thumbnailUrlIndex, photoUrlIndex, cursor)
                }
                return true
            }
        })
    }

    private fun filterPhotos(query: String) {
        val filteredPhotos = photosList.filter { it.title.contains(query, ignoreCase = true) }
        val cursor = MatrixCursor(
            arrayOf(
                BaseColumns._ID,
                SearchManager.SUGGEST_COLUMN_TEXT_1,
                "photoUrl",
                "thumbnailUrl"
            )
        )
        filteredPhotos.forEachIndexed { index, photo ->
            cursor.addRow(arrayOf(index, photo.title, photo.url, photo.thumbnailUrl))
        }
        cursorAdapter?.changeCursor(cursor)
    }

    private fun fetchPhotos() {
        val photosRequest = PlaceHolderJsonApi.PhotosListRequest(
            { response ->
                photosList.clear()
                photosList.addAll(response)
            },
            {
                Toast.makeText(this, getString(R.string.request_problem), Toast.LENGTH_SHORT).show()
            }
        )
        PlaceHolderJsonApi.getInstance(this).addToRequestQueue(photosRequest)
    }

    private fun verifyIndexAndLoadImages(
        thumbnailUrlIndex: Int,
        photoUrlIndex: Int,
        cursor: Cursor
    ) {
        if (thumbnailUrlIndex >= 0 && photoUrlIndex >= 0) {
            val thumbnailUrl = cursor.getString(thumbnailUrlIndex)
            val photoUrl = cursor.getString(photoUrlIndex)

            binding.ivThumbnail.isVisible = true
            binding.ivPhoto.isVisible = true
            Glide.with(this@MainActivity)
                .load(thumbnailUrl)
                .into(binding.ivThumbnail)

            Glide.with(this@MainActivity)
                .load(photoUrl)
                .into(binding.ivPhoto)
        } else {
            Toast.makeText(
                this@MainActivity,
                getString(R.string.image_error),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
