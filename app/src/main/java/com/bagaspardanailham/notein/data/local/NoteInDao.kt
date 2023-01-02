package com.bagaspardanailham.notein.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteInDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertNote(note: NoteEntity)

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Query("SELECT * FROM note")
    fun getAllNote(): List<NoteEntity>

    @Query("SELECT * FROM note WHERE title LIKE '%' || :q || '%'")
    fun getNoteByQuery(q: String?): List<NoteEntity>

    @Delete
    suspend fun deleteNote(note: NoteEntity?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertLink(link: LinkEntity)

    @Update
    suspend fun updateLink(link: LinkEntity)

    @Query("SELECT * FROM link")
    fun getAllLink(): LiveData<List<LinkEntity>>

    @Query("SELECT * FROM link WHERE urlName LIKE '%' || :q || '%'")
    fun getLinkByQuery(q: String?): LiveData<List<LinkEntity>>

    @Delete
    suspend fun deleteLink(link: LinkEntity)

}