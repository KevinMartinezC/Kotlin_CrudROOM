package com.example.libreria

import ENTITIES.Autor
import ENTITIES.Libro
import REPOSITORY.LibroRepository
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.libreria.databinding.ActivityUpdateBookBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList


class UpdateBook : AppCompatActivity() {
    lateinit var photoFile: File
    private lateinit var binding: ActivityUpdateBookBinding
    private var autorSelected: Autor? = null
    var key: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_book)
        binding = ActivityUpdateBookBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getbook()
        addListener()
        buildSpinner()
        addpicture()

    }

    private fun getbook() {
        var bundle = intent.extras!!
        key = bundle.getInt("id")
        // Get Repository
        val repository = LibroRepository.getRepository(this)

        lifecycleScope.launch {
            repository.allLibros.collect { libros ->
                libros.forEach() {
                    if (it.id == key) {
                        fillbook(it)

                    }
                }
            }
        }
    }

    private fun fillbook(libro: Libro) {
        binding.ISBN.setText(libro.ISBN)
        binding.titulo.setText(libro.Titulo)
        binding.categoria.setText(libro.Categoria)
        val bitmap = BitmapFactory.decodeFile(libro.imagen)
        binding.imageupdate.setImageBitmap(bitmap)


    }

    private fun addListener() {
        val repository = LibroRepository.getRepository(this)
        binding.addDatos.setOnClickListener {
            startActivity(Intent(this, AddAutorActivity::class.java))
        }
        binding.imgDelete.setOnClickListener{//giving SetOnClickListener to the deleteOneAutor
            deleteOneAuthor()
        }
        binding.registrar.setOnClickListener {
            hideKeyboard()
            with(binding) {
                if (ISBN.text.isBlank() || titulo.text.isBlank() || categoria.text.isBlank()) {
                    showSnack("Complete todos los campos")
                } else {
                    val isbnPattern =
                        Pattern.compile("^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?\$)[\\d-]+\$")
                    val isValid = isbnPattern.matcher(binding.ISBN.text).matches()

                    if (isValid) {
                        val tituloPatter =
                            Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$")
                        val isValidTitle = tituloPatter.matcher(binding.titulo.text).matches()

                        if (isValidTitle) {
                            val categoryPattern = Pattern.compile("^[a-zA-Z]+\$")
                            val isValidCategory =
                                categoryPattern.matcher(binding.categoria.text).matches()

                            if (isValidCategory) {
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        repository.update(
                                            Libro(
                                                id = key,
                                                ISBN = ISBN.text.toString(),
                                                Titulo = titulo.text.toString(),
                                                Categoria = categoria.text.toString(),
                                                autor = autorSelected,
                                                imagen = photoFile.absolutePath
                                            )
                                        )
                                    }
                                    onBackPressed()
                                }
                            } else {
                                val InvalidCategory = getString(R.string.InvalidCategory)
                                showSnack(InvalidCategory)
                            }
                        } else {
                            val InvalidTitle = getString(R.string.InvalidBookTitle)
                            showSnack(InvalidTitle)
                        }
                    } else {
                        val InvalidISBN = getString(R.string.InvalidISBN)
                        showSnack(InvalidISBN)
                    }

                }
            }
        }
    }


    private fun buildSpinner() {
        val repository = LibroRepository.getRepository(this)
        lifecycleScope.launch {
            repository.autor.collect { libros ->
                var AuthorNames = ArrayList<String>()
                libros.forEach {
                    AuthorNames.add(it.escritor) }
                binding.spAutor.apply {
                    adapter = ArrayAdapter(
                        this@UpdateBook,
                        R.layout.support_simple_spinner_dropdown_item,
                        AuthorNames
                    )
                    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long,
                        ) {
                            autorSelected = libros[position]
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {}
                    }
                }
                println(libros)
            }
        }
    }
    private fun deleteOneAuthor() {
        val getAutorName = binding.spAutor.selectedItem?.toString()
        val repository = LibroRepository.getRepository(this)
        CoroutineScope(Dispatchers.IO).launch {
            if (getAutorName != null) {
                repository.deleteOneAuthor(getAutorName)
            }
        }
    }

    private fun hideKeyboard() {
        val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    private fun addpicture() {


        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    if (result.data?.data != null) {
                        binding.imageupdate.setImageURI(result.data?.data)
                        binding.imageupdate.rotation = 0f
                    } else {
                        println(photoFile.absolutePath)
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        binding.imageupdate.setImageBitmap(bitmap)
                        binding.imageupdate.rotation = 90f
                    }
                }
            }

        binding.imageupdate.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            photoFile = generateFile()
            val fileProvider = FileProvider.getUriForFile(this, AUTHORITY, photoFile)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
            resultLauncher.launch(cameraIntent)
        }
    }

    private fun generateFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", SUFFIX, storageDirectory)
    }

    private fun showSnack(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()

    }

}