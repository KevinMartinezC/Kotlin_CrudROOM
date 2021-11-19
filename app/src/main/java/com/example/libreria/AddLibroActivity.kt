package com.example.libreria

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.example.libreria.databinding.ActivityAddLibroBinding
import ENTITIES.Libro
import ENTITIES.Autor
import REPOSITORY.LibroRepository
import android.graphics.BitmapFactory
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
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
const val AUTHORITY = "com.example.libreria.fileprovider"
const val SUFFIX = ".jpg"

class AddLibroActivity : AppCompatActivity() {
    lateinit var photoFile: File
    private lateinit var binding: ActivityAddLibroBinding
    private var autorSelected: Autor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            binding = ActivityAddLibroBinding.inflate(layoutInflater)
            setContentView(binding.root)
            addListener()
            buildSpinner()
            addpicture()

    }
    private fun addListener(){
        val repository = LibroRepository.getRepository(this)
        binding.addDatos.setOnClickListener {
            startActivity(Intent(this, AddAutorActivity::class.java))
        }
        binding.imgDelete.setOnClickListener{//giving SetOnClickListener to the deleteOneAutor
            deleteOneAuthor()
        }
        binding.registrar.setOnClickListener {
            hideKeyboard()
            with(binding){
                if (ISBN.text.isBlank() || escritor.text.isBlank() || categoria.text.isBlank()){
                   showSnack("Complete todos los campos")
                } else {
                    val isbnPattern = Pattern.compile("^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?\$)[\\d-]+\$")
                    val isValid = isbnPattern.matcher(binding.ISBN.text).matches()
                    if(isValid){
                        val tituloPatter = Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$")
                        val isValidTitle = tituloPatter.matcher(binding.escritor.text).matches()
                        if(isValidTitle){
                            val categoryPattern = Pattern.compile("^[a-zA-Z]+\$")
                            val isValidCategory = categoryPattern.matcher(binding.categoria.text).matches()
                            if(isValidCategory){
                                lifecycleScope.launch{
                                    withContext(Dispatchers.IO){
                                        repository.insert(
                                            Libro(
                                                ISBN = ISBN.text.toString(),
                                                Titulo = escritor.text.toString(),
                                                Categoria = categoria.text.toString(),
                                                autor = autorSelected,
                                                imagen = photoFile.absolutePath
                                            )
                                        )
                                    }
                                    onBackPressed()
                                }
                            }else{
                                val InvalidCategory = getString(R.string.InvalidCategory)
                                showSnack(InvalidCategory)
                            }
                        }else{
                            val InvalidTitle = getString(R.string.InvalidBookTitle)
                            showSnack(InvalidTitle)
                        }
                    }
                    else{
                        val InvalidISBN= getString(R.string.InvalidISBN)
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
                var autornames = ArrayList<String>()
                libros.forEach {
                    autornames.add(it.escritor)
                }
                binding.spAutor.apply {
                    adapter = ArrayAdapter(
                        this@AddLibroActivity,
                        R.layout.support_simple_spinner_dropdown_item,
                        autornames
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
                        binding.addPictureImg.setImageURI(result.data?.data)
                        binding.addPictureImg.rotation = 90f
                    } else {
                        println(photoFile.absolutePath)
                        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                        binding.addPictureImg.setImageBitmap(bitmap)
                        binding.addPictureImg.rotation = 90f
                    }
                }
            }

        binding.addPictureImg.setOnClickListener {
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
