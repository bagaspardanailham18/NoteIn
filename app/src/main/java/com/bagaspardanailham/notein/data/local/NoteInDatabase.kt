package com.bagaspardanailham.notein.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import com.bagaspardanailham.notein.data.local.model.NoteEntity

@Database(entities = [NoteEntity::class, LinkEntity::class], version = 1)
abstract class NoteInDatabase: RoomDatabase() {

    abstract fun noteDao(): NoteInDao

    companion object {
        @Volatile
        private var INSTANCE: NoteInDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): NoteInDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    NoteInDatabase::class.java, "notein_database"
                )
                    .build()
                    .also {
                        INSTANCE = it
                    }
            }
        }
    }

}