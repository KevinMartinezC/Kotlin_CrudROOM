package com.example.libreria

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.libreria.databinding.ItemLibroBinding
import ENTITIES.Libro
import REPOSITORY.LibroRepository
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Filter
import android.widget.Filterable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Suppress("MemberVisibilityCanBePrivate")
class LibroAdapter( list: ArrayList<Libro>, private val context: Context) :
    RecyclerView.Adapter<LibroAdapter.LibrosViewHolder>(), Filterable{

    private val mainList = list
    private  val searchList =ArrayList<Libro>(list)

    class LibrosViewHolder(val binding: ItemLibroBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LibrosViewHolder {
        val binding = ItemLibroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LibrosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LibrosViewHolder, position: Int) {
        with(holder.binding) {
            lbIsbn.text = mainList[position].ISBN
            lbTitulo.text = mainList[position].Titulo
            lbCat.text = mainList[position].Categoria
            lbAutor.text = "${mainList[position].autor?.escritor} \n " +
                    "${mainList[position].autor?.descripcion} \n" +
                    "${mainList[position].autor?.borndate}"
            val bitmap = BitmapFactory.decodeFile(mainList[position].imagen)
            IVBook.setImageBitmap(bitmap)


            imgDelete.setOnClickListener {
                val repository = LibroRepository.getRepository(context)
                CoroutineScope(Dispatchers.IO).launch {
                    repository.deleteOneBook(mainList[position].id)
                }
            }
            bookItem.setOnClickListener {
                val intent = Intent(context, UpdateBook::class.java)
                intent.putExtra("id", mainList[position].id)
                context.startActivity(intent)
            }


        }


    }


    override fun getItemCount(): Int = mainList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constrainst: CharSequence): FilterResults {
                val filteredList = ArrayList<Libro>()

                if (constrainst.isNullOrBlank() or constrainst.isEmpty()) {
                    filteredList.addAll(searchList)
                } else {
                    val filtePattern = constrainst.toString().lowercase().trim()
                    searchList.forEach {
                        if (it.Titulo.lowercase().contains(filtePattern)) {
                            filteredList.add(it)
                        }
                    }
                }

                val result = FilterResults()
                result.values = filteredList

                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                mainList.clear()
                mainList.addAll(results!!.values as List<Libro>)
                notifyDataSetChanged()
            }

        }
    }


}


