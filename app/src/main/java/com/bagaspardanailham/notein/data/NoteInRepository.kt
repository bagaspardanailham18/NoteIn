package com.bagaspardanailham.notein.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bagaspardanailham.notein.data.local.NoteInDatabase
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteInRepository @Inject constructor(private val noteInDatabase: NoteInDatabase) {

    suspend fun insertNote(note: NoteEntity) {
        noteInDatabase.noteDao().insertNote(note)
    }

    suspend fun updateNote(note: NoteEntity) {
        noteInDatabase.noteDao().updateNote(note)
    }

    suspend fun deleteNote(note: NoteEntity?) {
        noteInDatabase.noteDao().deleteNote(note)
    }

    fun getAllNotes(): LiveData<List<NoteEntity>> {
        return noteInDatabase.noteDao().getAllNote()
    }

    fun getNoteByQuery(q: String?): LiveData<List<NoteEntity>> {
        return noteInDatabase.noteDao().getNoteByQuery(q)
    }

    suspend fun insertLink(link: LinkEntity) {
        noteInDatabase.noteDao().insertLink(link)
    }

    suspend fun updateLink(link: LinkEntity) {
        noteInDatabase.noteDao().updateLink(link)
    }

    suspend fun deleteLink(link: LinkEntity) {
        noteInDatabase.noteDao().deleteLink(link)
    }

    fun getAllLinks(): LiveData<List<LinkEntity>> {
        return noteInDatabase.noteDao().getAllLink()
    }

    fun getLinkByQuery(q: String?): LiveData<List<LinkEntity>> {
        return noteInDatabase.noteDao().getLinkByQuery(q)
    }

//    fun deleteNoteById(id: Int) = liveData<String> {
//        try {
//            noteInDatabase.noteDao().deleteNoteById(id)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

}