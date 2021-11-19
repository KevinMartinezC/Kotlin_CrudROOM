package ENTITIES

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "autor_table")
data class Autor(
    @PrimaryKey(autoGenerate = true)
    val autorId: Int = 0,
    @ColumnInfo(name = "autor")
    val escritor: String,
    @ColumnInfo(name = "description")
    val descripcion: String,
    @ColumnInfo(name = "born date")
    val borndate: String
)
