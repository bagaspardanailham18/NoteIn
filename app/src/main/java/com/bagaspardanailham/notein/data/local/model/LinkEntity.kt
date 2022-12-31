package com.bagaspardanailham.notein.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "link")
data class LinkEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "urlName")
    var urlName: String? = null,

    @ColumnInfo(name = "urlLink")
    var urlLink: String? = null

)
