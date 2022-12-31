package com.bagaspardanailham.notein.ui.link

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bagaspardanailham.notein.data.NoteInRepository
import com.bagaspardanailham.notein.data.local.model.LinkEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LinkViewModel @Inject constructor(private val noteInRepository: NoteInRepository) : ViewModel() {

    var _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    suspend fun getAllLinks(): LiveData<List<LinkEntity>> = noteInRepository.getAllLinks()
    suspend fun getLinkByQuery(q: String?): LiveData<List<LinkEntity>> = noteInRepository.getLinkByQuery(q)

}