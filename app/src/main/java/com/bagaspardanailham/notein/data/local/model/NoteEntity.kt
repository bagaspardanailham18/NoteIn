package com.bagaspardanailham.notein.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String? = null,

    @ColumnInfo(name = "description")
    var description: String? = null,

    @ColumnInfo(name = "image")
    var image: String? = null,

    @ColumnInfo(name = "link")
    var link: String? = null,

    @ColumnInfo(name = "dateCreated")
    var dateCreated: String? = null
)
