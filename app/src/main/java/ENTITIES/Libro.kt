package ENTITIES

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "libreria_table")
data class Libro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "isbn")
    val ISBN: String,
    @ColumnInfo(name = "title")
    val Titulo: String,
    @ColumnInfo(name = "category")
    val Categoria: String,
    @ColumnInfo(name = "image")
    val imagen: String,
    @Embedded
    val autor: Autor?,
)
