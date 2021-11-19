package com.example.libreria

import ENTITIES.Libro
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.libreria.databinding.ActivityMainBinding
import REPOSITORY.LibroRepository
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity()  {
    private lateinit var binding: ActivityMainBinding;
    private lateinit var libroadapter: LibroAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        buildList()
        addlisteners()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {//Search methods
        menuInflater.inflate(R.menu.menu_main, menu)

        val item = menu?.findItem(R.id.searchView_MenuMain)
        val searchView: SearchView = item?.actionView as SearchView

        searchView.imeOptions = EditorInfo.IME_ACTION_DONE

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                libroadapter.filter.filter(newText)
                return false
            }

        })

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return true
    }

    private fun buildList() {//building list

        val repository = LibroRepository.getRepository(this)

        val layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            repository.allLibros.collect { libros ->
                libroadapter = LibroAdapter(libros as ArrayList<Libro>, this@MainActivity)
                binding.rvBooks.apply {
                    adapter = libroadapter

                    setLayoutManager(layoutManager)
                }
            }
        }
    }

    private fun addlisteners() {
        binding.btnAddBook.setOnClickListener {
            startActivity(Intent(this, AddLibroActivity::class.java))
        }

    }

}