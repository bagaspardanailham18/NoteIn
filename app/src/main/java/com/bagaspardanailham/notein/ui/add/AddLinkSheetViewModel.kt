package com.bagaspardanailham.notein.ui.add

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AddLinkSheetViewModel: ViewModel() {

    private var _link = MutableLiveData<String?>()
    val link: LiveData<String?> = _link

    fun addLink(link: String) {
        _link.value = link
    }

    fun clearLink() {
        _link.value = null
    }

}