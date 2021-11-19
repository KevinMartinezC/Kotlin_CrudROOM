package DAO

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ENTITIES.Autor
import kotlinx.coroutines.flow.Flow

@Dao
interface AutorInfDao {
        @Query("SELECT * FROM autor_table")
        fun getAutor(): Flow<List<Autor>>
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(autor: Autor)
        @Query("DELETE FROM autor_table WHERE autor= :autor")
        suspend fun deleteOneAuthor(autor: String)
}