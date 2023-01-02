package com.bagaspardanailham.notein.ui.note

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagaspardanailham.notein.data.NoteInRepository
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import com.bumptech.glide.Glide.init
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteInRepository: NoteInRepository) : ViewModel() {

    private var _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var _notes: MutableStateFlow<List<NoteEntity>> = MutableStateFlow(listOf())
    val note: StateFlow<List<NoteEntity>> = _notes.asStateFlow()

    private var _searchedNotes: MutableStateFlow<List<NoteEntity>> = MutableStateFlow(listOf())
    val searchedNotes: StateFlow<List<NoteEntity>> = _searchedNotes.asStateFlow()

    init {
        getAllNotes()
    }

    private fun getAllNotes() {
        viewModelScope.launch {
            _isLoading.emit(true)
            noteInRepository.getAllNotes()
                .flowOn(Dispatchers.IO)
                .collect {
                    _notes.emit(it)
                    _isLoading.emit(false)
                }
        }
    }
    fun getNoteByQuery(q: String?) {
        viewModelScope.launch {
            _isLoading.emit(true)
            noteInRepository.getNoteByQuery(q)
                .flowOn(Dispatchers.IO)
                .collect {
                    _searchedNotes.emit(it)
                    _isLoading.emit(false)
                }
        }
    }

}