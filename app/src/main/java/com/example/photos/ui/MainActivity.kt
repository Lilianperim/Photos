package com.example.photos.ui

import android.app.SearchManager
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.SimpleCursorAdapter
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
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1) // Coluna do cursor a ser mostrada
        val to = intArrayOf(android.R.id.text1) // ID do TextView que vai mostrar os dados

        cursorAdapter = SimpleCursorAdapter(
            this@MainActivity,
            android.R.layout.simple_list_item_1, // Layout padrão para exibir cada sugestão
            null, // Cursor inicial é nulo
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
    }

    private fun filterPhotos(query: String) {
        val filteredPhotos = photosList.filter { it.title.contains(query, ignoreCase = true) }

        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
        filteredPhotos.forEachIndexed { index, photo ->
            cursor.addRow(arrayOf(index, photo.title))
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
}
