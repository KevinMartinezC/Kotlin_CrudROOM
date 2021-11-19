package DATABASE

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import DAO.AutorInfDao
import DAO.LibroDao
import ENTITIES.Libro
import ENTITIES.Autor

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [Libro::class, Autor::class], version = 1, exportSchema = false)
abstract class LibroRoomDatabase :RoomDatabase() {
    abstract fun libroDao(): LibroDao
    abstract fun autorinfDao(): AutorInfDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: LibroRoomDatabase? = null
        fun getDatabase(context: Context): LibroRoomDatabase{

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LibroRoomDatabase::class.java,
                    "libros_database"
                ).build()
                INSTANCE = instance

                instance
            }
        }
    }
}