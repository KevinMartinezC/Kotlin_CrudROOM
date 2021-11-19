package com.example.libreria

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import com.example.libreria.databinding.ActivityAddAutorBinding
import ENTITIES.Autor
import REPOSITORY.LibroRepository
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

class AddAutorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddAutorBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddAutorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        addListeners()
    }

    private fun addListeners() {
        val repository = LibroRepository.getRepository(this)
        binding.btnAddAutor.setOnClickListener {
            hideKeyboard()
            with(binding) {
                if (edAutor.text.isBlank() || edBorn.text.isBlank() || edDescrip.text.isBlank()) {
                    val complete = getString(R.string.completetext)//getting text from string resourceS
                    Snackbar.make(this.root, complete, Snackbar.LENGTH_SHORT).show()
                } else {
                    val AutorPatter = Pattern.compile("^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*\$")//Autor Validation
                    val isValidAutor = AutorPatter.matcher(binding.edAutor.text).matches()
                    if (isValidAutor) {
                        val BornDatePatter =
                            Pattern.compile("^([0-2][0-9]|3[0-1])(\\/|-)(0[1-9]|1[0-2])\\2(\\d{4})\$")//BornDate Validation
                        val isValidBornDate = BornDatePatter.matcher(binding.edBorn.text).matches()
                        if (isValidBornDate) {
                            val DescriptionPatter =
                                Pattern.compile("[^\\r\\n]+((\\r|\\n|\\r\\n)[^\\r\\n]+)*")
                            val isValidDescription =
                                DescriptionPatter.matcher(binding.edDescrip.text).matches()
                            if (isValidDescription) {
                                lifecycleScope.launch {
                                    withContext(Dispatchers.IO) {
                                        repository.insertAutor(
                                            Autor(
                                                escritor = edAutor.text.toString(),
                                                descripcion = edDescrip.text.toString(),
                                                borndate = edBorn.text.toString()
                                            )
                                        )
                                    }
                                    onBackPressed()
                                }
                            } else {
                                val DescriptionError = getString(R.string.correctDescripcion)
                                showSnack(DescriptionError)
                            }
                        } else {
                            val BornDateInvalid = getString(R.string.BornDateCorrect)
                            showSnack(BornDateInvalid)
                        }
                    } else {
                        val AutorInvalid = getString(R.string.AutorNameCorrect)
                        showSnack(AutorInvalid)
                    }
                }
            }
        }

    }
    private fun showSnack(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
    }

    private fun hideKeyboard() {
        val manager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}