package com.bagaspardanailham.notein.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagaspardanailham.notein.data.NoteInRepository
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddUpdateViewModel @Inject constructor(private val noteInRepository: NoteInRepository): ViewModel() {

    fun insertNote(note: NoteEntity) {
        viewModelScope.launch {
            noteInRepository.insertNote(note)
        }
    }

    fun updateNote(note: NoteEntity) {
        viewModelScope.launch {
            noteInRepository.updateNote(note)
        }
    }

    fun deleteNote(note: NoteEntity?) {
        viewModelScope.launch {
            noteInRepository.deleteNote(note)
        }
    }

    fun insertLink(link: LinkEntity) {
        viewModelScope.launch {
            noteInRepository.insertLink(link)
        }
    }

    fun updateLink(link: LinkEntity) {
        viewModelScope.launch {
            noteInRepository.updateLink(link)
        }
    }

    fun deleteLink(link: LinkEntity) {
        viewModelScope.launch {
            noteInRepository.deleteLink(link)
        }
    }

}