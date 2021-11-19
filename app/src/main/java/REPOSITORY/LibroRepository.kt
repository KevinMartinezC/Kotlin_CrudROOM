package REPOSITORY

import android.content.Context
import DAO.AutorInfDao
import DAO.LibroDao
import DATABASE.LibroRoomDatabase
import ENTITIES.Autor
import ENTITIES.Libro
import kotlinx.coroutines.flow.Flow

class LibroRepository(private val libroDao: LibroDao, private val autorInfDao: AutorInfDao) {
    companion object {
        private var INSTANCE: LibroRepository? = null
        fun getRepository(context: Context): LibroRepository {
            return INSTANCE ?: synchronized(this) {
                val database = LibroRoomDatabase.getDatabase(context)
                val instance = LibroRepository(database.libroDao(), database.autorinfDao())
                INSTANCE = instance
                instance
            }
        }
    }

    val allLibros: Flow<List<Libro>> = libroDao.getAlphabetizedLibros()

    suspend fun insert(libro: Libro) {//insert book
        libroDao.insert(libro)
    }

    //Book Dao
    val autor: Flow<List<Autor>> = autorInfDao.getAutor()
    suspend fun insertAutor(autor: Autor) {//insert author
        autorInfDao.insert(autor)
    }

    suspend fun deleteOneAuthor(autor: String) {//delete one Autor
        autorInfDao.deleteOneAuthor(autor)
    }

    suspend fun deleteOneBook(id: Int) {//delete one book
        libroDao.deleteOneBook(id)
    }

    suspend fun update(libro: Libro) {//update book information
        libroDao.update(libro)
    }

}