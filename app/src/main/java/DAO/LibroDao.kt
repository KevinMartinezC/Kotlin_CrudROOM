package DAO

import ENTITIES.Libro
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LibroDao {
    @Query("SELECT * FROM libreria_table ORDER BY title ASC")
    fun getAlphabetizedLibros(): Flow<List<Libro>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(libro: Libro)

    @Query("DELETE FROM libreria_table WHERE  id=:id")
    suspend fun deleteOneBook(id: Int)

    @Update
    suspend fun update(libro: Libro)


}
