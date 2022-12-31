package com.bagaspardanailham.notein.ui.note

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bagaspardanailham.notein.data.NoteInRepository
import com.bagaspardanailham.notein.data.local.model.NoteEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(private val noteInRepository: NoteInRepository) : ViewModel() {

    var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    suspend fun getAllNotes(): LiveData<List<NoteEntity>> = noteInRepository.getAllNotes()
    suspend fun getNoteByQuery(q: String?): LiveData<List<NoteEntity>> = noteInRepository.getNoteByQuery(q)

}